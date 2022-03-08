package com.example.firsttestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button changeActBtn;
    TextView toolbarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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