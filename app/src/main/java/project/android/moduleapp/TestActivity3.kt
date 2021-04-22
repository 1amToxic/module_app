package project.android.moduleapp

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import project.android.get_location.usecase.GetLocationFeature
import project.android.get_location.usecase.GetLocationFeatureImpl
import project.android.get_location.usecase.GetLocationUC
import project.android.get_location.usecase.Response
import timber.log.Timber

class TestActivity3 : AppCompatActivity() {
    lateinit var getLocationFeatureImpl: GetLocationFeatureImpl
    private val permission = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private var listPermissionReject = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test3)
        val getLocationUC = GetLocationUC()
        getLocationFeatureImpl = GetLocationFeatureImpl{
            checkTypeResponse(it)
        }
        getLocationUC(getLocationFeatureImpl,this)
    }
    private fun checkTypeResponse(response: Response){
        when(response.status){
            GetLocationFeature.Status.ACCEPT->{
                Timber.d("Debug ${response.value}")
                Toast.makeText(this,"Location ${response.value?.latitude} $$ ${response.value?.longitude}",Toast.LENGTH_SHORT).show()
            }
            GetLocationFeature.Status.BUG ->{
                Toast.makeText(this,"You have to check your Google Play Service",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun hasPermission(permission: String): Boolean {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            GetLocationFeatureImpl.REQUEST_CODE -> {
                val list = permission.toTypedArray()
                listPermissionReject.clear()
                list.forEach {
                    if (!hasPermission(it)) {
                        listPermissionReject.add(it)
                    }
                }
                if (listPermissionReject.size > 0) {
                    if (shouldShowRequestPermissionRationale(listPermissionReject[0])) {
                        AlertDialog.Builder(this)
                            .setMessage("These permissions are mandatory to get your location. You need to allow them.")
                            .setPositiveButton(
                                "OK"
                            ) { _, _ ->
                                requestPermissions(
                                    listPermissionReject.toTypedArray()
                                    , GetLocationFeatureImpl.REQUEST_CODE
                                )
                            }.setNegativeButton("Cancel", null).create().show()

                        return
                    }
                } else {
                    getLocationFeatureImpl.initGoogleApiClient()
                }
            }
        }
    }
}