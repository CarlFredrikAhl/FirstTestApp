package com.example.firsttestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.ConnectIQ.*;
import com.garmin.android.connectiq.IQDevice;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    Button changeActBtn;
    TextView toolbarText;

    //Garmin Connect
    ConnectIQ connectIQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectIQ = ConnectIQ.getInstance(this, IQConnectType.WIRELESS);
        connectIQ.initialize(this, true, new ConnectIQListener() {
            @Override
            public void onSdkReady() {
                Toast toast = Toast.makeText(getApplicationContext(), "Garmin SDK Success", Toast.LENGTH_LONG);
                toast.show();

                ListView listView = findViewById(R.id.listView);

                List<String> list = new ArrayList<>();
                list.add("Test 1");
                list.add("Test 2");
                list.add("Test 3");

                ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);

                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onInitializeError(IQSdkErrorStatus iqSdkErrorStatus) {

            }

            @Override
            public void onSdkShutDown() {

            }
        });

        changeActBtn = (Button) findViewById(R.id.changeActBtn);
        changeActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start intent
                //changeAct();
                openCam();
                
            }
        });
    }

    public void openCam() {
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            startActivity(takePicIntent);
        } catch (ActivityNotFoundException e) {

        }
    }

    public void changeAct() {
        Intent intent = new Intent(MainActivity.this, Activity2.class);
        startActivity(intent);
    }
}