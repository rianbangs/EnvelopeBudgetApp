package com.example.envelopebudgetapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.envelopebudgetapp.R;
import com.example.envelopebudgetapp.model.EnvelopDetailModel;
import com.example.envelopebudgetapp.model.IncomeModel;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class IncomeAdapter extends ArrayAdapter<IncomeModel>
{
    private Context context;
    private List<IncomeModel> incomeModelList;

    public IncomeAdapter(Context context, List<IncomeModel> incomeModelList)
    {
        super(context, 0, incomeModelList);
        this.context = context;
        this.incomeModelList = incomeModelList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_income, parent, false);
        }

        // Get the data item for this position
        IncomeModel incomeDetail = getItem(position);


        TextView textDescription = convertView.findViewById(R.id.textDescription);
        TextView textAmount = convertView.findViewById(R.id.textAmount);
        TextView textDate = convertView.findViewById(R.id.textDate);
        Button buttonEdit = convertView.findViewById(R.id.editIncBtn);
        Button buttonDelete = convertView.findViewById(R.id.deleteIncBtn);

        textDescription.setText(incomeDetail.getIncomeDetail());
        textAmount.setText(formatIncome(incomeDetail.getIncome()));

        // Format the date to your desired format
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy");
        try {
            Date date = inputFormat.parse(incomeDetail.getIncomeDate());
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
                if (context instanceof IncomeAdapter.OnEditDeleteButtonClickListener) {
                    ((IncomeAdapter.OnEditDeleteButtonClickListener) context).onEditButtonClick(incomeDetail);
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement delete action
                if (context instanceof IncomeAdapter.OnEditDeleteButtonClickListener) {
                    ((IncomeAdapter.OnEditDeleteButtonClickListener) context).onDeleteButtonClick(incomeDetail);
                }
            }
        });

        // Return the completed view to render on screen
        return convertView;

    }

    private String formatIncome(float income)
    {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(income);
    }

    // Define an interface for edit and delete button clicks
    public interface OnEditDeleteButtonClickListener
    {
        void onEditButtonClick(IncomeModel incomeModel);
        void onDeleteButtonClick(IncomeModel incomeModel);
    }
}
