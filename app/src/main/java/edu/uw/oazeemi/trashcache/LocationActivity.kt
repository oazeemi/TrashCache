package edu.uw.oazeemi.trashcache

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.nfc.Tag
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity : AppCompatActivity() {

    private lateinit var listview: ListView
    private val REQUEST_CODE = 1
    private val TAG = "LocationActivity"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var coordinates: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        setupPermissions()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation.addOnSuccessListener {location: Location ->
            //coordinates.latitude = location.latitude
            //coordinates.longitude = location.longitude
        }

        downloadData()
        listview = findViewById(R.id.location_listView)
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        if(permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission denied")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Accepted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun downloadData() {
        val key = "5eb05dcbb906719f"
        val url = "http://api.earth911.com/"
        val testQuery = url + "earth911.getMaterials?api_key=" + key

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, testQuery, null,
                Response.Listener { response ->
                    Toast.makeText(this, "$response", Toast.LENGTH_SHORT).show()
                },
                Response.ErrorListener { error ->
                }
        )
        Singleton.getInstance(this).add(jsonObjectRequest)
    }
}
