package com.famark.apiexample;

import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.RequiresApi;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class FamarkCloudAPI extends AsyncTask<String,Void,String> {

    private final OnAPICallback _callback;
    private String _errorMessage;

    public FamarkCloudAPI(OnAPICallback callback)
    {
        _callback = callback;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected String doInBackground(String... args) {
        HttpURLConnection http = null;
        try {
            //args[0] contains url suffix, args[1] contains request body, args[2] contains sessionID
            URL Url = new URL("https://www.famark.com/host/api.svc/api" + args[0]);
            http = (HttpURLConnection) Url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            byte[] out = args[1].getBytes(StandardCharsets.UTF_8);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            http.setRequestProperty("Content-Length", Integer.toString(out.length));
            String sessionID = args[2];
            if (sessionID != null && sessionID.length() > 0) {
                http.setRequestProperty("SessionId", sessionID);
            }
            try (DataOutputStream os = new DataOutputStream(http.getOutputStream())) {
                os.write(out);
            }
            _errorMessage = http.getHeaderField("ErrorMessage");
            if (_errorMessage != null && _errorMessage.length() > 0) {
                return null;
            }
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
            return bufferReader.lines().collect(Collectors.joining("\n"));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (http != null)
                http.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(_errorMessage == null || _errorMessage.length() == 0) {
            _callback.onSuccess(result);
        }
        else{
            _callback.onError(_errorMessage);
        }
        //You can update UI here
    }
}
