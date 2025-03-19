package com.example.envelopebudgetapp.controller;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.envelopebudgetapp.R;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;


import java.io.File;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private Button buttonTakePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        buttonTakePicture = findViewById(R.id.buttonTakePicture);

        // Set click listener for the button
        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        startCamera();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // Handle any errors (including cancellation) here.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();
        imageCapture = new ImageCapture.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    private void takePicture() {
        File photoFile = new File(getFilesDir(), "receipt.jpg");

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        // Handle the saved image
                        analyzeImage(photoFile);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        // Handle the error
                    }
                });
    }

    private void analyzeImage(File photoFile) {
        try {
            // Create an InputImage object from the File
            InputImage image = InputImage.fromFilePath(this, Uri.fromFile(photoFile));

            // Get an instance of TextRecognizer
            TextRecognizer recognizer = TextRecognition.getClient();

            // Process the image
            recognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        // Task completed successfully
                        String resultText = visionText.getText();
                        if (resultText.matches(".*\\d+.*")) {
                            // Numbers found
//                            Toast.makeText(CameraActivity.this, "Numbers found in the image: " + resultText, Toast.LENGTH_SHORT).show();
                            displayDialog("Numbers found in the image: " + resultText);
                        } else {
                            // No numbers found
                           // Toast.makeText(CameraActivity.this, "No numbers found in the image", Toast.LENGTH_SHORT).show();

                            displayDialog("No numbers found in the image");
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Task failed with an exception
                        e.printStackTrace();
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void  displayDialog(String message)
    {
        AlertDialog.Builder msg = new AlertDialog.Builder( CameraActivity.this);
        msg.setMessage(message);
        AlertDialog alertSuccess = msg.create();
        alertSuccess.show();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                alertSuccess.dismiss();
            }
        }, 5000); // 5000 milliseconds = 5 seconds
    }


}
