package edu.uw.oazeemi.trashcache

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

import kotlinx.android.synthetic.main.activity_recycle_information.*
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text

class RecycleInformation : AppCompatActivity() {

    private val TAG = "RecycleInformation"
    private var itemsID: MutableList<Int> = mutableListOf()
    private var statement: String = ""
    private var allItems: MutableList<ItemsData> = mutableListOf()
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

    data class ItemsData(var image: String = "", var name: String = "", var descr: String = "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycle_information)
        downloadData()
    }

    private fun downloadAllData() {
        val key = getString(R.string.earth_key)
        val url = "http://api.earth911.com/earth911.getMaterials?api_key=" + key
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    parseAllData(response.toString())
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, "$error", Toast.LENGTH_LONG).show()
                }
        )
        Singleton.getInstance(this).add(jsonObjectRequest)
    }

    private fun downloadData() {
        val key = getString(R.string.earth_key)
        val myintent = intent
        val string = myintent.getStringExtra("item")
        val url = "http://api.earth911.com/earth911.searchMaterials?"
        val query = url + "api_key=" + key + "&query=" + string + "&max_results=5"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, query, null,
                Response.Listener { response ->
                    Log.v(TAG, "$response")
                    parseData(response.toString())
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, "$error", Toast.LENGTH_LONG).show()
                }
        )
        Singleton.getInstance(this).add(jsonObjectRequest)
    }

    private fun parseData(response: String) {
        try {
            itemsID = mutableListOf()
            val JsonArray = JSONObject(response).getJSONArray("result")
            if (JsonArray.length() == 0) {
                statement = "Not Recyclable"
            }
            for (i in 0 until JsonArray.length()) {
                val recycleObj = JsonArray.getJSONObject(i)
                val exact = recycleObj.getString("exact").toBoolean()
                if (exact) {
                    statement = "Recyclable"
                    val item = recycleObj.getString("material_id").toInt()
                    itemsID.add(item)
                    break
                } else {
                    statement = "No perfect match. Related items."
                    val item = recycleObj.getString("material_id").toInt()
                    itemsID.add(item)
                }
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Error parsing JSON", e)
        }
        val textView = findViewById<TextView>(R.id.recycle_heading)
        textView.text = statement
        downloadAllData()
    }

    private fun parseAllData(response: String) {
        try {
            val JsonArray = JSONObject(response).getJSONArray("result")
            for (i in 0 until JsonArray.length()) {
                val allData = ItemsData()
                val recycleObj = JsonArray.getJSONObject(i)
                val id = recycleObj.getString("material_id").toInt()
                if (id in itemsID) {
                    allData.descr = recycleObj.getString("long_description")
                    allData.name = recycleObj.getString("description")
                    allData.image = recycleObj.getString("image")
                    allItems.add(allData)
                }
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Error parsing JSON", e)
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recycle_view)

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = RecyclingAdapter(this, allItems)
    }
}
