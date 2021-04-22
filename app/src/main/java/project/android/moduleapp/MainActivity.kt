package project.android.moduleapp

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationListener
import project.android.moduleapp.databinding.ActivityMainBinding
import timber.log.Timber


class MainActivity : AppCompatActivity(), LocationListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    companion object {
        val REQUEST_CODE: Int = 1231
        val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
        val UPDATE_INTERVAL: Long = 5000
        val FASTEST_INTERVAL: Long = 5000
    }

    private var location: Location? = null
    private var listPermissionReject = mutableListOf<String>()
    private var listPermission = mutableListOf<String>()
    lateinit var locationRequest: LocationRequest
    var googleApiClient: GoogleApiClient? = null
    lateinit var binding: ActivityMainBinding
    val permission = mutableListOf<String>(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermission()
    }

    override fun onResume() {
        super.onResume()
        if (!checkPlayService()) {
            Timber.d("Debug Error Service")
        }
    }

    private fun checkPlayService(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
            } else {
                finish()
            }
            return false
        }
        return true
    }

    private fun requestPermission() {
        if(permissionToRequest(permission).isNotEmpty()) {
            requestPermissions(
                permissionToRequest(permissionToRequest(permission)).toTypedArray(),
                REQUEST_CODE
            )
        }
    }

    private fun permissionToRequest(listPermission: List<String>): List<String> {
        val listPermission = mutableListOf<String>()
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        return listPermission
    }

    private fun hasPermission(permission: String): Boolean {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                Timber.d("Debug 2")
                val list = permissionToRequest(permission)
                list.forEach {
                    if (!hasPermission(it)) {
                        listPermissionReject.add(it)
                    }
                }
                if (listPermissionReject.size > 0) {
                    Timber.d("Debug 3")
                    if (shouldShowRequestPermissionRationale(listPermissionReject[0])) {
                        AlertDialog.Builder(this@MainActivity)
                            .setMessage("These permissions are mandatory to get your location. You need to allow them.")
                            .setPositiveButton(
                                "OK",
                                DialogInterface.OnClickListener { dialogInterface, i ->
                                    requestPermissions(
                                        listPermissionReject.toTypedArray()
                                        , REQUEST_CODE
                                    )
                                }).setNegativeButton("Cancel", null).create().show()

                        return
                    }
                } else {
                    googleApiClient = GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build()
                    if (!checkPlayService()) {
                        Timber.d("Debug Error Service")
                    }
                    Timber.d("Debug Else Switch case")
                    googleApiClient?.let {
                        it.connect()
                    }
                }
            }
            else ->{

            }
        }
    }

    override fun onConnected(p0: Bundle?) {
        Timber.d("Debug 4 Connected")
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        location?.let {
            Timber.d("$location")
        }

        startLocationUpdates()

    }

    private fun startLocationUpdates() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = UPDATE_INTERVAL
        locationRequest.fastestInterval = FASTEST_INTERVAL
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                this,
                "You need to enable permissions to display location !",
                Toast.LENGTH_SHORT
            ).show()
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
            googleApiClient,
            locationRequest,
            this
        )

    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun onLocationChanged(location: Location) {
        location.let {
            Timber.d("Debug $it")
        }
    }

}