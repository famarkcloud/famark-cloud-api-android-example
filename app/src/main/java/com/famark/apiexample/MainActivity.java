package com.famark.apiexample;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnLogin = findViewById(R.id.btnLogin);
        EditText txtDomainName = findViewById(R.id.txtDomainName);
        EditText txtUserName = findViewById(R.id.txtUserName);
        EditText txtPassword = findViewById(R.id.txtPassword);
        TextView txtMessage = findViewById(R.id.txtMessage);
        FamarkCloudAPI cloudApi = new FamarkCloudAPI(new OnAPICallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    //Using this workaround for parsing json string value to get SessionId as JSONObject cannot parse json encoded string returned in response
                    String jsonResponse = "{\"SessionId\":" + response + "}";
                    JSONObject obj = new JSONObject(jsonResponse);
                    String sessionID = obj.getString("SessionId");
                    txtMessage.setTextColor(Color.GREEN);
                    txtMessage.setText(sessionID);
                    Intent i = new Intent(MainActivity.this, ProfileCreationActivity.class);
                    i.putExtra("SessionId", sessionID);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {
                txtMessage.setTextColor(Color.RED);
                txtMessage.setText(errorMessage);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String domainName = String.valueOf(txtDomainName.getText());
                String userName = String.valueOf(txtUserName.getText());
                String password = String.valueOf(txtPassword.getText());
                HashMap<String, Object> credData = new HashMap<>();
                credData.put("DomainName", domainName);
                credData.put("UserName", userName);
                credData.put("Password", password);
                JSONObject credDataJson = new JSONObject(credData);
                String request = credDataJson.toString();
                cloudApi.execute("/Credential/Connect", request, null);
            }
        });

    }
}