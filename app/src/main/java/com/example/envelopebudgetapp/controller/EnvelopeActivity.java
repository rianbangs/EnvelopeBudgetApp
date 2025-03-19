package com.example.envelopebudgetapp.controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.envelopebudgetapp.R;
import com.example.envelopebudgetapp.adapter.EnvelopeDetailAdapter;
import com.example.envelopebudgetapp.fragment.DatePickerFragment;
import com.example.envelopebudgetapp.helper.DataBaseHelper;
import com.example.envelopebudgetapp.model.EnvelopDetailModel;
import com.example.envelopebudgetapp.model.EnvelopModel;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class EnvelopeActivity extends AppCompatActivity implements  EnvelopeDetailAdapter.OnEditDeleteButtonClickListener {

    private List<EnvelopDetailModel> envelopDetailList;
    DataBaseHelper dataBaseHelper = new DataBaseHelper(EnvelopeActivity.this);

    ListView EnvelopeDetailListView;
    TextView remainingIncome;
    TextView remainingAlloc;
    TextView budgetTitle;

    EnvelopModel clickedEnvelope;
    EnvelopDetailModel envelopDetailModel;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_envelope);

        remainingIncome = findViewById(R.id.remainingIncome);
        remainingAlloc  = findViewById(R.id.remainingAlloc);
        budgetTitle = findViewById(R.id.budgetTitle);
        displayEnvelopList();
    }

    public void showAddDetailDialog(View v)
    {
//        Toast.makeText(EnvelopeActivity.this, "You Load " + clickedEnvelope, Toast.LENGTH_SHORT).show();
        showAddDetailDialog();
    }

    private void showAddDetailDialog()
    {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_detail, null);

        // Get the input fields
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        Spinner spinnerDetailType = dialogView.findViewById(R.id.spinnerDetailType);
        EditText editTextAmount = dialogView.findViewById(R.id.editTextAmount);
        EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
