package com.example.lbs

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lbs.common.NetWorkVerification
import com.example.lbs.databinding.ActivityMainBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission

const val LBS_CHECK_TAG = "LBS_CHECK_TAG"
const val LBS_CHECK_CODE = 100

class MainActivity : AppCompatActivity() {

    /**
     * Fused Location Provider Api 에서
     * 위치 업데이트를위한 서비스 품질등 다양한요청을
     * 설정하는데 사용하는 객체.
     */
    private lateinit var mLocationRequest: LocationRequest

    /**
     * 현재 위치정보를 나타내는 객체
     */
    private lateinit var mCurrentLocation: Location

    /**
     * 현재 위치제공자(Provider)와 상호작용하는 진입점
     */
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private lateinit var locationCallback : LocationCallback

    private lateinit var binding: ActivityMainBinding
    private val data = arrayListOf<com.example.lbs.common.Location>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (NetWorkVerification.isNetworkAvailable(this)) {
            checkMyPermissionLocation()
        }

        binding.rvLocation.layoutManager = LinearLayoutManager(this)
        binding.rvLocation.adapter = MainAdapter(data)

        with(binding){
            button3.setOnClickListener {
                checkLocationCurrentDevice()
            }
            button4.setOnClickListener {
                mFusedLocationClient.removeLocationUpdates(locationCallback)
            }

        }

    }

    private val permissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            checkLocationCurrentDevice()
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            finish()
        }
    }

    private fun checkMyPermissionLocation() {
        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setRationaleMessage("지도를 사용하기 위해서는 위치제공 허락이 필요합니다")
            .setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).check()
    }

    private fun checkLocationCurrentDevice() {
        val locationIntervalTime = 5000L
        mLocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, locationIntervalTime)
                /**
                 * 정확한 위치를 기다림: true 일시 지하, 이동 중일 경우 늦어질 수 있음
                 */
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(locationIntervalTime) //위치 획득 후 update 되는 최소 주기
                .setMaxUpdateDelayMillis(locationIntervalTime).build() //위치 획득 후 update delay 최대 주기


        val lbsSettingsRequest: LocationSettingsRequest =
            LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest).build()
        val settingClient: SettingsClient = LocationServices.getSettingsClient(this)
        val taskLBSSettingResponse: Task<LocationSettingsResponse> =
            settingClient.checkLocationSettings(lbsSettingsRequest)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        /**
         * 위치 정보설정이 On 일 경우
         */
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.locations[0]
                mCurrentLocation = location
                val latitude: Double = location.latitude
                val longitude: Double = location.longitude
                Log.e("asd", latitude.toString())
                Toast.makeText(
                    this@MainActivity,
                    "$latitude, $longitude",
                    Toast.LENGTH_SHORT
                ).show()

                (binding.rvLocation.adapter as MainAdapter).addData(com.example.lbs.common.Location(latitude, longitude))
            }
        }

        taskLBSSettingResponse.addOnSuccessListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@addOnSuccessListener
            }

            mFusedLocationClient.requestLocationUpdates(
                mLocationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
        /**
         * 위치 정보설정이 OFF 일 경우
         */
        taskLBSSettingResponse.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    Toast.makeText(
                        applicationContext,
                        "위치정보 설정은 반드시 On 상태여야 해요!",
                        Toast.LENGTH_SHORT
                    ).show()
                    /**
                     * 위치 설정이 되어있지 않을 시 대응방안을 정의
                     * 여기선 onActivityResult 를 이용해 대응한다
                     */
                    exception.startResolutionForResult(
                        this,
                        LBS_CHECK_CODE
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(LBS_CHECK_TAG, sendEx.message.toString())
                }
            }
        }
    }


}