package com.ckdroid.permissions

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ckdroid.dynamicpermissions.PermissionStatus
import com.ckdroid.dynamicpermissions.PermissionUtils
import kotlinx.android.synthetic.main.activity_permission_test.*

class PermissionTestActivity : AppCompatActivity() {

    private val REQUEST_PERMISSION_CODE = 12

    val requestPermissionList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_test)

        initializePermissionList()

        setClickListeners()
    }

    private fun initializePermissionList() {
        requestPermissionList.add(Manifest.permission.CAMERA)
        requestPermissionList.add(Manifest.permission.RECORD_AUDIO)
        requestPermissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissionList.add(Manifest.permission.WRITE_CONTACTS)
    }

    private fun setClickListeners() {
        btnRequestPermission.setOnClickListener {
            requestMultiplePermissions()
        }
    }

    private fun requestMultiplePermissions() {

        val permissionResult =
            PermissionUtils.checkAndRequestPermissions(
                this,
                requestPermissionList,
                REQUEST_PERMISSION_CODE
            )

        when (permissionResult.finalStatus) {
            PermissionStatus.ALLOWED -> {//DO further stuffs as all permissions are allowed by user
                Toast.makeText(
                    this,
                    "Permission is already allowed by user",
                    Toast.LENGTH_LONG
                ).show()
            }
            PermissionStatus.DENIED_PERMANENTLY -> {
                //Request user to allow permission by sending to permission list page
                //You can show customized dialog and then call this function
                Toast.makeText(this, "Permission is permanently denied by user", Toast.LENGTH_LONG)
                    .show()
                PermissionUtils.askUserToRequestPermissionExplicitly(this)
            }
            else -> {
                //Permission is requesting for first time or user denied permission before but not permanently
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            //Check status after user allowed or denied permission using the same way while requested permission
            val permissionResult =
                PermissionUtils.checkAndRequestPermissions(
                    this,
                    requestPermissionList,
                    REQUEST_PERMISSION_CODE,
                    checkStatusOnly = true
                )

            when (permissionResult.finalStatus) {
                PermissionStatus.ALLOWED -> {//DO further stuffs as all permissions are allowed by user
                    Toast.makeText(this, "Permission allowed by user", Toast.LENGTH_LONG).show()
                }
                PermissionStatus.DENIED_PERMANENTLY -> {
                    Toast.makeText(
                        this,
                        "Permission is permanently denied by user",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    //Permission denied by user but not permanently
                }
            }

            val cameraPermissionStatus =
                permissionResult.permissionStatus[Manifest.permission.CAMERA]

            when (cameraPermissionStatus) {
                PermissionStatus.ALLOWED -> {
                    //Allowed
                }
                PermissionStatus.DENIED_PERMANENTLY -> {
                    //Denied Permanently
                }
                else -> {
                    //Not given
                }
            }
        }
    }
}
