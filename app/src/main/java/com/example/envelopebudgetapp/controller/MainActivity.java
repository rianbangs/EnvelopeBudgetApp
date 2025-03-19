package com.example.envelopebudgetapp.controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.envelopebudgetapp.adapter.BudgetAdapter;
import com.example.envelopebudgetapp.adapter.IncomeAdapter;
import com.example.envelopebudgetapp.fragment.DatePickerFragment;
import com.example.envelopebudgetapp.helper.DataBaseHelper;
import com.example.envelopebudgetapp.model.EnvelopDetailModel;
import com.example.envelopebudgetapp.model.EnvelopModel;
import com.example.envelopebudgetapp.model.IncomeModel;
import com.example.envelopebudgetapp.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IncomeAdapter.OnEditDeleteButtonClickListener {
    //references to buttons and other controls on the layout
    Button AddIncomeBtn;
    EditText editTextNumberDecimal,IncomeDate,IncomeDetail;
    ListView budget_list;

    TextView remainingIncome;
    DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);

    private List<IncomeModel> incomeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        AddIncomeBtn = findViewById(R.id.AddIncomeBtn);
//        editTextNumberDecimal = findViewById(R.id.editTextNumberDecimal);
//        IncomeDate = findViewById(R.id.IncomeDate);
//        IncomeDetail = findViewById(R.id.IncomeDetail);
        budget_list = findViewById(R.id.budget_list);
        remainingIncome = findViewById(R.id.remainingIncome);

        displayIncomeList();

        // Button listeners
        AddIncomeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addIncome();
            }
        });



        budget_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Retrieve the IncomeModel object associated with the clicked item
                IncomeModel clickedIncome = incomeList.get(position);

