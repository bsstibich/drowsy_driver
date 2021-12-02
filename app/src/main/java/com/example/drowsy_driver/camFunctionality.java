package com.example.drowsy_driver;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
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

import static com.google.mlkit.vision.face.FaceDetectorOptions.CLASSIFICATION_MODE_ALL;
import static com.google.mlkit.vision.face.FaceDetectorOptions.LANDMARK_MODE_ALL;

public class camFunctionality extends AppCompatActivity {

    ActivityResultLauncher<String> requestPermissionLauncher;
    private ImageAnalysis imageAnalysis;
    private Executor analysisExecutor;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    Overlay overlay;
    Canvas canvas;

    Toolbar toolbar;

    final static float EYE_OPEN_THRESHOLD = 0.5F;
    private boolean face_detected = false;
    private boolean eyes_are_closed = false;

    private long timer;

    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;
    Uri alarm;
    MediaPlayer mp;
    private static final String CHANNEL_ID = "drowsy notification channel";
    private static final int NOTIFY_LVL_1 = 1;
    private boolean first_alert_triggered = false;
    private boolean second_alert_triggered = false;

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.LogOut:
                //Intent i = new Intent(camFunctionality.this, MainActivity.class);
                //startActivity(i);
                //overridePendingTransition(0, 0);
                finish();
                return true;
            case R.id.SettingsOption:
                setContentView(R.layout.activity_main);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //creates tool bar drop down options
        if (getSupportActionBar().getTitle().equals("Watcher")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_main, menu);
            return true;
        }
        else
        {
            return true;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportActionBar().getTitle().equals("Edit Personal Information"))
        {
            return Navigation.findNavController(this, R.id.fragmentContainerView).navigateUp() || super.onSupportNavigateUp();
        }
        else if (getSupportActionBar().getTitle().equals("Delete Profile")){
            return Navigation.findNavController(this, R.id.fragmentContainerView).navigateUp() || super.onSupportNavigateUp();
        }
        else if (getSupportActionBar().getTitle().equals("Change Password"))
        {
            return Navigation.findNavController(this, R.id.fragmentContainerView).navigateUp() || super.onSupportNavigateUp();
        }
        else{
            Intent i = new Intent(camFunctionality.this, camFunctionality.class);
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        }

        return true;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_functionality);

        toolbar = findViewById(R.id.camtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Watcher");

        if (getSupportActionBar() != null)
        {
            if (getSupportActionBar().getTitle().equals("Edit Personal Information"))
            {
                NavigationUI.setupActionBarWithNavController(camFunctionality.this, Navigation.findNavController(this, R.id.fragmentContainerView));
            }
            else if (getSupportActionBar().getTitle().equals("Profile"))
            {
                NavigationUI.setupActionBarWithNavController(camFunctionality.this, Navigation.findNavController(this, R.id.fragmentContainerView));
            }
        }

// =================================================================================================
// INITIALIZE WATCHER COMPONENTS
// =================================================================================================

// INITIALIZE CAMERA SURFACE
// -------------------------------------------------------------------------------------------------

        previewView = (PreviewView) findViewById(R.id.preview_view);

// INITIALIZE DRAWING OBJECTS
// -------------------------------------------------------------------------------------------------

        overlay = findViewById(R.id.overlay_view);
        canvas = new Canvas();

// INITIALIZE NOTIFICATION
// -------------------------------------------------------------------------------------------------

        // notification pop up corresponds with LEVEL 1 ALERT
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notif_icon)
                .setContentTitle("Drowsiness Detected")
                .setContentText("eyes were closed longer than a typical blink")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // allow notifications to be pushed to user
        createNotificationChannel();

        // manages all notifications
        notificationManager = NotificationManagerCompat.from(this);

        // attempts to grab a default alarm tone from phone
        setAlarm();

// =================================================================================================
// CAMERA PERMISSIONS CHECK BEGINS
// =================================================================================================

        registerCamPermissionResponse();
        // will start only if permissions met
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
        // update: performance good on single thread
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

        // images will be fed to this analyzer through the image proxy service
        imageAnalysis.setAnalyzer(analysisExecutor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull @NotNull ImageProxy imageProxy) {

// FEED IMAGE INTO ML ALGORITHM
// -------------------------------------------------------------------------------------------------

                // run ML model on images from analyzer (live frames)
                detectFaces(imageProxy, detector);
            }
        });

// =================================================================================================
// ML KIT IMPLEMENTATION WILL END HERE
// =================================================================================================

// UNBIND PREVIOUS USE CASES
// -------------------------------------------------------------------------------------------------

        // WARNING: must unbing previous use cases before new ones can be bound
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

