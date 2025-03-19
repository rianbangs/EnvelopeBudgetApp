package com.example.envelopebudgetapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.envelopebudgetapp.R;
import com.example.envelopebudgetapp.model.EnvelopDetailModel;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EnvelopeDetailAdapter extends ArrayAdapter<EnvelopDetailModel>
{

    private Context context;
    private List<EnvelopDetailModel> envelopDetailList;

    public EnvelopeDetailAdapter(Context context, List<EnvelopDetailModel> envelopDetailList) {
        super(context, 0, envelopDetailList);
        this.context = context;
        this.envelopDetailList = envelopDetailList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_envelope_detail, parent, false);
        }

        // Get the data item for this position
        EnvelopDetailModel envelopDetail = getItem(position);

        // Lookup view for data population
        TextView textDescription = convertView.findViewById(R.id.textDescription);
        TextView textDetailType = convertView.findViewById(R.id.textDetailType);
        TextView textAmount = convertView.findViewById(R.id.textAmount);
        TextView textDate = convertView.findViewById(R.id.textDate);
        Button buttonEdit = convertView.findViewById(R.id.editEnvDetBtn);
        Button buttonDelete = convertView.findViewById(R.id.deleteEnvDetBtn);


        // Populate the data into the template view using the data object
        textDescription.setText(envelopDetail.getEnv_detail_description());
        textDetailType.setText(envelopDetail.getDetail_type());
        textAmount.setText(formatIncome(envelopDetail.getEnv_amount()));

        // Set the font color of textDetailType based on the detail type
        if ("Expense".equalsIgnoreCase(envelopDetail.getDetail_type())) {
            textDetailType.setTextColor(Color.RED);
        } else if ("Allocated Income".equalsIgnoreCase(envelopDetail.getDetail_type())) {
            textDetailType.setTextColor(Color.GREEN);
        } else {
            // Default color (you can set this to whatever you want)
            textDetailType.setTextColor(Color.BLACK);
        }

        // Format the date to your desired format
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy");
        try {
            Date date = inputFormat.parse(envelopDetail.getEvn_date());
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
                if (context instanceof OnEditDeleteButtonClickListener) {
                    ((OnEditDeleteButtonClickListener) context).onEditButtonClick(envelopDetail);
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement delete action
                if (context instanceof OnEditDeleteButtonClickListener) {
                    ((OnEditDeleteButtonClickListener) context).onDeleteButtonClick(envelopDetail);
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
        void onEditButtonClick(EnvelopDetailModel envelopDetail);
        void onDeleteButtonClick(EnvelopDetailModel envelopDetail);
    }
}