//                // Extract the ID from the clicked IncomeModel object
//                int clickedId = clickedIncome.getId();
//
//                // Use the clickedId as needed
//                Toast.makeText(MainActivity.this, "Clicked ID: " + clickedId, Toast.LENGTH_SHORT).show();

               //  Build the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to delete this income?");
                builder.setCancelable(true);

                // Add buttons for confirmation and cancellation
                builder.setPositiveButton(
                        "Delete",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                // User clicked Delete button, proceed with deletion
                                dataBaseHelper.deleteIncome(clickedIncome);
                                displayIncomeList();
                                displayDialog("Income Deleted Successfully");
                            }
                        });

                builder.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                // User clicked Cancel button, dismiss the dialog
                                dialog.dismiss();
                            }
                        });

                // Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();



            }
        });

    }

    public void homeView(View v)
    {
        Intent i =  new Intent(this, HomeActivity.class);
        startActivity(i);
    }

    private void clearIncomeForm()
    {
        editTextNumberDecimal.setText(""); // Clear the value of editTextNumberDecimal
        IncomeDate.setText(""); // Clear the value of IncomeDate
        IncomeDetail.setText(""); // Clear the value of IncomeDetail
    }

    // Helper method to extract income values from the list of IncomeModel objects
    private List<String> getIncomeValues(List<IncomeModel> incomeList)
    {
        // Sort the incomeList based on the date in descending order
        Collections.sort(incomeList, new Comparator<IncomeModel>()
        {
            @Override
            public int compare(IncomeModel income1, IncomeModel income2)
            {
                // Parse the date strings to Date objects for comparison
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = dateFormat.parse(income1.getIncomeDate());
                    Date date2 = dateFormat.parse(income2.getIncomeDate());
                    // Compare the dates in descending order
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        List<String> incomeValues = new ArrayList<>();


        for (IncomeModel income : incomeList)
        {
            String dateString = income.getIncomeDate();
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
            incomeValues.add(
                    String.valueOf(formatIncome(income.getIncome())) + "    " + dateString
            );
        }
        return incomeValues;
    }

    private void displayIncomeList() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        incomeList = dataBaseHelper.getIncomeList();

        // Create a custom adapter to display only the income value
//        ArrayAdapter<String> incomeAdapter = new ArrayAdapter<String>(
//                MainActivity.this,
//                android.R.layout.simple_list_item_1,
//                getIncomeValues(incomeList)
//        );
//        budget_list.setAdapter(incomeAdapter);
        sortEnvelopDetailListByDate(incomeList);
        IncomeAdapter adapter = new  IncomeAdapter(this,incomeList);
        budget_list.setAdapter(adapter);


        // Get the total income amount
        float totalIncomeAmount = dataBaseHelper.getTotalIncomeAmount();
        // Set the total income amount to the remainingIncome TextView
        remainingIncome.setText(formatIncome(totalIncomeAmount));

    }

    private void sortEnvelopDetailListByDate(List<IncomeModel> incomeList) {
        Collections.sort(incomeList, new Comparator<IncomeModel>() {
            @Override
            public int compare(IncomeModel inc1, IncomeModel inc2) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date1 = dateFormat.parse(inc1.getIncomeDate());
                    Date date2 = dateFormat.parse(inc2.getIncomeDate());
                    return date2.compareTo(date1); // Descending order
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }

    private String formatIncome(float income)
    {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(income);
    }


    private void addIncome()
    {
//        IncomeModel incomeModel;
//        try {
//            // Get values from EditText and Date objects
//            float incomeAmount = Float.parseFloat(editTextNumberDecimal.getText().toString());
//            String incomeDate = IncomeDate.getText().toString();
//            String incomeDetail = IncomeDetail.getText().toString();
//
//            // Create IncomeModel object with the obtained values
//            incomeModel = new IncomeModel(-1, incomeAmount, incomeDate, incomeDetail);
//          //  Toast.makeText(MainActivity.this, incomeModel.toString(), Toast.LENGTH_SHORT).show();
//        } catch(Exception e) {
//            Toast.makeText(MainActivity.this, "Error adding income", Toast.LENGTH_SHORT).show();
//            incomeModel = new IncomeModel(-1,0,"0","error");
//        }
//
//        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
//        boolean success = dataBaseHelper.addIncome(incomeModel);
//
//        displayDialog("Income Successfully Added!!");
//        clearIncomeForm();
//        // Update the income list after adding a new income
//        displayIncomeList();

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_income, null);
        // Get the input fields
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        EditText incomeAmount = dialogView.findViewById(R.id.incomeAmount);

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



        // Overriding the onClick of the positive button to handle validation and submission
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input values
                String description = editTextDescription.getText().toString();
                String amountString = incomeAmount.getText().toString();
                String date = editTextDate.getText().toString();

                // Validate the inputs
                if (description.isEmpty() || amountString.isEmpty() || date.isEmpty())
                {
                    displayDialog("Please fill all fields");
                }
                else
                {
                    try
                    {
                        IncomeModel incomeModel;
                        float amount = Float.parseFloat(amountString);
                        incomeModel = new IncomeModel(-1, amount, date, description);
                        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
                        boolean success = dataBaseHelper.addIncome(incomeModel);
                        if (success) {
                            displayIncomeList();
                            displayDialog("Entry Successfully Added!!");
                            dialog.dismiss();

                            // Show a success message
                            // Toast.makeText(EnvelopeActivity.this, "Detail added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Error adding detail", Toast.LENGTH_SHORT).show();
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



    private void  displayDialog(String message)
    {
        AlertDialog.Builder msg = new AlertDialog.Builder(MainActivity.this);
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
    public void onEditButtonClick(IncomeModel incomeModel)
    {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_income, null);
        // Get the input fields
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        EditText incomeAmount = dialogView.findViewById(R.id.incomeAmount);

        editTextDescription.setText(incomeModel.getIncomeDetail());
        editTextDate.setText(incomeModel.getIncomeDate());
        incomeAmount.setText(String.valueOf(incomeModel.getIncome()));

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
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirm Update")
                        .setMessage("Are you sure you want to update this entry?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogConfirm, int which) {
                                // Update the envelopDetail object with new values
                                incomeModel.setIncomeDetail(editTextDescription.getText().toString());
                                incomeModel.setIncome(Float.parseFloat(incomeAmount.getText().toString()));
                                incomeModel.setIncomeDate(editTextDate.getText().toString());

                                // Call the update function
                                boolean updated = dataBaseHelper.updateIncome(incomeModel);
                                if (updated) {
                                    displayDialog("Entry Successfully Updated!!");
                                    dialog.dismiss(); // Close the outer dialog
                                    displayIncomeList();
                                } else {
                                    Toast.makeText(MainActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

    }

    @Override
    public void onDeleteButtonClick(IncomeModel incomeModel)
    {
        //  Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want to delete this income?");
        builder.setCancelable(true);

        // Add buttons for confirmation and cancellation
        builder.setPositiveButton(
                "Delete",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // User clicked Delete button, proceed with deletion
                        dataBaseHelper.deleteIncome(incomeModel);
                        displayIncomeList();
                        displayDialog("Income Deleted Successfully");
                    }
                });

        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // User clicked Cancel button, dismiss the dialog
                        dialog.dismiss();
                    }
                });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}