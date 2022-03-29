package com.example.firsttestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.Sceneform;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MainActivity extends AppCompatActivity implements Scene.OnUpdateListener {
    //Button changeActBtn;
    //TextInputEditText modelLinkText;
    static final String ALIEN_LINK = "https://raw.githubusercontent.com/BabylonJS/Babylon.js/master/Playground/scenes/Alien/Alien.gltf";
    static final String AVOCADO_LINK = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF/Avocado.gltf";

    private ArFragment arFragment;

    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //modelLinkText = findViewById(R.id.modelLinkInput);

        setupSesh();
        configSesh();

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            Anchor anchor = hitResult.createAnchor();
            ModelRenderable.builder()
                    .setSource(this, Uri.parse("https://storage.googleapis.com/ar-answers-in-search-models/static/Tiger/model.glb"))
                    .setIsFilamentGltf(true)
                    .setAsyncLoadEnabled(true)
                    .build()
                    .thenAccept(modelRenderable -> addModelToScene(modelRenderable, anchor));

        });
    }

    private void setupSesh() {
        if(session == null) {
            try {
                session = new Session(this);
            } catch (Exception e) {
            }
        }
    }

    private void addModelToScene(ModelRenderable model, Anchor anchor) {

        AnchorNode node = new AnchorNode(anchor);
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(node);
        transformableNode.setRenderable(model);

        arFragment.getArSceneView().getScene().addChild(node);
        transformableNode.select();
    }

    private void configSesh() {
        Config config = new Config(session);

        //If buildDatabase failed
        if(!buildDatabase(config)) {
            Toast.makeText(this, "Error creating database", Toast.LENGTH_SHORT).show();
        }

        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        session.configure(config);
    }

    private boolean buildDatabase(Config config) {
        AugmentedImageDatabase imgDatabase;
        Bitmap bitmap = loadImg();

        if(bitmap == null) {
            return false;
        }

        imgDatabase = new AugmentedImageDatabase(session);
        imgDatabase.addImage("flower", bitmap);
        config.setAugmentedImageDatabase(imgDatabase);
        return true;
    }

    private Bitmap loadImg() {
        try {
            InputStream is = getAssets().open("lotus.jpg");
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  null;
    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        //Set position and rotation of model to match the image in the database

        //Current frame
        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> updateAugmentedImage = frame.getUpdatedTrackables(AugmentedImage.class);
    }
}