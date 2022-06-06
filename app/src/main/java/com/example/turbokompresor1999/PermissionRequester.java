package com.example.turbokompresor1999;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class PermissionRequester {
    private static boolean hasPermission(Context context, String permission)
    {
        int res = context.checkCallingOrSelfPermission(permission);
        Log.v(TAG, "permission: " + permission + " = \t\t" +
                (res == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));
        return res == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasPermissions(Context context, String... permissions)
    {
        boolean hasAllPermissions = true;
        for(String permission : permissions) {
            //you can return false instead of assigning, but by assigning you can log all permission values
            if (! hasPermission(context, permission)) {hasAllPermissions = false; }
        }
        return hasAllPermissions;
    }

    public static void checkPermissions(Context ctx)
    {
        int permissionsCode = 42;
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!hasPermissions(ctx, permissions)) {
            ActivityCompat.requestPermissions((Activity) ctx, permissions, permissionsCode);
        }
    }
}
