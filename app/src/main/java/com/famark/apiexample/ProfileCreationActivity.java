package com.famark.apiexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ProfileCreationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
        Intent i = getIntent();
        String sessionID = i.getExtras().getString("SessionId");
        Button btnSubmit = findViewById(R.id.btnSubmit);
        EditText txtSystemName = findViewById(R.id.txtSystemName);
        EditText txtDisplayName = findViewById(R.id.txtDisplayName);
        TextView txtMessage2 = findViewById(R.id.txtMessage2);
        FamarkCloudAPI cloudApi = new FamarkCloudAPI(new OnAPICallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    //Using this workaround for parsing json string value to get RecordId as JSONObject cannot parse json encoded string returned in response
                    String jsonResponse = "{\"RecordId\":" + response + "}";
                    JSONObject obj = new JSONObject(jsonResponse);
                    String recordID = obj.getString("RecordId");
                    txtMessage2.setTextColor(Color.GREEN);
                    txtMessage2.setText(recordID);
                    Intent i = new Intent(ProfileCreationActivity.this, RetrieveRecords.class);
                    i.putExtra("SessionId", sessionID);
                    startActivity(i);
                } catch (
                        JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(String errorMessage) {
                txtMessage2.setTextColor(Color.RED);
                txtMessage2.setText(errorMessage);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String systemName = String.valueOf(txtSystemName.getText());
                String displayName = String.valueOf(txtDisplayName.getText());
                HashMap<String, Object> credData = new HashMap<String, Object>();
                credData.put("SystemName", systemName);
                credData.put("DisplayName", displayName);
                JSONObject credDataJson = new JSONObject(credData);
                String request = credDataJson.toString();
                cloudApi.execute("/System_Profile/CreateRecord", request, sessionID);
            }
        });
    }
}