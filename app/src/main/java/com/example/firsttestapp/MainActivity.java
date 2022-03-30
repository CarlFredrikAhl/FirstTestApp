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
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.Sceneform;
import com.google.ar.sceneform.math.Quaternion;
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


public class MainActivity extends AppCompatActivity {
    //Button changeActBtn;
    //TextInputEditText modelLinkText;
    static final String ALIEN_LINK = "https://raw.githubusercontent.com/BabylonJS/Babylon.js/master/Playground/scenes/Alien/Alien.gltf";
    static final String AVOCADO_LINK = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF/Avocado.gltf";

    private ArFragment arFragment;

    private Session session;

    private boolean modelLoaded;
    private boolean notifyImgTracked;

    private ModelRenderable model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //modelLinkText = findViewById(R.id.modelLinkInput);
        modelLoaded = false;
        notifyImgTracked = false;

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

        //Wait for scene view to be ready then add the update listener
        arFragment.setOnViewCreatedListener(arSceneView -> {
            setupSesh();
            arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
                //Toast.makeText(getApplicationContext(), "Update loop", Toast.LENGTH_SHORT).show();
                Frame frame = arFragment.getArSceneView().getArFrame();
                Collection<AugmentedImage> updateAugmentedImg = frame.getUpdatedTrackables(AugmentedImage.class);

                for(AugmentedImage img : updateAugmentedImg) {
                    if(img.getTrackingState() == TrackingState.TRACKING) {
                        if(img.getName().equals("flower")) {
                            if(!notifyImgTracked) {
                                Toast.makeText(getApplicationContext(), "Seeing tracked image", Toast.LENGTH_SHORT).show();
                                notifyImgTracked = true;
                            }
                            //Load model

                            AnchorNode anchorNode = new AnchorNode(img.createAnchor(img.getCenterPose()));
                            //Node node = new Node();
                            Pose pose = Pose.makeTranslation(0.0f, 0.0f, 0f);

                            //node.setParent(anchorNode);
                            anchorNode.setLocalPosition(new Vector3(pose.tx(), pose.ty(), pose.tz()));
                            anchorNode.setLocalScale(new Vector3(0.1f, 0.1f, 0.1f));
                            //node.setLocalPosition(new Vector3(pose.tx(), pose.ty(), pose.tz()));
                            //node.setLocalScale(new Vector3(0.1f, 0.1f, 0.1f));
                            //node.setLocalRotation(new Quaternion(pose.qx(), pose.qy(), pose.qz(), pose.qw()));

                            if(!modelLoaded) {
                                ModelRenderable.builder()
                                        .setSource(this, Uri.parse("https://storage.googleapis.com/ar-answers-in-search-models/static/Tiger/model.glb"))
                                        .setIsFilamentGltf(true)
                                        .setAsyncLoadEnabled(true)
                                        .build()
                                        .thenAccept(modelRenderable -> addModelToScene(modelRenderable, anchorNode));

                                modelLoaded = true;
                            }

                            break;
                        }
                    }
                }
            });
        });
    }

    private void setupSesh() {
        if(session == null) {
            try {
                session = new Session(this);
            } catch (Exception e) {
            }
        }
        configSesh();
        arFragment.getArSceneView().setSession(session);
    }

    private void addModelToScene(ModelRenderable model) {
        this.model = model;
    }

    private void addModelToScene(ModelRenderable model, Anchor anchor) {
        AnchorNode node = new AnchorNode(anchor);
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setLocalScale(new Vector3(0.1f, 0.1f, 0.1f));
        transformableNode.setParent(node);
        transformableNode.setRenderable(model);

        arFragment.getArSceneView().getScene().addChild(node);
        transformableNode.select();
    }

    private void addModelToScene(ModelRenderable model, AnchorNode node) {
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setLocalScale(new Vector3(0.1f, 0.1f, 0.1f));
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
}