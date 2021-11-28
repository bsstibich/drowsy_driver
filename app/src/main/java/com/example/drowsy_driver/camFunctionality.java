package com.example.drowsy_driver;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.google.mlkit.vision.face.FaceDetectorOptions.CLASSIFICATION_MODE_ALL;
import static com.google.mlkit.vision.face.FaceDetectorOptions.LANDMARK_MODE_ALL;

public class camFunctionality extends AppCompatActivity {

    ActivityResultLauncher<String> requestPermissionLauncher;
    private ImageAnalysis imageAnalysis;
    private Executor analysisExecutor;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    RectOverlay rectOverlay;
    Canvas canvas;

// OVERRIDE DRAWING METHOD TO OVERLAY RECTANGLES
// -------------------------------------------------------------------------------------------------



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_functionality);

// INITIALIZE CAMERA SURFACE
// -------------------------------------------------------------------------------------------------

        previewView = (PreviewView) findViewById(R.id.preview_view);

// INITIALIZE DRAWING OBJECTS
// -------------------------------------------------------------------------------------------------

        rectOverlay = findViewById(R.id.rect_overlay);
        canvas = new Canvas();

// =================================================================================================
// CAMERA PERMISSIONS CHECK BEGINS
// =================================================================================================

        registerCamPermissionResponse();
        tryToStartCamera();
    }

// =================================================================================================
// CAMERA PERMISSIONS CHECK ENDS
// =================================================================================================

// =================================================================================================
// CAMERA USE CASE BEGINS
// =================================================================================================

    private void startCamera() {

// LISTENER
// -------------------------------------------------------------------------------------------------

        // Create a camera listener
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

// SET-UP
// -------------------------------------------------------------------------------------------------

        cameraProviderFuture.addListener(new Runnable () {
            @Override
            public void run() {
                try {

// CAMERA PROVIDER OBJECT
// -------------------------------------------------------------------------------------------------

                    // Camera provider is now guaranteed to be available
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindUseCases(cameraProvider);

// EXCEPTIONS
// -------------------------------------------------------------------------------------------------

                } catch (InterruptedException | ExecutionException e) {
                    // Currently no exceptions thrown. cameraProviderFuture.get()
                    // shouldn't block since the listener is being called, so no need to
                    // handle InterruptedException.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindUseCases(@NonNull ProcessCameraProvider cameraProvider) {
// PREVIEW
// -------------------------------------------------------------------------------------------------

        // Set up the view finder use case to display camera preview
        Preview preview = new Preview.Builder().build();

// DEFAULT CAMERA SELECTOR
// -------------------------------------------------------------------------------------------------

        // Choose the camera by requiring a lens facing
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

// EXECUTOR
// -------------------------------------------------------------------------------------------------

        // Create an executor for the camera to run on
        // may need to change to newFixedThreadPool(n) depending on performance
        analysisExecutor = Executors.newSingleThreadExecutor();

// IMAGE ANALYSIS
// -------------------------------------------------------------------------------------------------

        // Set up the analysis use case for ML kit
        imageAnalysis = new ImageAnalysis.Builder()
                //.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

// =================================================================================================
// ML KIT IMPLEMENTATION WILL BEGIN HERE
// =================================================================================================

// SET UP FACE DETECTION OBJECT
// -------------------------------------------------------------------------------------------------

        // set to detect eyes and classify as open or closed
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setLandmarkMode(LANDMARK_MODE_ALL)
                .setClassificationMode(CLASSIFICATION_MODE_ALL)
                .build();

        FaceDetector detector = FaceDetection.getClient(options);

// IMAGE ANALYZER
// -------------------------------------------------------------------------------------------------

        imageAnalysis.setAnalyzer(analysisExecutor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull @NotNull ImageProxy imageProxy) {

// FEED IMAGE INTO ML ALGORITHM
// -------------------------------------------------------------------------------------------------

                @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
                Image mediaImage = imageProxy.getImage();

                if (mediaImage != null) {
                    InputImage image =
                            InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

                    detectFaces(image, detector);
                }

                imageProxy.close();
            }
        });

// =================================================================================================
// ML KIT IMPLEMENTATION WILL END HERE
// =================================================================================================

// UNBIND PREVIOUS USE CASES
// -------------------------------------------------------------------------------------------------

        cameraProvider.unbindAll();

// BIND USE CASES TO CAMERA LIFECYCLE
// -------------------------------------------------------------------------------------------------

        // Attach use cases to the camera with the same lifecycle owner
        Camera camera = cameraProvider.bindToLifecycle(this,
                cameraSelector,
                preview,
                imageAnalysis);

        // Connect the preview use case to the previewView
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
    }

// =================================================================================================
// CAMERA USE CASE ENDS
// =================================================================================================

    private void detectFaces(InputImage image, FaceDetector detector) {

// FACE DETECTION LISTENERS
// -------------------------------------------------------------------------------------------------

        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        processFaces(faces);
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });


    }

    private void processFaces(List<Face> faces) {

        for (Face face : faces) {
            Log.d("camFunctionality", "found a face");

            Rect bounds = face.getBoundingBox();
            rectOverlay.frameFace(bounds);

            FaceLandmark leftEye = face.getLandmark(FaceLandmark.LEFT_EYE);
            if (leftEye != null) {
                PointF leftEyePos = leftEye.getPosition();
            }

            FaceLandmark rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE);
            if (rightEye != null) {
                PointF rightEyePos = rightEye.getPosition();
            }

            if (face.getLeftEyeOpenProbability() != null) {
                float leftEyeOpenProb = face.getLeftEyeOpenProbability();
            }

            if (face.getRightEyeOpenProbability() != null) {
                float rightEyeOpenProb = face.getRightEyeOpenProbability();
            }
        }
    }

    private void handlePermissionResponse(boolean permission) {
        if (permission) {
            startCamera();
        }
        else {
            Toast.makeText(this,
                    "Drowsy driver requires camera access. Restart the app to grant permissions."
                    , Toast.LENGTH_LONG).show();
        }
    }

    private void registerCamPermissionResponse() {

        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog.
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        handlePermissionResponse(true);
                    } else {
                        handlePermissionResponse(false);
                    }
                });
    }

    private void tryToStartCamera() {

        // check if the app already has permissions for camera
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            handlePermissionResponse(true);
        }
        else {
            // ask for permission
            requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA);
        }
    }



// CONFIGURATION
// -------------------------------------------------------------------------------------------------

    /*
    // Screen orientation configuration
    OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
        @Override
        public void onOrientationChanged(int orientation) {
            int rotation;

            // Monitors orientation values to determine the target rotation value
            if (orientation >= 45 && orientation < 135) {
                rotation = Surface.ROTATION_270;
            } else if (orientation >= 135 && orientation < 225) {
                rotation = Surface.ROTATION_180;
            } else if (orientation >= 225 && orientation < 315) {
                rotation = Surface.ROTATION_90;
            } else {
                rotation = Surface.ROTATION_0;
            }

            imageAnalysis.setTargetRotation(rotation);
        }
    };
     */
}