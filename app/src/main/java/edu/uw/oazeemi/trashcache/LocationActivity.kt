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
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONException
import org.json.JSONObject

class LocationActivity : AppCompatActivity() {

    private lateinit var listview: ListView
    private val REQUEST_CODE = 1
    private val TAG = "LocationActivity"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationCode: MutableList<String> = mutableListOf()
    private var download = false
    private val locationData: MutableList<LocationData> = mutableListOf()
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null


    data class LocationData(var pcode: String = "", var descr: String = "", var city: String = "", var address: String = "", var phone: String = "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Recycling Facilities"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        setupPermissions()
        getLocation()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission denied")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Accepted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        var latitude = 0.0
        var longitude = 0.0

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location ->
            downloadData(location.latitude, location.longitude)
        }
    }

    private fun downloadData(latitude : Double, longitude : Double) {
        val key = getString(R.string.earth_key)
        val url = "http://api.earth911.com/"
        var query = ""
        if (download) {
            for (i in 0 until locationCode.size) {
                query = url + "earth911.getLocationDetails?api_key=" + key + "&location_id=" + locationCode[i]
                val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, query, null,
                        Response.Listener { response ->
                            parseLocation(response.toString(), i)
                        },
                        Response.ErrorListener { error ->
                            Toast.makeText(this, "$error", Toast.LENGTH_LONG).show()
                        }
                )
                Singleton.getInstance(this).add(jsonObjectRequest)
            }
        } else {
            Toast.makeText(this, "$latitude", Toast.LENGTH_SHORT).show()
            query = url + "earth911.searchLocations?api_key=" + key + "&latitude=" + latitude + "&longitude=" + longitude + "&max_distance=2"
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, query, null,
                    Response.Listener { response ->
                        parseData(response.toString())
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(this, "$error", Toast.LENGTH_LONG).show()
                    }
            )
            Singleton.getInstance(this).add(jsonObjectRequest)
        }
    }

    private fun parseData(response: String) {
        try {
            val JsonArray = JSONObject(response).getJSONArray("result")
            for (i in 0 until JsonArray.length()) {
                val locationObject = JsonArray.getJSONObject(i)
                locationCode.add(locationObject.getString("location_id"))
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Error parsing JSON", e)
        }
        download = true
        getLocation()
    }

    private fun parseLocation(response: String, index: Int) {
        try {
            val JsonArray = JSONObject(response).getJSONObject("result").getJSONObject(locationCode[index])
            val data = LocationActivity.LocationData()
            data.address = JsonArray.getString("address")
            data.city = JsonArray.getString("city")
            data.descr = JsonArray.getString("description")
            data.pcode = JsonArray.getString("postal_code")
            data.phone = JsonArray.getString("phone")
            locationData.add(data)
        } catch (e: JSONException) {
            Log.e(TAG, "Error parsing JSON", e)
        }
        val recyclerView = findViewById<RecyclerView>(R.id.location_view)

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = RecyclerAdapter(this, locationData)
    }
}
