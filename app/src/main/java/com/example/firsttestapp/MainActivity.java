package com.example.firsttestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    Button changeActBtn;
    TextInputEditText modelLinkText;
    static final String ALIEN_LINK = "https://raw.githubusercontent.com/BabylonJS/Babylon.js/master/Playground/scenes/Alien/Alien.gltf";
    static final String AVOCADO_LINK = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF/Avocado.gltf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        modelLinkText = findViewById(R.id.modelLinkInput);

        changeActBtn = (Button) findViewById(R.id.changeActBtn);
        changeActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(modelLinkText.getText().toString().matches("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Du måste välja en modell", Toast.LENGTH_SHORT);
                    toast.show();
                    return;

                } else {
                    modelViewer();
                }
            }
        });
    }

    public void modelViewer() {
        String intentLink = "https://arvr.google.com/scene-viewer/1.0?file=";
        String inputFile = modelLinkText.getText().toString();
        String totalLink = intentLink + inputFile;

        switch (inputFile) {
            case "Alien":
                totalLink = intentLink + ALIEN_LINK;
                break;
            case "Avocado":
                totalLink = intentLink + AVOCADO_LINK;
                break;
        }

        try {
            Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
            sceneViewerIntent.setData(Uri.parse(totalLink));
            sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox");
            startActivity(sceneViewerIntent);

        } catch (Exception e) { }
    }
}