package project.android.moduleapp

import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import project.android.get_location.usecase.*


class MainActivity : AppCompatActivity() {
    lateinit var getLocationFeatureImpl: GetLocationFeatureImpl
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val getLocationUC = GetLocationUC()
        val disableLocationUC = DisableLocationUC()
        getLocationFeatureImpl = GetLocationFeatureImpl{
            checkTypeResponse(it)
        }
        getLocationUC(getLocationFeatureImpl,this)
        findViewById<Button>(R.id.disable_get_location).setOnClickListener {
            disableLocationUC(getLocationFeatureImpl)
        }
    }
    private fun checkTypeResponse(response: Response){
        when(response.status){
            GetLocationFeature.Status.ACCEPT->{
                val location : Location = response.value as Location
                Toast.makeText(this,"Location ${location.latitude} $$ ${location.longitude}",Toast.LENGTH_SHORT).show()
            }
            GetLocationFeature.Status.BUG ->{
                Toast.makeText(this,"You have to check your Google Play Service",Toast.LENGTH_SHORT).show()
            }
            GetLocationFeature.Status.DENY ->{
                Toast.makeText(this,"Oops! Some permission has been Deny ${response.value.toString()}",Toast.LENGTH_SHORT).show()
            }
            GetLocationFeature.Status.DISABLE ->{
                Toast.makeText(this, response.value.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }

}