package project.android.get_location.usecase

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import timber.log.Timber

class GetLocationUC{
    operator fun invoke(getLocationFeature: GetLocationFeature,context: Context){
        getLocationFeature.getLocation(context)
    }
}