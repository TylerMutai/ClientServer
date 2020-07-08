package com.example.clientserver.interfaces;


import com.example.clientserver.data.NetworkResponse;

public interface NetworkCallback{

    void onCompleted(NetworkResponse response);
    void onError(NetworkResponse response);
    void beforeStart();
}