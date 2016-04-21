package com.assignment1.musharaf.assignment1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    private EditText url_edittext;
    StatusLine status;
    String responseAsString;
    TextView text_view;
    Exception e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        //What happens here? What is this? It feels like this is wrong.
        //Maybe the weird programmer who wrote this forgot to do something?
        text_view  = (TextView)findViewById(R.id.textView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void fetchHTML(View view) throws IOException {

        url_edittext = (EditText) findViewById(R.id.url);
        if (url_edittext.getText().toString().equals("")) {
            showToast("Please enter some URL");
        } else {
            new Thread(new Runnable() {
                public void run() {

                    try {
                        HttpClient client = new DefaultHttpClient();
                        HttpResponse response = client.execute(new HttpGet(url_edittext.getText().toString()));

                        status = response.getStatusLine();
                        if (status.getStatusCode() == HttpStatus.SC_OK) {
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            response.getEntity().writeTo(outStream);
                            responseAsString = outStream.toString();
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    text_view.setText(responseAsString.toString());
                                }
                            });
                        }
                        else {
                            response.getEntity().getContent().close();
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    text_view.setText("");
                                    showToast(status.getStatusCode() + ": "+ status.getReasonPhrase());
                                }
                            });

                        }
                    } catch (Exception excep) {
                        e = excep;
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                text_view.setText("");
                                showToast("Exception: " + e.getMessage());
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_LONG).show();
    }
}
