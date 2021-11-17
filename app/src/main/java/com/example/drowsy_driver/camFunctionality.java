package com.example.drowsy_driver;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.Surface;

import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class camFunctionality extends AppCompatActivity {

    private ImageAnalysis imageAnalysis;
    private boolean cam_access = false;
    private Executor analysisExecutor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_functionality);

        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog.
        ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                cam_access = true;
            } else {
                // Explain that it is necessary and link back to main page
                cam_access = false;
            }
        });

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

        if (cam_access) {

            // may need to change to newFixedThreadPool(n) depending on performance
            analysisExecutor = Executors.newSingleThreadExecutor();

            PreviewView previewView = (PreviewView) findViewById(R.id.previewView);

            ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                    ProcessCameraProvider.getInstance(this);

            cameraProviderFuture.addListener(() -> {
                try {
                    // Camera provider is now guaranteed to be available
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                    // Set up the view finder use case to display camera preview
                    Preview preview = new Preview.Builder().build();

                    // Set up the analysis use case for ML kit
                    imageAnalysis = new ImageAnalysis.Builder().setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                            .build();

                    imageAnalysis.setAnalyzer(analysisExecutor, new ImageAnalysis.Analyzer() {
                        @Override
                        public void analyze(@NonNull @NotNull ImageProxy imageProxy) {
                            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                            // more code here
                            imageProxy.close();
                        }
                    });

                    // Choose the camera by requiring a lens facing
                    CameraSelector cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                            .build();

                    // Attach use cases to the camera with the same lifecycle owner
                    Camera camera = (Camera) cameraProvider.bindToLifecycle(((LifecycleOwner) this),
                            cameraSelector,
                            preview,
                            imageAnalysis);

                    // Connect the preview use case to the previewView
                    preview.setSurfaceProvider(previewView.getSurfaceProvider());
                } catch (InterruptedException | ExecutionException e) {
                    // Currently no exceptions thrown. cameraProviderFuture.get()
                    // shouldn't block since the listener is being called, so no need to
                    // handle InterruptedException.
                }
            }, ContextCompat.getMainExecutor(this));

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
        }
    }
}