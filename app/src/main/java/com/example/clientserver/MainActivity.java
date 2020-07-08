package com.example.clientserver;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import tech.gusavila92.websocketclient.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    TextView messagesView;
    private WebSocketClient webSocketClient;
    Button connectionButton;
    EditText messageET;
    boolean connected = false;
    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messagesView = findViewById(R.id.client_text_view);
        messagesView.setMovementMethod(new ScrollingMovementMethod());
        connectionButton = findViewById(R.id.establish_connection_button);
        messageET = findViewById(R.id.client_edit_text);
    }

    public void onEstablishConnectionButtonClick(View view) {
        if (connected) {
            messagesView.append(getString(R.string.closing_connection) + "\n");
            webSocketClient.close();
            connected = false;
            messagesView.append(getString(R.string.connection_closed) + "\n");
            connectionButton.setText(getString(R.string.establish_connection));
        } else {
            createWebSocketClient();
        }
    }

    public void onSendButtonClick(View view) {
        if (!connected) {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(this, getString(R.string.connect_to_server), Toast.LENGTH_SHORT);
            mToast.show();
            return;
        }

        String message = String.valueOf(messageET.getText());
        if (message.length() > 0) {
            messagesView.append("\nME: " + message + "\n");
            webSocketClient.send(message);
        }
    }


    private void createWebSocketClient() {
        URI uri;
        try {
            // Connect to local host
            uri = new URI("wss://stormy-island-50747.herokuapp.com/server");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        messagesView.append("\n" + getString(R.string.establishing_connection) + "\n");
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                connected = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messagesView.append(getString(R.string.connection_established) + "\n");
                        connectionButton.setText(getString(R.string.close_connection));
                    }
                });
                webSocketClient.send("Hello World!");
            }

            @Override
            public void onTextReceived(String s) {
                // Log.i("WebSocket", "Message received");
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            messagesView.append("SERVER: " + message + "\n");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onBinaryReceived(byte[] data) {
            }

            @Override
            public void onPingReceived(byte[] data) {
            }

            @Override
            public void onPongReceived(byte[] data) {
            }

            @Override
            public void onException(Exception e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                //Log.i("WebSocket", "Closed ");
                //System.out.println("onCloseReceived");
                connected = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messagesView.append(getString(R.string.connection_closed) + "\n");
                        connectionButton.setText(getString(R.string.establish_connection));
                    }
                });
            }
        };
        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }

   /* @Override
    public void onCompleted(final NetworkResponse response) {
        JSONObject jObj;
        JSONArray jsonArray;
        ArrayList<String> servers = new ArrayList<>();
        try {
            jObj = new JSONObject(response.getResponse());
            jsonArray = jObj.getJSONArray("response");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                servers.add(obj.toString());
            }
        } catch (JSONException e) {
            setTexts(e.getMessage());
            e.printStackTrace();
        }

        if (servers.size() < 1) {
            setTexts(getString(R.string.));
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        // Add the buttons
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        }).setAdapter(new MyCustomAdapter(servers, getApplicationContext()), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });


        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTexts(response.getResponse());
            }
        });

    }

    @Override
    public void onError(final NetworkResponse response) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                setTexts(response.getResponse());
            }
        });
    }

    @Override
    public void beforeStart() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTexts(getString(R.string.establishing_connection));
            }
        });

    }*/

}
