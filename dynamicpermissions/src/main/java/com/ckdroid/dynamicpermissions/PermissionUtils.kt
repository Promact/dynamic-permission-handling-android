package com.ckdroid.dynamicpermissions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionUtils {
    companion object {

        fun checkAndRequestPermissions(
            activity: Activity,
            permissions: MutableList<String>,
            requestCode: Int,
            checkStatusOnly: Boolean = false
        ): PermissionResult {

            val permissionPreference = PermissionPreference(activity)

            val permissionResult = PermissionResult()

            val permissionStatus: HashMap<String, PermissionStatus> = hashMapOf()

            permissions.forEach { permission ->
                if (hasPermissionAllowed(activity, permission)) {
                    permissionStatus[permission] = PermissionStatus.ALLOWED
                } else {
                    val isShowRational = isNeededToShowRequestRational(activity, permission)
                    val isAskedPermissionBefore =
                        permissionPreference.isPermissionRequestedBefore(permission)

                    when {
                        isShowRational -> {
                            permissionStatus[permission] = PermissionStatus.NOT_GIVEN
                        }
                        isAskedPermissionBefore && !isShowRational -> {
                            permissionStatus[permission] = PermissionStatus.DENIED_PERMANENTLY
                        }
                        else -> {
                            permissionStatus[permission] = PermissionStatus.NOT_GIVEN
                        }
                    }
                }
            }

            permissionResult.permissionStatus = permissionStatus

            val isAnyPermissionDeniedPermanently =
                permissionStatus.values.any { it == PermissionStatus.DENIED_PERMANENTLY }

            if (isAnyPermissionDeniedPermanently) {
                permissionResult.finalStatus = PermissionStatus.DENIED_PERMANENTLY
                return permissionResult
            }

            val isAnyPermissionNotGiven =
                permissionStatus.values.any { it == PermissionStatus.NOT_GIVEN }

            if (isAnyPermissionNotGiven) {

                if (!checkStatusOnly) {
                    val notGivenPermissionList =
                        permissionStatus.filter { it.value == PermissionStatus.NOT_GIVEN }
                            .keys.toMutableList()

                    requestPermissions(activity, notGivenPermissionList, requestCode)
                }

                permissionResult.finalStatus = PermissionStatus.NOT_GIVEN
                return permissionResult

            }

            permissionResult.finalStatus = PermissionStatus.ALLOWED
            return permissionResult
        }

        fun checkAndRequestPermissions(
            activity: Activity,
            permission: String,
            requestCode: Int,
            checkStatusOnly: Boolean = false
        ): PermissionResult {
            val permissionList: MutableList<String> = mutableListOf()
            permissionList.add(permission)
            return checkAndRequestPermissions(
                activity,
                permissionList,
                requestCode,
                checkStatusOnly
            )
        }

        fun askUserToRequestPermissionExplicitly(context: Context){
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        }

        private fun hasPermissionAllowed(activity: Activity, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        private fun isNeededToShowRequestRational(activity: Activity, permission: String): Boolean {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }

        private fun requestPermissions(
            activity: Activity,
            permissionList: MutableList<String>,
            requestCode: Int
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(permissionList.toTypedArray(), requestCode)

                val permissionPreference = PermissionPreference(activity)

                for(permission in permissionList){
                    permissionPreference.setPermissionRequested(permission)
                }
            }
        }
    }
}