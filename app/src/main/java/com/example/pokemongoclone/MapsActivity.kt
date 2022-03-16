package com.example.pokemongoclone

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.pokemongoclone.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //-----------------------------------Instance Variables-------------------------------------//

    private var USER_LOCATION_REQUEST_CODE: Int = 1000
    private var playerLocation: Location? = null
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var pokemonCharacters: ArrayList<PokemonCharacters> = ArrayList()
    private var oldLocationOfPlayer: Location? = null

    //-----------------------------------UI Variables-------------------------------------//

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    //-----------------------------------Create Program-------------------------------------//

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = PlayerLocationListener()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        requestLocationPermission()
        initializePokemonCharacters()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it insideAthe SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Brampton and move the camera
        val pLoc = LatLng(playerLocation!!.latitude, playerLocation!!.longitude)
        mMap.addMarker(MarkerOptions().position(pLoc).title("Hi, I am the player")
            .snippet("Let's go")
            .icon(BitmapDescriptorFactory.fromResource((R.drawable.player))))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pLoc))
    }
    //-----------------------------------Ask Permission-------------------------------------//

    private fun requestLocationPermission(){

        if(Build.VERSION.SDK_INT>=23){
            if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    ,USER_LOCATION_REQUEST_CODE)
                return
            }
        }

        accessUserLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == USER_LOCATION_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                accessUserLocation()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    inner class PlayerLocationListener: LocationListener{

        constructor(){
            playerLocation = Location("MyProvider")
            playerLocation?.latitude = 0.0
            playerLocation?.latitude = 0.0
        }
        override fun onLocationChanged(updatedLocation: Location) {
            playerLocation=updatedLocation
        }
        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
        }

        override fun onProviderDisabled(provider: String) {


        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            super.onStatusChanged(provider, status, extras)
        }
    }

    private fun initializePokemonCharacters(){

        pokemonCharacters.add(PokemonCharacters(
            "Hello, this is c1",
            "I'm powerful",
            R.drawable.c1,
            1.651729,
            31.996134
        ))
        pokemonCharacters.add(PokemonCharacters(
            "Hello, this is c2",
            "I'm powerful",
            R.drawable.c2,
            27.404523,
            29.647654
        ))
        pokemonCharacters.add(PokemonCharacters(
            "Hello, this is c3",
            "I'm powerful",
            R.drawable.c3,
            10.492703,
            10.709112
        ))
        pokemonCharacters.add(PokemonCharacters(
            "Hello, this is c4",
            "I'm powerful",
            R.drawable.c4,
            28.220750,
            1.898764
        ))
    }

    private fun accessUserLocation(){
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            1000 , 2f, locationListener!!)

        var newThread = NewThread()
        newThread.start()
    }

    inner class NewThread: Thread {

        constructor(): super() {
            oldLocationOfPlayer = Location("MyProvider")
            oldLocationOfPlayer?.latitude=0.0
            oldLocationOfPlayer?.longitude=0.0
        }

        override fun run() {
            super.run()

            while(true){
                if (oldLocationOfPlayer?.distanceTo(playerLocation)==0f){
                    continue
                }
                oldLocationOfPlayer= playerLocation
                try{
                    runOnUiThread{
                        mMap.clear()
                        val pLocation = LatLng(playerLocation!!.latitude, playerLocation!!.longitude)
                        mMap.addMarker(MarkerOptions().position(pLocation).title("Hi, I am the player")
                            .snippet("Let's go")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.player)))
                        mMap.moveCamera((CameraUpdateFactory.newLatLng(pLocation)))

                        for (pokemon in pokemonCharacters){
                            if (pokemon.isKilled == false){
                                var pcLoc = LatLng(pokemon.location!!.latitude, pokemon.location!!.longitude)
                                mMap.addMarker(MarkerOptions()
                                    .position(pcLoc)
                                    .title(pokemon.titleOfPokemon)
                                    .snippet(pokemon.message)
                                    .icon(BitmapDescriptorFactory.fromResource(pokemon.iconOfPokemon!!))
                                )
                                if (playerLocation!!.distanceTo(pokemon.location)<1){
                                    Toast.makeText(this@MapsActivity,
                                        "${pokemon.titleOfPokemon} is eliminated",
                                        Toast.LENGTH_SHORT)
                                        .show()
                                    pokemon.isKilled=true
                                    pokemonCharacters[pokemonCharacters.indexOf(pokemon)] = pokemon
                                }
                            }

                        }
                        Thread.sleep(1000)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }


        }
    }
}