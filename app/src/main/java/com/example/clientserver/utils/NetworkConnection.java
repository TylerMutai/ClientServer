package com.example.clientserver.utils;

import androidx.appcompat.app.AppCompatActivity;
import com.example.clientserver.R;
import com.example.clientserver.data.NetworkResponse;
import com.example.clientserver.interfaces.NetworkCallback;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.concurrent.Executor;

public class NetworkConnection {

    private final NetworkCallback callback;
    private final WeakReference<AppCompatActivity> activity;

    public NetworkConnection(NetworkCallback callback, AppCompatActivity activity) {
        this.callback = callback;
        this.activity = new WeakReference<>(activity);
    }

    public void makeConnection(final String url) {
        Executor executor = new DirectExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                NetworkResponse response = new NetworkResponse();
                if (url != null) {
                    try {
                        URL theUrl = new URL(url);
                        callback.beforeStart();
                        response = makeConnection(theUrl);
                        if (response.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                            callback.onCompleted(response);
                        } else {
                            callback.onError(response);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        String message = activity.get().getString(R.string.malformed_url);

                        if (e.getMessage() != null && e.getMessage().length() > 1) {
                            message = e.getMessage();
                        }

                        response.setResponse(message);
                        callback.onError(response);
                    }
                } else {
                    throw new NullPointerException("URL string cannot be null");
                }
            }
        });
    }

    /**
     * Given a URL, sets up a connection and gets the HTTP response body from the server.
     * If the network request is successful, it returns the response body in String form. Otherwise,
     * it will throw an IOException.
     */
    private NetworkResponse makeConnection(URL url) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        NetworkResponse networkResponse = new NetworkResponse();
        try {
            connection = (HttpsURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();

            int responseCode = connection.getResponseCode();
            networkResponse.setResponseCode(responseCode);
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null) {
                //creating an InputStreamReader object
                InputStreamReader isReader = new InputStreamReader(stream);
                //Creating a BufferedReader object
                BufferedReader reader = new BufferedReader(isReader);
                StringBuilder sb = new StringBuilder();
                String str;
                while ((str = reader.readLine()) != null) {
                    sb.append(str);
                }
                networkResponse.setResponse(sb.toString());
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return networkResponse;
    }

}
