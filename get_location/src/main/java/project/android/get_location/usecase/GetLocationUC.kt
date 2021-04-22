package project.android.get_location.usecase

import android.app.Activity
import android.view.ViewGroup
import timber.log.Timber

class GetLocationUC{
    operator fun invoke(getLocationFeature: GetLocationFeature,activity: Activity){
        getLocationFeature.getLocation(activity)
    }
}