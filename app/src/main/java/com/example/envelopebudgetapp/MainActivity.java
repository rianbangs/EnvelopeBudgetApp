package com.example.envelopebudgetapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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
        editTextNumberDecimal = findViewById(R.id.editTextNumberDecimal);
        IncomeDate = findViewById(R.id.IncomeDate);
        IncomeDetail = findViewById(R.id.IncomeDetail);
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
        ArrayAdapter<String> incomeAdapter = new ArrayAdapter<String>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                getIncomeValues(incomeList)
        );

        budget_list.setAdapter(incomeAdapter);
        // Get the total income amount
        float totalIncomeAmount = dataBaseHelper.getTotalIncomeAmount();
        // Set the total income amount to the remainingIncome TextView
        remainingIncome.setText(formatIncome(totalIncomeAmount));

    }

    private String formatIncome(float income)
    {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(income);
    }


    private void addIncome()
    {
        IncomeModel incomeModel;
        try {
            // Get values from EditText and Date objects
            float incomeAmount = Float.parseFloat(editTextNumberDecimal.getText().toString());
            String incomeDate = IncomeDate.getText().toString();
            String incomeDetail = IncomeDetail.getText().toString();

            // Create IncomeModel object with the obtained values
            incomeModel = new IncomeModel(-1, incomeAmount, incomeDate, incomeDetail);
          //  Toast.makeText(MainActivity.this, incomeModel.toString(), Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            Toast.makeText(MainActivity.this, "Error adding income", Toast.LENGTH_SHORT).show();
            incomeModel = new IncomeModel(-1,0,"0","error");
        }

        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        boolean success = dataBaseHelper.addIncome(incomeModel);

        displayDialog("Income Successfully Added!!");
        clearIncomeForm();
        // Update the income list after adding a new income
        displayIncomeList();
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



}