package com.example.firsttestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button changeActBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeActBtn = (Button) findViewById(R.id.changeActBtn);
        changeActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start intent
                changeAct();
            }
        });
    }

    public void changeAct() {
        Intent intent = new Intent(MainActivity.this, Activity2.class);
        startActivity(intent);
    }
}