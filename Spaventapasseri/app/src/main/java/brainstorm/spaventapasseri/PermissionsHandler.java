package brainstorm.spaventapasseri;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * @author M
 * Inspired by https://github.com/Fotoapparat/Fotoapparat/blob/master/sample/src/main/java/io/fotoapparat/sample/PermissionsDelegate.java
 */
public class PermissionsHandler {

    public static final int REQUEST_CAMERA_CODE = 15;
    public static final int REQUEST_STORAGE_CODE = 16;
    private final Activity activity;

    PermissionsHandler(Activity activity) {
        this.activity = activity;
    }

    boolean hasCameraPermission() {
        if (Build.VERSION.SDK_INT < 23)
            return true;
        int permissionCheckResult = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
            );
        return permissionCheckResult == PackageManager.PERMISSION_GRANTED;
    }

    void requestCameraPermission() {
        ActivityCompat.requestPermissions(
            activity,
            new String[] {Manifest.permission.CAMERA},
            REQUEST_CAMERA_CODE
            );
    }

    boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT < 23)
            return true;
        int permissionCheckResult = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            );
        return permissionCheckResult == PackageManager.PERMISSION_GRANTED;
    }

    void requestStoragePermission() {
        ActivityCompat.requestPermissions(
            activity,
            new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
            REQUEST_STORAGE_CODE
            );
    }

    boolean resultGranted(int requestCode,
                          String[] permissions,
                          int[] grantResults) {
        if (requestCode != REQUEST_CAMERA_CODE && requestCode != REQUEST_STORAGE_CODE)
        {
            return false;
        }
        if (grantResults.length < 1)
        {
            return false;
        }
        if (requestCode == REQUEST_CAMERA_CODE && !( permissions[0].equals(Manifest.permission.CAMERA) ))
        {
            return false;
        }
        else if (requestCode == REQUEST_STORAGE_CODE && !( permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) ))
        {
            return false;
        }
        View noPermissionView = activity.findViewById(R.id.no_permission);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            noPermissionView.setVisibility(View.GONE);
            return true;
        }
        if (requestCode == REQUEST_CAMERA_CODE)
            requestCameraPermission();
        else if (requestCode == REQUEST_STORAGE_CODE)
            requestStoragePermission();
        noPermissionView.setVisibility(View.VISIBLE);
        return false;
    }
}
