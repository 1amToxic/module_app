package project.android.get_location.usecase

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat

class GetLocationFeatureImpl(val callback : (Response) -> Unit) :
    GetLocationFeature,
    LocationListener {
    companion object {
        const val UPDATE_INTERVAL: Long = 5000
    }
    private lateinit var locationManager : LocationManager

    override fun getLocation(context: Context) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            var list = mutableListOf<String>()
            if(ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                list.add(Manifest.permission.ACCESS_FINE_LOCATION)
            if(ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                list.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            callback(Response(list,GetLocationFeature.Status.DENY))
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL,0F,this)

    }

    override fun disableGetLocation() {
        locationManager.removeUpdates(this)
        callback(Response("Disable Update Location Success",GetLocationFeature.Status.DISABLE))
    }


    override fun onLocationChanged(location: Location) {
        callback(Response(location,GetLocationFeature.Status.ACCEPT))
    }


}