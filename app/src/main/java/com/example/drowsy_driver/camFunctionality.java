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
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.graphics.Camera;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.Surface;
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

    private ImageAnalysis imageAnalysis;
    private boolean cam_access = false;
    private Executor analysisExecutor;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView preview_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_functionality);

// INITIALIZE CAMERA SURFACE
// -------------------------------------------------------------------------------------------------

        preview_view = (PreviewView) findViewById(R.id.preview_view);

// =================================================================================================
// CAMERA PERMISSIONS CHECK BEGINS
// =================================================================================================

        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog.
        ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                cam_access = true;
            } else {
                // cam_access = false;
                // Explain that it is necessary and link back to main page
                Toast.makeText(this, "You can't use drowsy driver without granting CAMERA permission",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });

        // check if the app already has permissions for camera
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            cam_access = true;
            /*
        } else if (shouldShowRequestPermissionRationale(...)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            showInContextUI(...);
             */
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA);
        }

// =================================================================================================
// CAMERA PERMISSIONS CHECK ENDS
// =================================================================================================

// =================================================================================================
// CAMERA USE CASE BEGINS
// =================================================================================================

        // main loop begins here only if permissions satisfied
        if (cam_access) {
            startCamera();
        }
    }

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

// CAMERA PROVIDER
// -------------------------------------------------------------------------------------------------

                    // Camera provider is now guaranteed to be available
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

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

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
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
        // Set image output to RGB format
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
        FaceDetectorOptions classifyLandmarks = new FaceDetectorOptions.Builder()
                .setLandmarkMode(LANDMARK_MODE_ALL)
                .setClassificationMode(CLASSIFICATION_MODE_ALL)
                .build();

        FaceDetector detector = FaceDetection.getClient(classifyLandmarks);

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

// FACE DETECTION LISTENERS
// -------------------------------------------------------------------------------------------------

                    Task<List<Face>> result =
                            detector.process(image)
                                    .addOnSuccessListener(
                                            new OnSuccessListener<List<Face>>() {
                                                @Override
                                                public void onSuccess(List<Face> faces) {
                                                    for (Face face : faces) {
                                                        Rect bounds = face.getBoundingBox();

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
                //int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                //orientationEventListener.onOrientationChanged(rotationDegrees);
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
        Camera camera = (Camera) cameraProvider.bindToLifecycle((LifecycleOwner) this,
                cameraSelector,
                preview,
                imageAnalysis);

        // Connect the preview use case to the previewView
        preview.setSurfaceProvider(preview_view.getSurfaceProvider());
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