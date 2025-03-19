package com.example.envelopebudgetapp.controller;

import static com.example.envelopebudgetapp.R.id.envelope_list;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.envelopebudgetapp.R;
import com.example.envelopebudgetapp.adapter.BudgetAdapter;
import com.example.envelopebudgetapp.fragment.DatePickerFragment;
import com.example.envelopebudgetapp.helper.DataBaseHelper;
import com.example.envelopebudgetapp.model.EnvelopDetailModel;
import com.example.envelopebudgetapp.model.EnvelopModel;
import com.example.envelopebudgetapp.model.IncomeModel;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements  BudgetAdapter.OnEditDeleteBrowseButtonClickListener
{
    TextView remainingIncome;
    EditText budgetName;
    ListView envelope_list;
    DataBaseHelper dataBaseHelper = new DataBaseHelper(HomeActivity.this);
    EnvelopModel envelopModel;
    private List<EnvelopModel> envelopeList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        remainingIncome = findViewById(R.id.remainingIncome);
        envelope_list = findViewById(R.id.envelope_list);
//cleaning the table----------------------------------------------
//        dataBaseHelper.truncate_ENVELOPE_DETAIL_TABLE();
//-------------------------------------------------------------------
        displayEnvelopeList();

        envelope_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EnvelopModel clickedEnvelope = envelopeList.get(position);
//                Toast.makeText(HomeActivity.this, "You clicked " + clickedEnvelope.getEnv_id(), Toast.LENGTH_SHORT).show();

                Intent i = new Intent(HomeActivity.this, EnvelopeActivity.class);
                i.putExtra("clickedEnvelope", clickedEnvelope);
                startActivity(i);
            }
        });

    }

    private String getCurrentDate()
    {
        // Create a SimpleDateFormat object with the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Get the current date
        Date currentDate = new Date();

        // Format the current date using the SimpleDateFormat object
        return sdf.format(currentDate);
    }



    public void AddBudget(View v)
    {

//        String budName = budgetName.getText().toString();
//        String dateAdded = getCurrentDate();
//        Toast.makeText(HomeActivity.this, "executed"+budName+" "+dateAdded, Toast.LENGTH_SHORT).show();
//
//        try
//        {
//            envelopModel = new EnvelopModel(-1,budName,dateAdded);
//        }
//        catch(Exception e)
//        {
//            Toast.makeText(HomeActivity.this, "Error adding income", Toast.LENGTH_SHORT).show();
//            envelopModel = new EnvelopModel(-1,"none","error");
//        }
//
//        boolean success = dataBaseHelper.addEnvelope(envelopModel);
//        displayDialog("Budget Successfully Added!!");
//        clearIncomeForm();
//        displayEnvelopeList();
        // Get the input values
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_budget, null);
        // Get the input fields
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        EditText editTextDate = dialogView.findViewById(R.id.editTextDate);



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
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input values
                String description = editTextDescription.getText().toString();
                String date = editTextDate.getText().toString();


                // Validate the inputs
                if (description.isEmpty() || date.isEmpty())
                {
                    displayDialog("Please fill all fields");
                }
                else
                {
                    try
                    {

                        envelopModel = new EnvelopModel(-1, description, date);

                        boolean success = dataBaseHelper.addEnvelope(envelopModel);
                        if (success) {
                            displayEnvelopeList();
                            displayDialog("Entry Successfully Added!!");
                            dialog.dismiss();

                        } else {
                            Toast.makeText(HomeActivity.this, "Error adding detail", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch(NumberFormatException e)
                    {
                        displayDialog("Please enter a valid amount");
                    }
                }
            }
        });

    }

    private void clearIncomeForm()
    {
        budgetName.setText("");
    }

    private void  displayDialog(String message)
    {
        AlertDialog.Builder msg = new AlertDialog.Builder(HomeActivity.this);
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

    private void remainingIncome()
    {
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

    public void incomeView(View v)
    {
        Intent i =  new Intent(this, MainActivity.class);
        startActivity(i);
    }


    private void displayEnvelopeList()
    {

       DataBaseHelper dataBaseHelper = new DataBaseHelper(HomeActivity.this);
        envelopeList = dataBaseHelper.getEnvelopeList();

//        // Create a custom adapter to display only the income value
//        ArrayAdapter<String> envAdapter = new ArrayAdapter<String>(
//                HomeActivity.this,
//                android.R.layout.simple_list_item_1,
//                getEnvelopeValues(envelopeList)
//        );

        // Sort the list before setting the adapter
        sortEnvelopDetailListByDate(envelopeList);
        BudgetAdapter adapter = new  BudgetAdapter(this,envelopeList);

        envelope_list.setAdapter(adapter);
        remainingIncome();
    }


    private void sortEnvelopDetailListByDate(List<EnvelopModel> envelopeList) {
        Collections.sort(envelopeList, new Comparator<EnvelopModel>() {
            @Override
            public int compare(EnvelopModel env1, EnvelopModel env2) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date1 = dateFormat.parse(env1.getDate_added());
                    Date date2 = dateFormat.parse(env2.getDate_added());
                    return date2.compareTo(date1); // Descending order
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }

    private List<String> getEnvelopeValues(List<EnvelopModel> envelopeList)
    {
        // Sort the incomeList based on the date in descending order
        Collections.sort(envelopeList, new Comparator<EnvelopModel>()
        {
            @Override
            public int compare(EnvelopModel env1, EnvelopModel env2)
            {
                // Parse the date strings to Date objects for comparison
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = dateFormat.parse(env1.getDate_added());
                    Date date2 = dateFormat.parse(env2.getDate_added());
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

        for (EnvelopModel env : envelopeList)
        {
            String dateString = env.getDate_added();
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
                    String.valueOf(env.getEnv_name()) + "    " + dateString
            );
//            Toast.makeText(HomeActivity.this, "executed in for loop" + add, Toast.LENGTH_SHORT).show();
        }
        return envValues;
    }

    @Override
    public void onEditButtonClick(EnvelopModel envelopModel) {

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_budget, null);
        // Get the input fields
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        EditText editTextDate = dialogView.findViewById(R.id.editTextDate);

        editTextDescription.setText(envelopModel.getEnv_name());
        editTextDate.setText(envelopModel.getDate_added());


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
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Confirm Update")
                        .setMessage("Are you sure you want to update this entry?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogConfirm, int which) {
                                // Update the envelopDetail object with new values
                                envelopModel.setEnv_name(editTextDescription.getText().toString());
                                envelopModel.setDate_added(editTextDate.getText().toString());

                                // Call the update function
                                boolean updated = dataBaseHelper.updateABudget(envelopModel);
                                if (updated) {
                                    displayDialog("Entry Successfully Updated!!");
                                    dialog.dismiss(); // Close the outer dialog
                                    displayEnvelopeList();
                                } else {
                                    Toast.makeText(HomeActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

    }



    @Override
    public void onDeleteButtonClick(EnvelopModel envelopModel) {
        new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogConfirm, int which) {
                        // Call the delete function
                        boolean deleted = dataBaseHelper.deleteABudget(envelopModel,"ENV_ID = ?");
                        dataBaseHelper.deleteEnvelope_detail(null,"ENV_ID = ?",envelopModel);
                        dataBaseHelper.clean_ENVELOPE_DETAIL_TABLE();

                        if (deleted) {
                            displayDialog("Entry Successfully Deleted!!");
                            displayEnvelopeList();
                        } else {
                            Toast.makeText(HomeActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void onBrowseButtonClick(EnvelopModel envelopModel)
    {
        Intent i = new Intent(HomeActivity.this, EnvelopeActivity.class);
        i.putExtra("clickedEnvelope", envelopModel);
        startActivity(i);
    }
}