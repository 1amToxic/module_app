package project.android.get_location.usecase

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import timber.log.Timber
import java.util.*

class GetLocationFeatureImpl(val callback : (Response) -> Unit) :
    GetLocationFeature,
    LocationListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    companion object {
        val REQUEST_CODE: Int = Random().nextInt(1000)+1000
        val PLAY_SERVICES_RESOLUTION_REQUEST = Random().nextInt(1000)+1000
        const val UPDATE_INTERVAL: Long = 5000
        const val FASTEST_INTERVAL: Long = 4000
    }
    private lateinit var activity : Activity
    private var location: Location? = null
    lateinit var locationRequest: LocationRequest
    var googleApiClient: GoogleApiClient? = null
//    val permission = mutableListOf(
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_COARSE_LOCATION
//    )

    override fun getLocation(activity : Activity) {
        this.activity = activity
        if (!checkPlayService()) {
            callback(Response(null,GetLocationFeature.Status.BUG))
        }
        val list = permissionToRequest()
        if(list.isEmpty()){
            initGoogleApiClient()
        }
        else{
            callback(Response(list,GetLocationFeature.Status.DENY))
        }

    }

    override fun checkPlayService(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(activity)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
            } else {
                activity.finish()
            }
            return false
        }
        return true
    }

    override fun permissionToRequest(): List<String> {
        val listPermission = mutableListOf<String>()
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        return listPermission
    }

    override fun hasPermission(permission: String): Boolean {
        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun startLocationUpdate() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = UPDATE_INTERVAL
        locationRequest.fastestInterval = FASTEST_INTERVAL
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            callback(Response(permissionToRequest(),GetLocationFeature.Status.DENY))
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
            googleApiClient,
            locationRequest,
            this
        )
    }

    override fun onLocationChanged(location: Location) {
        callback(Response(location,GetLocationFeature.Status.ACCEPT))
    }

    override fun onConnected(p0: Bundle?) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            callback(Response(permissionToRequest(),GetLocationFeature.Status.DENY))
            return
        }
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        location?.let {
            callback(Response(location,GetLocationFeature.Status.ACCEPT))
        }

        startLocationUpdate()
    }

    override fun initGoogleApiClient(){
        googleApiClient = GoogleApiClient.Builder(activity)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
        googleApiClient?.connect()
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

}