package com.famark.apiexample;

import java.io.IOException;
import java.io.Reader;

public interface OnAPICallback {

    void onSuccess(String response);
    void onError(String errorMessage);
}
