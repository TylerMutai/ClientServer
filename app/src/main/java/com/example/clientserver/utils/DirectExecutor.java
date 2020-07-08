package com.example.clientserver.utils;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;

public class DirectExecutor implements Executor {


    public void execute(@NonNull Runnable r) {
        //r.run();
        new Thread(r).start();
    }
}
