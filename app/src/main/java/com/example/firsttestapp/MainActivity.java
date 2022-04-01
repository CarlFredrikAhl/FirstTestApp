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
import com.google.ar.core.Pose;
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
import com.google.ar.sceneform.math.Vector3;
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


public class MainActivity extends AppCompatActivity implements
        FragmentOnAttachListener,
        BaseArFragment.OnSessionConfigurationListener,
        BaseArFragment.OnTapArPlaneListener {
    //Button changeActBtn;
    //TextInputEditText modelLinkText;
    static final String ALIEN_LINK = "https://raw.githubusercontent.com/BabylonJS/Babylon.js/master/Playground/scenes/Alien/Alien.gltf";
    static final String AVOCADO_LINK = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF/Avocado.gltf";

    private ArFragment arFragment;

    private Session session;

    private boolean modelLoaded;
    private boolean notifyImgTracked;
    private boolean shouldConfigureSession = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //modelLinkText = findViewById(R.id.modelLinkInput);

        getSupportFragmentManager().addFragmentOnAttachListener(this);
        getSupportFragmentManager().beginTransaction().add(R.id.arFragment, ArFragment.class, null).commit();
    }

    private boolean buildDatabase(Config config) {
        AugmentedImageDatabase imgDatabase;

        Bitmap[] bitmaps = new Bitmap[] {
                loadImg("lotus.jpg"),
                loadImg("bee_qr_code.jpeg"),
                loadImg("lion_qr_code.jpeg")
        };

        for(Bitmap bitmap : bitmaps) {
            if(bitmap == null) {
                return false;
            }
        }

        imgDatabase = new AugmentedImageDatabase(session);
        imgDatabase.addImage("flower", bitmaps[0]);
        imgDatabase.addImage("bee", bitmaps[1]);
        imgDatabase.addImage("lion", bitmaps[2]);
        config.setAugmentedImageDatabase(imgDatabase);
        return true;
    }

    private Bitmap loadImg(String img) {
        try {
            InputStream is = getAssets().open(img);
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  null;
    }

    private void addModelToScene(ModelRenderable model, Anchor anchor) {

        AnchorNode node = new AnchorNode(anchor);

        Pose pose = Pose.makeTranslation(0.0f, 0.0f, 0.25f);

        node.setLocalPosition(new Vector3(pose.tx(), pose.ty(), pose.tz()));
        node.setLocalScale(new Vector3(0.1f, 0.1f, 0.1f));

        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(node);
        transformableNode.setRenderable(model);

        arFragment.getArSceneView().getScene().addChild(node);
        transformableNode.select();
    }

    @Override
    public void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        if(fragment.getId() == R.id.arFragment) {
            arFragment = (ArFragment) fragment;
            arFragment.setOnSessionConfigurationListener(this);
            arFragment.setOnTapArPlaneListener(this);
        }
    }

    @Override
    public void onSessionConfiguration(Session session, Config config) {
        this.session = session;
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        buildDatabase(config);

        //Image detection
        arFragment.setOnAugmentedImageUpdateListener(this::onAugmentedImageTrackingUpdate);
    }

    private void onTapArPlaneListener(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        Anchor anchor = hitResult.createAnchor();
        ModelRenderable.builder()
                .setSource(this, Uri.parse("https://storage.googleapis.com/ar-answers-in-search-models/static/Tiger/model.glb"))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(modelRenderable -> addModelToScene(modelRenderable, anchor));
    }

    private void onAugmentedImageTrackingUpdate(AugmentedImage augmentedImage) {
    }

    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        Anchor anchor = hitResult.createAnchor();
        ModelRenderable.builder()
                .setSource(this, Uri.parse("https://storage.googleapis.com/ar-answers-in-search-models/static/Tiger/model.glb"))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(modelRenderable -> addModelToScene(modelRenderable, anchor));
    }
}