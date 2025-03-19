package com.example.envelopebudgetapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.envelopebudgetapp.R;
import com.example.envelopebudgetapp.helper.DataBaseHelper;
import com.example.envelopebudgetapp.model.EnvelopModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class BudgetAdapter extends ArrayAdapter<EnvelopModel>
{
    private Context context;
    private List<EnvelopModel> envelopModelList;


    public BudgetAdapter(Context context, List<EnvelopModel> envelopModelList)
    {
        super(context, 0, envelopModelList);
        this.context = context;
        this.envelopModelList = envelopModelList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_budget, parent, false);
        }

        EnvelopModel envelopModel = getItem(position);
        // Lookup view for data population
        TextView textDescription = convertView.findViewById(R.id.textDescription);
        TextView textAmount = convertView.findViewById(R.id.remainingAlloc);
        TextView textDate = convertView.findViewById(R.id.textDate);
        Button buttonEdit = convertView.findViewById(R.id.editBudgbtn);
        Button buttonDelete = convertView.findViewById(R.id.deleteBudgBtn);
        Button browseDetail =  convertView.findViewById(R.id.browseDetail);

        textDescription.setText(envelopModel.getEnv_name());

        // Create an instance of DataBaseHelper
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext());
        // Get remaining allocation
        float remainingAllocation = dataBaseHelper.getRemainingAllocation(envelopModel);
        // Set the remaining allocation in textAmount
        textAmount.setText(String.format("%.2f", remainingAllocation));


        // Format the date to your desired format
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy");
        try {
            Date date = inputFormat.parse(envelopModel.getDate_added());
            String formattedDate = outputFormat.format(date);
            textDate.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Set up click listeners for the buttons
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement edit action
                if (context instanceof OnEditDeleteBrowseButtonClickListener) {
                    ((OnEditDeleteBrowseButtonClickListener) context).onEditButtonClick(envelopModel);
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement delete action
                if (context instanceof OnEditDeleteBrowseButtonClickListener) {
                    ((OnEditDeleteBrowseButtonClickListener) context).onDeleteButtonClick(envelopModel);
                }
            }
        });

        browseDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof OnEditDeleteBrowseButtonClickListener) {
                    ((OnEditDeleteBrowseButtonClickListener) context).onBrowseButtonClick(envelopModel);
                }
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    // Define an interface for edit and delete button clicks
    public interface OnEditDeleteBrowseButtonClickListener
    {
        void onEditButtonClick(EnvelopModel envelopModel);
        void onDeleteButtonClick(EnvelopModel envelopModel);
        void onBrowseButtonClick(EnvelopModel envelopModel);
    }
}
