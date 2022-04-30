package com.adyen.android.assignment.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.adyen.android.assignment.R
import com.adyen.android.assignment.adapter.PlacesAdapter
import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.api.model.GeoCode
import com.adyen.android.assignment.utils.ActivityUtils
import com.adyen.android.assignment.utils.PermissionUtils
import com.adyen.android.assignment.viewmodel.PlacesViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_maps.*
import javax.inject.Inject


@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    }

    private lateinit var mMap : GoogleMap
    private lateinit var permissionUtils: PermissionUtils
    private val placesViewModel: PlacesViewModel by viewModels()
    private lateinit var currentLocation: Location
    private var nearbylocationList: MutableList<GeoCode> = ArrayList()
    private var isPermissionGranted: Boolean = false
    private lateinit var mView: View

    @Inject
    lateinit var adapter: PlacesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_maps, container, false)
        mView = v
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionUtils = PermissionUtils(requireActivity(), locationPermission)
        observeViewModel()
    }

    private fun plotOnMap(map: GoogleMap) {
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
        map.animateCamera(cameraUpdate)
        val marker: View =
            (requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.layout_marker_title,
                null
            )
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(ActivityUtils.createDrawableFromView(requireActivity(), marker))
        for (i in nearbylocationList) {
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(i.main.latitude, i.main.longitude))
                    .icon(bitmapDescriptor)
            )
        }
    }
    @SuppressLint("MissingPermission")
    private fun fetchUserCurrentLocation() {
        if (isPermissionGranted) {

            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationProviderClient.lastLocation.addOnSuccessListener(requireActivity()) {
                if (it != null) {
                    currentLocation = it
                    val mapFragment =
                        childFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment?
                    mapFragment?.getMapAsync(this)
                    setupAPICall(it.latitude, it.longitude)
                }
            }

        }
    }

    private fun setupAPICall(latitude: Double, longitude: Double) {
        val query = VenueRecommendationsQueryBuilder()
            .setLatitudeLongitude(latitude, longitude)
            .build()
        placesViewModel.fetchVenueRecommendations(query)
    }

    private fun observeViewModel() {
        placesViewModel.placesLiveData.observe(viewLifecycleOwner, { places ->
            places?.let { resultList ->
                for (i in resultList) {
                    nearbylocationList.add(i.geocodes)
                }
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (LOCATION_PERMISSION_REQUEST_CODE == requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = true
            } else {
                permissionUtils.setShouldShowStatus()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkIfPermissionGranted()
    }

    private fun checkIfPermissionGranted() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                locationPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionUtils.neverAskAgainSelected()
                ) {
                    permissionUtils.displayNeverAskAgainDialog()
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }
        } else {
            isPermissionGranted = true
            fetchUserCurrentLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        fetchUserCurrentLocation()
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val currentLocationMarker =
            MarkerOptions().position(latLng).title("Your current location")
        val marker: View =
            (requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.layout_marker_title,
                null
            )
        val tvMarkerLocation = marker.findViewById<View>(R.id.txt_marker_title) as TextView
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(
            ActivityUtils.createDrawableFromView(
                requireActivity(),
                marker
            )
        )
        for (i in nearbylocationList) {
            googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(i.main.latitude, i.main.longitude))
                    .icon(bitmapDescriptor)
            )
        }
        tvMarkerLocation.text = requireActivity().getString(R.string.click)
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        googleMap.addMarker(currentLocationMarker)?.showInfoWindow()
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return false;
    }

}