package com.famark.apiexample;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.autofill.AutofillValue;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RetrieveRecords extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_records);
        Intent i = getIntent();
        String sessionID = i.getExtras().getString("SessionId");
        Button btnRetrieve = findViewById(R.id.btnRetrieve);
        TableLayout table = findViewById(R.id.table);
        FamarkCloudAPI cloudApi = new FamarkCloudAPI(new OnAPICallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray records = new JSONArray(response);
                    if(records.length() > 0) {
                        addHeaderRow(table);
                    }
                    for (int i = 0; i < records.length(); i++) {
                        JSONObject record = records.getJSONObject(i);
                        String systemName = record.getString("SystemName");
                        String displayName = record.getString("DisplayName");
                        TableRow row = new TableRow(RetrieveRecords.this);
                        TextView tvSystemName = new TextView(RetrieveRecords.this);
                        tvSystemName.setText(systemName);
                        TextView tvDisplayName = new TextView(RetrieveRecords.this);
                        tvDisplayName.setText(displayName);
                        row.addView(tvDisplayName);
                        row.addView(tvSystemName);
                        table.addView(row);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onError(String errorMessage) {
                table.autofill(AutofillValue.forText(errorMessage));
            }
        });

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> credData = new HashMap<String, Object>();
                credData.put("Columns", "DisplayName, SystemName");
                credData.put("OrderBy", "DisplayName");
                JSONObject credDataJson = new JSONObject(credData);
                String request = credDataJson.toString();
                cloudApi.execute("/System_Profile/RetrieveMultipleRecords", request, sessionID);
            }
        });
    }
    public void addHeaderRow(TableLayout table){
        TableRow headerRow = new TableRow(RetrieveRecords.this);
        TextView headerColumn1 = new TextView(RetrieveRecords.this);
        headerColumn1.setTextColor(Color.BLUE);
        headerColumn1.setTypeface(null, Typeface.BOLD_ITALIC);
        headerColumn1.setText("Display Name");
        TextView headerColumn2 = new TextView(RetrieveRecords.this);
        headerColumn2.setTextColor(Color.BLUE);
        headerColumn2.setTypeface(null, Typeface.BOLD_ITALIC);
        headerColumn2.setText("System Name");
        headerRow.addView(headerColumn1);
        headerRow.addView(headerColumn2);
        table.addView(headerRow);
    }
}