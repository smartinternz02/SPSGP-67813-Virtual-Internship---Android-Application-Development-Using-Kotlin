package com.rahulpa.myapplication

import android.annotation.SuppressLint
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.widget.SearchView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.rahulpa.myapplication.api.NearbyPlacesResponse
import com.rahulpa.myapplication.api.PlacesService
import com.rahulpa.myapplication.databinding.ActivityMapsBinding
import com.rahulpa.myapplication.model.Place
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = "MainActivity"
    private var currentLocation: Location? = null
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesService: PlacesService
    private lateinit var mapFragment: SupportMapFragment
    private var places: List<Place>? = null
    private var markers: MutableList<Marker> = emptyList<Marker>().toMutableList()
    private var rMarker:MutableList<Marker> = emptyList<Marker>().toMutableList()
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        searchView = findViewById(R.id.idSearchView)
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        placesService = PlacesService.create()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment.getMapAsync(this)
        setUpMaps()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                for(marker in rMarker){
                    marker.remove()
                }

                currentLocation?.let {
                    if (query != null) {
                        getNearbyPlaces(it,query)
                    }
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return  false
            }
        })
        val btn = findViewById<Button>(R.id.currentLoc)
        btn.setOnClickListener {
            setUpMaps()
        }

    }

    @SuppressLint("MissingPermission")
    private fun setUpMaps() {
        mapFragment.getMapAsync { googleMap ->
            googleMap.isMyLocationEnabled = true
            getCurrentLocation {
                googleMap.addMarker(
                    MarkerOptions().title("Your Location").position(it.latLng))
                val pos = CameraPosition.fromLatLngZoom(it.latLng, 15f)
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos))

            }
            mMap = googleMap
        }
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.setPadding(0, 0, 10, 0)

    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(onSuccess: (Location) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            currentLocation = location
            onSuccess(location)
        }.addOnFailureListener {
            Log.e(TAG, "Could not get location")
        }
    }






    private fun getNearbyPlaces(location: Location,query:String ) {
        val apiKey = this.getString(R.string.google_maps_keys)
        placesService.nearbyPlaces(
            apiKey = apiKey,
            location = "${location.latitude},${location.longitude}",
            radiusInMeters = 10000,
            placeType = query
        ).enqueue(
            object : Callback<NearbyPlacesResponse> {
                override fun onFailure(call: Call<NearbyPlacesResponse>, t: Throwable) {
                    Log.e(TAG, "Failed to get nearby places", t)
                }

                override fun onResponse(
                    call: Call<NearbyPlacesResponse>,
                    response: Response<NearbyPlacesResponse>
                ) {
                    if (!response.isSuccessful) {
                        Log.e(TAG, "Failed to get nearby places")
                        return
                    }

                    val places = response.body()?.results ?: emptyList()
                    this@MapsActivity.places = places
                    addPlaces()
                }
            }
        )
    }

    private fun addPlaces() {
        val currentLocation = currentLocation
        if (currentLocation == null) {
            Log.w(TAG, "Location has not been determined yet")
            return
        }

        val places = places
        if (places == null) {
            Log.w(TAG, "No places to put")
            return
        }

        for (place in places) {

            // Add the place in maps
            mMap.let {
                val marker = it.addMarker(
                    MarkerOptions()
                        .position(place.geometry.location.latLng)
                        .title(place.name)
                )
                if (marker != null) {
                    marker.tag = place
                    markers.add(marker)
                    rMarker.add(marker)
                }
            }
        }
    }

    val Location.latLng: LatLng
        get() = LatLng(this.latitude, this.longitude)


}