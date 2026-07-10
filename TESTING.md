# How to Test via GitHub Actions (Cloud Build)

Your project zip now includes a GitHub Actions workflow at:
  .github/workflows/build-apk.yml

## Steps

### 1. Unzip the project
```bash
cd ~
unzip CarPhotoGuide.zip
cd CarPhotoGuide
```

### 2. Create a GitHub repo and push
If you have `gh` CLI (Termux: `pkg install gh`):
```bash
gh auth login
gh repo create car-photo-guide --public --source=. --push
```

Or manually via git (if you already have a GitHub account):
```bash
git init
git add .
git commit -m "Initial commit: Car Photo Guide app"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/car-photo-guide.git
git push -u origin main
```

### 3. Watch the build
Go to your repo on GitHub:
  https://github.com/YOUR_USERNAME/car-photo-guide/actions

Click on the latest "Build APK" run. It takes ~3-5 minutes.

### 4. Download the APK
When the build finishes (green check):
1. Scroll to the bottom of the run page.
2. Under "Artifacts", click `car-photo-guide-apk`.
3. It downloads as a ZIP containing `app-debug.apk`.

### 5. Install on your ROG Phone
Transfer the APK to your phone (download, email, Google Drive, etc.), then:
```bash
# From Termux after downloading:
adb install -r app-debug.apk
```
Or just tap the APK file in your phone's file manager and allow installation from unknown sources.

### 6. Test the app
1. Open "Car Photo Guide" from your app drawer.
2. Grant camera permission when prompted.
3. Try all three car types (Citadine / Berline / SUV) — the guide frame changes size.
4. Point at a car, align it within the guide frame, tap Capture.
5. Check `Pictures/CarPhotoGuide/` in your gallery — the saved photo has NO overlay.

## Troubleshooting
- If the build fails, click the failed step in the Actions tab to see the error log.
- Common fix: re-push a commit after editing, the workflow re-runs automatically.
- The workflow also supports manual triggering (Actions tab > "Run workflow" button).
