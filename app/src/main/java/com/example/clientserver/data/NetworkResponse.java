package com.example.clientserver.data;

public class NetworkResponse {

    private int responseCode;
    private String response;

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return this.response;
    }

}
