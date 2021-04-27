package project.android.get_location.usecase

class DisableLocationUC {
    operator fun invoke(getLocationFeature: GetLocationFeature){
        getLocationFeature.disableGetLocation()
    }
}