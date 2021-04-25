package project.android.get_location.usecase

import android.app.Activity
import android.location.Location
import android.view.ViewGroup

interface GetLocationFeature {
    fun getLocation(activity: Activity)
    fun checkPlayService() : Boolean
    fun permissionToRequest() : List<String>
    fun hasPermission(permission : String) : Boolean
    fun startLocationUpdate()
    fun initGoogleApiClient()
    enum class Status{
        DENY,
        ACCEPT,
        BUG
    }
}
data class Response(
    val value : Any?,
    val status : GetLocationFeature.Status
)