// =================================================================================================
// NOTIFICATIONS
// =================================================================================================


    private void setAlarm() {
        // attempt to grab the default alarm tone
        alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        // if none is set, grab default ringtone
        if (alarm == null) {
            alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

            // if none is set, grab default notification ping
            if (alarm == null) {
                alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }

        // create a media player which loads the sound
        mp = MediaPlayer.create(getApplicationContext(), alarm);
    }

    // Level 1 alert
    private void trigger_first_alert() {
        notificationManager.notify(NOTIFY_LVL_1, builder.build());
        first_alert_triggered = true;
    }

    // level 2 alert
    private void trigger_second_alert() {
        mp.start();
        second_alert_triggered = true;
    }

    // resets all alerts and stops and alarm playing
    private void reset_alerts() {
        first_alert_triggered = false;
        if (second_alert_triggered) {
            mp.pause();
            second_alert_triggered = false;
        }
    }

// =================================================================================================
// NOTIFICATIONS END HERE
// =================================================================================================


// =================================================================================================
// ML USE CASES BEGIN HERE
// =================================================================================================

    private void detectFaces(ImageProxy imageProxy, FaceDetector detector) {

// GET THE IMAGE FROM THE FRAME
// -------------------------------------------------------------------------------------------------

        @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
        Image mediaImage = imageProxy.getImage();

        // only proceed if the image is actually there
        if (mediaImage != null) {
            InputImage image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

// FACE DETECTION LISTENERS
// -------------------------------------------------------------------------------------------------

            // crates a task to process all faces found by facial detection
            // on successful processing it will trigger our custom processes
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
                                            // included for logging and debugging purposes
                                            //Log.d("face detection", "face detection failed");
                                            //e.printStackTrace();
                                            // ...
                                        }
                                    })
                            .addOnCompleteListener(
                                    new OnCompleteListener<List<Face>>() {
                                        @Override
                                        public void onComplete(@NonNull Task<List<Face>> task) {
                                            imageProxy.close();
                                        }
                                    }
                            );
        }
    }

// =================================================================================================
// CUSTOM PROCESSING FUNCTIONS FOLLOW
// =================================================================================================

    private void processFaces(List<Face> faces) {

        // only update screen if a single face is found
        if (faces.size() == 1) {

            // if a face wasn't previously detected
            // update the screen and show that it is now detected
            if (!face_detected) {
                face_detected = true;
                overlay.foundFace(true);
            }

            // if a face was detected, try to detect eyes
            if (foundEyes(faces.get(0))) {
                // if eyes were detected, determine if they are open or closed
                checkEyesOpen(faces.get(0));
            }
        }

        else {

            // if a face was detected and now it isn't
            // update the screen
            if (face_detected) {
                face_detected = false;
                overlay.foundFace(false);
            }
        }
    }

    // Measures eye open probability against a pre-determined threshold to predict if eyes are
    // open or closed
    private void checkEyesOpen(Face face) {
        if (face.getLeftEyeOpenProbability() == null || face.getRightEyeOpenProbability() == null) {
            handleEyesClosed();
        }

        else {
            boolean eye_status = (face.getLeftEyeOpenProbability() >= EYE_OPEN_THRESHOLD
                    && face.getRightEyeOpenProbability() >= EYE_OPEN_THRESHOLD);

            if (eye_status) {
                handleEyesOpen();
            } else {
                handleEyesClosed();
            }
        }
    }

    // triggers alerts based on duration of elapsed time
    // with eyes spend closed
    private void handleEyesClosed() {
        if (eyes_are_closed) {
            float duration = getTime();
            if (duration > 3 && !second_alert_triggered) {
                trigger_second_alert();
            }
            else if (duration > 0.5 && !first_alert_triggered) {
                trigger_first_alert();
            }
            else {
                if (duration > 0.25) {
                    overlay.eyesOpen(false);
                }
            }
        } else {
            eyes_are_closed = true;
            startTimer();
        }
    }

    // resets alerts when eyes reopen
    private void handleEyesOpen() {
        if (eyes_are_closed) {
            eyes_are_closed = false;
            resetTimer();
            reset_alerts();
            overlay.eyesOpen(true);
        }
    }

    // finds eyes within the face using facial landmarks
    // and updates screen accordingly
    private boolean foundEyes(Face face) {
        FaceLandmark leftEye = face.getLandmark(FaceLandmark.LEFT_EYE);
        FaceLandmark rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE);

        if (leftEye != null && rightEye != null) {
            overlay.foundEyes(true);
            return true;
        } else {
            overlay.foundEyes(false);
            return false;
        }
    }

    private void startTimer() {
        timer = System.currentTimeMillis();
    }

    private void resetTimer() {
        timer = 0;
    }

    // finds the elapsed time for eyes being closed
    private float getTime() {
        long now = System.currentTimeMillis();
        float time_elapsed = (float) (now - timer) / 1000;
        return time_elapsed;
    }

// =================================================================================================
// ML USE CASES END HERE
// =================================================================================================

// =================================================================================================
// PERMISSION CHECK FUNCTIONS BEGIN HERE
// =================================================================================================

    // handles the user response passed by the permission request
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

    // callback activity that checks for a recorded
    // camera permissions response from the user and loads a dialogue if there isn't one
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

// =================================================================================================
// PERMISSION CHECK FUNCTIONS END HERE
// =================================================================================================

    // starts camera if permissions have been granted
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

    // manages notifications
    private void createNotificationChannel() {
        // Only on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
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