//        Button scanButton = dialogView.findViewById(R.id.scanRreceipt);

        // Set up the spinner (dropdown)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.detail_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDetailType.setAdapter(adapter);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Add Entry")
                .setPositiveButton("Submit", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment(editTextDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        // Set onClick listener for the scan button
//        scanButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(EnvelopeActivity.this, CameraActivity.class);
//                startActivity(intent);
//            }
//        });

        // Overriding the onClick of the positive button to handle validation and submission
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input values
                String description = editTextDescription.getText().toString();
                String detailType = spinnerDetailType.getSelectedItem().toString();
                String amountString = editTextAmount.getText().toString();
                String date = editTextDate.getText().toString();
                int env_id = clickedEnvelope.getEnv_id();

                // Validate the inputs
                if (description.isEmpty() || amountString.isEmpty() || date.isEmpty())
                {
                    displayDialog("Please fill all fields");
                }
                else
                {
                    try
                    {
                        float amount = Float.parseFloat(amountString);
                        envelopDetailModel = new EnvelopDetailModel(-1, description, detailType, amount, date, env_id);

                        boolean success = dataBaseHelper.addEnvelopeDetail(envelopDetailModel);
                        if (success) {
                            displayEnvelopList();
                            displayDialog("Entry Successfully Added!!");
                            clearIncomeForm(dialogView);
                            dialog.dismiss();

                            // Show a success message
                           // Toast.makeText(EnvelopeActivity.this, "Detail added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EnvelopeActivity.this, "Error adding detail", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch(NumberFormatException e)
                    {
                        displayDialog("Please enter a valid amount");
//                        Toast.makeText(EnvelopeActivity.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }





    private void clearIncomeForm(View dialogView)
    {
        // Get the input fields
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        Spinner spinnerDetailType = dialogView.findViewById(R.id.spinnerDetailType);
        EditText editTextAmount = dialogView.findViewById(R.id.editTextAmount);
        EditText editTextDate = dialogView.findViewById(R.id.editTextDate);

        // Clear the input fields
        editTextDescription.setText("");
        editTextAmount.setText("");
        editTextDate.setText("");

        // Reset the spinner to its default position (usually the first item)
        spinnerDetailType.setSelection(0);
    }

    private void  displayDialog(String message)
    {
        AlertDialog.Builder msg = new AlertDialog.Builder( EnvelopeActivity.this);
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


    @Override
    public void onEditButtonClick(EnvelopDetailModel envelopDetail) {
        // Handle the edit action
        // For example, show a dialog with the current data to edit
        //Toast.makeText(EnvelopeActivity.this, "You Click for edit--> " + envelopDetail.getEnv_det_id(), Toast.LENGTH_SHORT).show();
        showEditDetailDialog(envelopDetail);
    }

    private void showEditDetailDialog(EnvelopDetailModel envelopDetail)
    {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_detail, null);

        // Get the input fields
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        Spinner spinnerDetailType = dialogView.findViewById(R.id.spinnerDetailType);
        EditText editTextAmount = dialogView.findViewById(R.id.editTextAmount);
        EditText editTextDate = dialogView.findViewById(R.id.editTextDate);

        editTextDescription.setText(envelopDetail.getEnv_detail_description());
        editTextAmount.setText(String.valueOf(envelopDetail.getEnv_amount()) );
        editTextDate.setText(envelopDetail.getEvn_date());


        // Set up the spinner (dropdown)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.detail_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDetailType.setAdapter(adapter);

        // Find the position of the current detail type in the array
        String detailType = envelopDetail.getDetail_type();
        int spinnerPosition = adapter.getPosition(detailType);
        spinnerDetailType.setSelection(spinnerPosition);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Edit Entry")
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment(editTextDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a confirmation dialog
                new AlertDialog.Builder(EnvelopeActivity.this)
                        .setTitle("Confirm Update")
                        .setMessage("Are you sure you want to update this entry?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogConfirm, int which) {
                                // Update the envelopDetail object with new values
                                envelopDetail.setEnv_detail_description(editTextDescription.getText().toString());
                                envelopDetail.setDetail_type(spinnerDetailType.getSelectedItem().toString());
                                envelopDetail.setEnv_amount(Float.parseFloat(editTextAmount.getText().toString()));
                                envelopDetail.setEvn_date(editTextDate.getText().toString());

                                // Call the update function
                                boolean updated = dataBaseHelper.updateOneDetail(envelopDetail);
                                if (updated) {
                                    displayDialog("Entry Successfully Updated!!");
                                    clearIncomeForm(dialogView);
                                    dialog.dismiss(); // Close the outer dialog
                                    displayEnvelopList();
                                } else {
                                    Toast.makeText(EnvelopeActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


    }

    @Override
    public void onDeleteButtonClick(EnvelopDetailModel envelopDetail) {
        new AlertDialog.Builder(EnvelopeActivity.this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogConfirm, int which) {
                        // Call the delete function
                        boolean deleted = dataBaseHelper.deleteEnvelope_detail(envelopDetail,"ENV_DET_ID = ?",null);
                        if (deleted) {
                            displayDialog("Entry Successfully Deleted!!");
                            displayEnvelopList();
                        } else {
                            Toast.makeText(EnvelopeActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    // Sort the envelopDetailList based on getEvn_date in descending order
    private void sortEnvelopDetailListByDate(List<EnvelopDetailModel> envelopDetailList) {
        Collections.sort(envelopDetailList, new Comparator<EnvelopDetailModel>() {
            @Override
            public int compare(EnvelopDetailModel env1, EnvelopDetailModel env2) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date1 = dateFormat.parse(env1.getEvn_date());
                    Date date2 = dateFormat.parse(env2.getEvn_date());
                    return date2.compareTo(date1); // Descending order
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }


    private void displayEnvelopList()
    {
        EnvelopeDetailListView = findViewById(R.id.EnvelopeDetailListView);
        clickedEnvelope = (EnvelopModel) getIntent().getSerializableExtra("clickedEnvelope");
//        Toast.makeText(EnvelopeActivity.this, "You Load " + clickedEnvelope, Toast.LENGTH_SHORT).show();
        envelopDetailList = dataBaseHelper.getEnvList(clickedEnvelope, "specific");

        // Sort the list before setting the adapter
        sortEnvelopDetailListByDate(envelopDetailList);

        // Create and set the custom adapter
        EnvelopeDetailAdapter adapter = new EnvelopeDetailAdapter(this, envelopDetailList);
        EnvelopeDetailListView.setAdapter(adapter);
        // Get the total income amount
        float totalIncomeAmount = dataBaseHelper.getTotalIncomeAmount();
        // Set the total income amount to the remainingIncome TextView
        remainingIncome.setText(formatIncome(totalIncomeAmount));

        float remainingAllocatedAmount = dataBaseHelper.getRemainingAllocation(clickedEnvelope);
        remainingAlloc.setText(formatIncome(remainingAllocatedAmount));

        budgetTitle.setText(clickedEnvelope.getEnv_name());
    }

    private String formatIncome(float income)
    {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(income);
    }

    private List<String> getEnvelopeDetailsValues(List<EnvelopDetailModel> envelopeDetailList)
    {
        // Sort the incomeList based on the date in descending order
        Collections.sort(envelopeDetailList, new Comparator<EnvelopDetailModel>()
        {
            @Override
            public int compare(EnvelopDetailModel env1, EnvelopDetailModel env2)
            {
                // Parse the date strings to Date objects for comparison
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = dateFormat.parse(env1.getEvn_date());
                    Date date2 = dateFormat.parse(env2.getEvn_date());
                    // Compare the dates in descending order
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        List<String> envValues = new ArrayList<>();
//        Toast.makeText(HomeActivity.this, "executed" , Toast.LENGTH_SHORT).show();

        for (EnvelopDetailModel env : envelopeDetailList)
        {
            String dateString = env.getEvn_date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy"); // Define your desired date format
            try
            {
                // Parse the input date string to a Date object
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
                // Format the date object to your desired format
                dateString = dateFormat.format(date);
            } catch (ParseException e)
            {
                e.printStackTrace(); // Handle parsing exceptions if needed
            }

            // Add the formatted date string to your list
            boolean add = envValues.add(
                    " "+String.valueOf(env.getEnv_detail_description()) + "    " + dateString
            );
//            Toast.makeText(HomeActivity.this, "executed in for loop" + add, Toast.LENGTH_SHORT).show();
        }
        return envValues;
    }



    public void homeView(View v)
    {
        Intent i =  new Intent(this, HomeActivity.class);
        startActivity(i);
    }
}