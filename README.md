# Handle M+ Permissions in Android

This library is created to help developers to check the status of requested permission that either it is allowed, not given or user denied it permanently.

In Android, it is very tough to find that user denied permission permanently and so the solution is build in this library.

## Implementation

### Add below line of jitpack module in your project level build.gradle file

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### Add library in your app-level build.gradle file

```gradle
dependencies {
    implementation 'com.github.Promact:dynamic-permission-handling-android:1.0.0'
}
```

That's you added library and click `sync now` on top of your gradle file.

Now request permission and check the status of the requested permission.

### Request Multiple Permissions

```kotlin
    //define your list of permission as 
    val requestPermissionList: MutableList<String> = mutableListOf()
    requestPermissionList.add(Manifest.permission.CAMERA)
    requestPermissionList.add(Manifest.permission.RECORD_AUDIO)
    requestPermissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
    requestPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    requestPermissionList.add(Manifest.permission.WRITE_CONTACTS)
    
    //Call the function to to request permission which will return a result of permission
    val permissionResult =
            PermissionUtils.checkAndRequestPermissions(
                this@MainActivity,
                requestPermissionList,
                REQUEST_PERMISSION_CODE
            )
```

#### OR

### Request Single Permission

```kotlin
    //Call the function to to request permission which will return a result of permission
    val permissionResult =
            PermissionUtils.checkAndRequestPermissions(
                this@MainActivity,
                Manifest.permission.CAMERA,
                REQUEST_PERMISSION_CODE
            )
```

On requesting permission will return you a result which will hold a status of all permissions and a final status that all the requested permission is already allowed by a user, user not given it previously when requested or denied it permanenlty which can cause to not show a permission request dialog again.

### Check the current status for requested permission(s)

```kotlin
when (permissionResult.finalStatus) {
    PermissionStatus.ALLOWED -> {
        //DO further stuffs as all permissions are allowed by user
        Toast.makeText(this, "Permission is already allowed by user", Toast.LENGTH_LONG).show()
    }
    PermissionStatus.DENIED_PERMANENTLY -> {
        //Request user to allow permission by sending to permission list page
        //You can show customized dialog and then call this function
        Toast.makeText(this, "Permission is permanently denied by user", Toast.LENGTH_LONG).show()
        PermissionUtils.askUserToRequestPermissionExplicitly(this)
        //This function will redirect user to app detail page from where user can manually turn on the permission
    }
    else -> {
        //Permission is requesting for first time or user denied permission before but not permanently
        //If this block is executed then user will be requested a permission request dialog.
    }
}
```

Now check the result after user allowed or denied permission in **onRequestPermissionsResult()**

### Check the result when user requested a permission request dialog

```kotlin
override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
  super.onRequestPermissionsResult(requestCode, permissions, grantResults)
  if (requestCode == REQUEST_PERMISSION_CODE) {
    //Check status after user allowed or denied permission using the same way while requested permission
    val permissionResult = PermissionUtils.checkAndRequestPermissions(
        this@MainActivity,
        requestPermissionList,
        REQUEST_PERMISSION_CODE,
        checkStatusOnly = true //Passing this as true will just check and return status, will not re-ask for permission
    )

    when (permissionResult.finalStatus) {
        PermissionStatus.ALLOWED -> { 
            //DO further stuffs as all permissions are allowed by user
            Toast.makeText(this, "Permission allowed by user", Toast.LENGTH_LONG).show()
        }
        PermissionStatus.DENIED_PERMANENTLY -> {
            Toast.makeText(this, "Permission is permanently denied by user", Toast.LENGTH_LONG).show()
        }
        else -> {
            //Permission denied by user but not permanently
        }
    }
  }
}
```

### Check each requested permission result 

You can check each requested permission result that which permission user has allowed, not given or denied permanently

```kotlin
val cameraPermissionStatus = permissionResult.permissionStatus[Manifest.permission.CAMERA]

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
```
