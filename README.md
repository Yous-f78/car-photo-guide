# Car Photo Guide

An Android app that overlays a standardized guide on the camera viewfinder so you
can take consistent photos of cars for listings, inspections, etc.

## Features
- Rear camera preview (CameraX).
- Three selectable templates:
  - **Citadine**   (small car, 65% frame width)
  - **Berline**    (sedan,   78% frame width)
  - **Monospace / SUV** (90% frame width)
- Semi-transparent overlay: rectangle frame, center crosshair, corner brackets,
  outside-area darkening.
- Capture button saves full-resolution JPEG to `Pictures/CarPhotoGuide/` with a
  timestamped name (`car_YYYYMMDD_HHMMSS.jpg`). The overlay is **not** burned
  into the saved photo.
- Runtime camera permission handling.
- Target SDK 34, min SDK 24 (Android 7.0+).

## Build

### Prerequisites
- Android Studio (Hedgehog or newer) **or** a JDK 17+ and the Android SDK
  (command-line tools). The Gradle wrapper is included.

### From the command line

    cd CarPhotoGuide
    ./gradlew assembleDebug

The APK is created at:

    app/build/outputs/apk/debug/app-debug.apk

Install on a connected device:

    adb install -r app/build/outputs/apk/debug/app-debug.apk

### Importing into Android Studio
1. File ▸ Open ▸ select the `CarPhotoGuide` folder.
2. Let Gradle sync finish.
3. Press Run (Shift+F10) or Build ▸ Build APK(s).

## How it works
- `MainActivity` sets up CameraX `Preview` + `ImageCapture` use-cases bound to
  the activity lifecycle.
- `GuideOverlayView` is a custom `View` layered above `PreviewView`. It draws
  the guide geometry based on the selected `CarType` enum.
- Selecting a chip changes `GuideOverlayView.carType`, which triggers a redraw
  with the new frame size.
- On capture, `ImageCapture` writes a JPEG to MediaStore; the overlay is a
  separate view and is never part of the captured sensor image.

## Notes
- On Android 10+ storage is scoped automatically (no `WRITE_EXTERNAL_STORAGE`
  needed). On Android 7-9 the manifest requests
  `WRITE_EXTERNAL_STORAGE` (maxSdk 28). If you target pre-Q devices, also grant
  the runtime storage permission.
- Orientation is locked to portrait for consistency.
