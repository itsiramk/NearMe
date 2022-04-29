package com.adyen.android.assignment.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.adyen.android.assignment.R
import com.adyen.android.assignment.adapter.PlacesAdapter
import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.utils.PermissionUtils
import com.adyen.android.assignment.utils.Resource
import com.adyen.android.assignment.utils.Status
import com.adyen.android.assignment.viewmodel.PlacesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val TAG = "MainActivity"
        private const val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    }

    private lateinit var permissionUtils: PermissionUtils
    private val placesViewModel: PlacesViewModel by viewModels()
    @Inject
    lateinit var adapter: PlacesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissionUtils = PermissionUtils(this, locationPermission)
        setupUI()
        setupAPICall()
        observeViewModel()
    }

    private fun setupUI() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PlacesAdapter()
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
    }

    private fun setupAPICall() {
        val query = VenueRecommendationsQueryBuilder()
            .setLatitudeLongitude(52.376510, 4.905890)
            .build()
       placesViewModel.fetchVenueRecommendations(query)
    }

    private fun observeViewModel() {
        placesViewModel.placesLiveData.observe(this, Observer {places ->
            places?.let {
                recyclerView.visibility = View.VISIBLE
                renderList(places)
            }
        })
        placesViewModel.usersLoadError.observe(this, Observer { isError ->
            tvError.visibility = if(isError == "") View.GONE else View.VISIBLE
        })
        placesViewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let {
                progressBar.visibility = if(it) View.VISIBLE else View.GONE
                if(it) {
                    tvError.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                }
            }
        })
    }

    private fun renderList(placesData: List<Result>) {
        adapter.apply {
            addData(placesData)
            notifyDataSetChanged()
        }
    }

    private fun fetchUserCurrentLocation(){}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (LOCATION_PERMISSION_REQUEST_CODE == requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted successfully")
                Toast.makeText(this, "Permission granted successfully", Toast.LENGTH_LONG).show()
            } else {
                permissionUtils.setShouldShowStatus()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        checkIfPermissionGranted()
    }

    private fun checkIfPermissionGranted(){
        if (ContextCompat.checkSelfPermission(this, locationPermission) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionUtils.neverAskAgainSelected()
                ) {
                    permissionUtils.displayNeverAskAgainDialog()
                } else {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }
        }
    }
}