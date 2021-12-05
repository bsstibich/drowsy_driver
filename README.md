## Drowsy Driver Android App
UTA group project for CSE 3310.

Contributers:
  Brandon Stibich,
  Augustine Nguyen,
  Clay Gifford,
  Anakin Chounchantharat,
  
Description: a mobile software application for individuals to monitor their state of awareness while driving to promote safe driving. The mobile-based application will monitor an individual’s eye and alert the user if their eye lids are closed for a specific amount of time due to drowsiness and alert the user. The application will be operational the second week of December 2021. (Group Name) is open to any recommendations or feedback on the look and feel of the app.  

### App Instructions
- Register or login with username and password
- Place phone in a secure location where face is in view of the front camera
  - Sound will be played to notify driver of eyes closing by a certain margin
- If needed, click top right pop up to either log off or access personal information
  - Personal information can be viewed and then edited

### Code Instructions
- Meet all requirements below
- Open Android Studio and click on File->New->Import Project... and import the drowsy_driver project
- Once imported, wait till gradle is built
  - If errors occur or gradle isn't built:
    - Go to Build->Clean Project and then Build->Rebuild Project
- Finally, run the AVD emulator
  - If emulator is not launched, run again and again until launched (sometimes running just builds gradle or simply launches the emulator without running the app)

### System Requirements for Android Studio
- Follow link: https://developer.android.com/studio
- Scroll to bottom and look for your OS 

### System Requirements for Emulator
- SDK Tools 26.1.1 or higher
- 64-bit processor
- Windows: CPU with UG (unrestricted guest) support
- HAXM 6.2.1 or later (HAXM 7.2.0 or later recommended)

### Software Requirements
- Android Studio Version and Gradle Plugin Version 4.2.2
- Gradle Version 7.0.2

### SDK and AVD Requirements
- Target SDK:  API 31
- Minimum SDK:  API 21
- AVD:  Any android device running API 21 or greater
- Recommended Device:  Pixel 4 API 21

### Gradle Configuration
#### Project Gradle [build.gradle (PROJECT_NAME)]:
```
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:4.2.2'
        classpath 'com.google.gms:google-services:4.3.3'

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```
#### App Gradle [build.gradle (:app)]:
```
plugins {
    id 'com.android.application'
    id 'com.google.gms.google-services'
}
apply plugin: 'com.android.application'
apply plugin: 'com.google.gms.google-services'
android {
    compileSdk 31
    defaultConfig {
        applicationId "com.example.drowsy_driver"
        minSdk 21
        targetSdk 31
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    useLibrary 'android.test.runner'
    useLibrary 'android.test.base'
    useLibrary 'android.test.mock'
}
dependencies {
    // CameraX core library using the camera2 implementation
    def camerax_version = "1.0.2"
    implementation "androidx.camera:camera-core:${camerax_version}"
    implementation "androidx.camera:camera-camera2:${camerax_version}"
    implementation "androidx.camera:camera-lifecycle:${camerax_version}"
    implementation "androidx.camera:camera-view:1.0.0-alpha31"
    //implementation "androidx.camera:camera-extensions:1.0.0-alpha31"

    // ML Kit
    implementation 'com.google.mlkit:face-detection:16.1.2'
    implementation 'com.google.android.gms:play-services-mlkit-face-detection:16.2.0'

    // UI Related
    def activity_version = "1.4.0"
    implementation "androidx.activity:activity:$activity_version"
    def fragment_version = "1.4.0"

    // Java language implementation
    implementation "androidx.fragment:fragment:$fragment_version"
    implementation 'androidx.appcompat:appcompat:1.3.1'
    implementation 'com.google.android.material:material:1.4.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.1'
    implementation 'androidx.navigation:navigation-fragment:2.3.5'
    implementation 'androidx.navigation:navigation-ui:2.3.5'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'androidx.preference:preference:1.1.1'

    // Firebase
    implementation 'com.google.firebase:firebase-auth:19.2.0'
    implementation 'com.google.firebase:firebase-database:19.2.1'
    implementation platform('com.google.firebase:firebase-bom:28.4.2')
    implementation 'com.google.firebase:firebase-analytics'
    implementation 'com.google.firebase:firebase-database'

    // Testing
    androidTestImplementation 'com.android.support:support-annotations:24.0.0'
    androidTestImplementation 'com.android.support.test:runner:0.5'

    // Testing Fragments in Isolation
    debugImplementation "androidx.fragment:fragment-testing:$fragment_version"

    // Core library
    androidTestImplementation 'androidx.test:core:1.4.0'

    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation 'androidx.test:runner:1.4.0'
    androidTestImplementation 'androidx.test:rules:1.4.0'
    testImplementation 'junit:junit:4.+'

    // Assertions
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.ext:truth:1.4.0'
    androidTestImplementation 'com.google.truth:truth:1.0.1'

    // Espresso dependencies
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
    androidTestImplementation 'androidx.test.espresso:espresso-contrib:3.4.0'
    androidTestImplementation 'androidx.test.espresso:espresso-intents:3.4.0'
    androidTestImplementation 'androidx.test.espresso:espresso-accessibility:3.4.0'
    androidTestImplementation 'androidx.test.espresso:espresso-web:3.4.0'
    androidTestImplementation 'androidx.test.espresso.idling:idling-concurrent:3.4.0'

    // other testing
    androidTestImplementation 'androidx.test.uiautomator:uiautomator:2.2.0'
    androidTestImplementation 'org.hamcrest:hamcrest-library:1.3'

// The following Espresso dependency can be either "implementation"
// or "androidTestImplementation", depending on whether you want the
}
```
