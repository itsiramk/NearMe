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
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.adyen.android.assignment.R
import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.utils.ActivityUtils
import com.adyen.android.assignment.utils.PermissionUtils
import com.adyen.android.assignment.viewmodel.PlacesViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.android.synthetic.main.item_marker_details.*


@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    }

    private lateinit var permissionUtils: PermissionUtils
    private val placesViewModel: PlacesViewModel by viewModels()
    private lateinit var currentLocation: Location
    private var nearbylocationList: List<Result> = ArrayList()
    private var isPermissionGranted: Boolean = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionUtils = PermissionUtils(requireActivity(), locationPermission)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
            requireActivity()
        )
        observeViewModel()
    }

    @SuppressLint("MissingPermission")
    private fun fetchUserCurrentLocation() {
        if (isPermissionGranted) {
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
            places?.let { nearbylocationList = it }
        })
        placesViewModel.usersLoadError.observe(viewLifecycleOwner, { isError ->
            if (isError.toString().isNotEmpty())
                permissionUtils.showAlert(
                    isError.toString(),
                    requireContext().getString(R.string.ok)
                )
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
                    permissionUtils.showAlert(
                        requireContext().getString(R.string.location_permission_msg),
                        requireContext().getString(R.string.permit_manually)
                    )
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

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val currentLocationMarker =
            MarkerOptions().position(latLng).title("Your current location")
        googleMap.addMarker(currentLocationMarker)?.showInfoWindow()
        val marker: View =
            (requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.layout_marker_title,
                null
            )
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(
            ActivityUtils.createDrawableFromView(
                requireActivity(),
                marker
            )
        )
        imgMyLocation.setOnClickListener {
            if (nearbylocationList.isNullOrEmpty()) {
                fetchUserCurrentLocation()
            }
            for (i in nearbylocationList.indices) {
                    googleMap.addMarker(MarkerOptions().position(LatLng(nearbylocationList[i].geocodes.main.latitude,nearbylocationList[i].geocodes.main.longitude))
                            .icon(bitmapDescriptor).snippet(i.toString())
                    )
                }
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        googleMap.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker.snippet != null) {
            val position: Int? = marker.snippet?.toInt()
            val dialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.layout_bottom_sheet, null)
            val btnClose = view.findViewById<Button>(R.id.idBtnDismiss)
            val tvItemName = view.findViewById<TextView>(R.id.tvItemName)
            val tvItemDetails = view.findViewById<TextView>(R.id.tvItemDetails)
            val tvError = view.findViewById<TextView>(R.id.tvError)
            if (position != null) {
                val name = nearbylocationList[position].name
                val address = nearbylocationList[position].location.formatted_address
                if (name.isNotEmpty()) {
                    tvItemName.visibility = View.VISIBLE
                    tvItemName.text = name
                }
                if (address.isNotEmpty()) {
                    tvItemDetails.visibility = View.VISIBLE
                    tvItemDetails.text = address
                }
                if (name.isNullOrEmpty() && name.isNullOrEmpty()) {
                    tvError.visibility = View.VISIBLE
                    tvError.text = requireContext().getString(R.string.no_data)
                }
            }
            btnClose.setOnClickListener {
                dialog.dismiss()
            }
            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()
        }
        return false
    }

}