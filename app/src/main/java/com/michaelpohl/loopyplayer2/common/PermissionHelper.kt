package com.michaelpohl.loopyplayer2.common

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import timber.log.Timber

class PermissionHelper(private val activity: Activity) {

    private val REQUEST_CODE = 999 // chosen randomly. What is the rule which number to pick?
    fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permissionsNotGranted = mutableListOf<String>()

        permissions.forEach {
            if (!checkSinglePermission(it)) {
                permissionsNotGranted.add(it)
            }
        }
        if (permissionsNotGranted.isNotEmpty()) {
            requestPermission(permissionsNotGranted.toTypedArray())
        }
    }

    private fun checkSinglePermission(permission: String): Boolean {
        val permissionState = ContextCompat.checkSelfPermission(
            activity,
            permission
        )

        return if (permissionState == PackageManager.PERMISSION_GRANTED) {
            Timber.v("Permission %s granted", permission)
            true
        } else {
            Timber.e("Permission %s denied", permission)
            false
        }
    }

    private fun requestPermission(permissions: Array<String>) {
        ActivityCompat.requestPermissions(
            activity,
            permissions,
            REQUEST_CODE
        )
    }
}
