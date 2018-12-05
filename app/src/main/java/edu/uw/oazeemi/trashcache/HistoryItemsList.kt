package edu.uw.oazeemi.trashcache


import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.NetworkImageView

import kotlinx.android.synthetic.main.historyitem_list_content.view.*


/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [NewsArticleDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class HistoryItemsList : AppCompatActivity() {

    private val TAG = "ActivityMain"
    private lateinit var API_KEY:String
    private var endpoint:String = "top-headlines"
    private var queryParams:String = "?country=us&language=en"
    private var BASE_URL = "https://newsapi.org/v2/"
    private var url:String? = null

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    private var historyItems = mutableListOf<HistoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_newsarticle_list)
        setContentView(R.layout.activity_historyitem_list)
//        setSupportActionBar(toolbar)
//        toolbar.title = title
//
//        API_KEY = getString(R.string.NEW_API_KEY)
//        url = "$BASE_URL$endpoint$queryParams&apiKey=$API_KEY"
//
//
//        fab.setOnClickListener { view ->
//            downloadArticles(url!!)
//        }

//        if (newsarticle_detail_container != null) {
//            // The detail container view will be present only in the
//            // large-screen layouts (res/values-w592dp).
//            // If this view is present, then the
//            // activity should be in two-pane mode.
//            twoPane = true
//            val simpleFragment = SimpleFragment.newInstance("Welcome to Material News!", "powered by NewsAPI.org")
//            supportFragmentManager.beginTransaction()
//                    .add(R.id.newsarticle_detail_container, simpleFragment)
//                    .addToBackStack("fTrans")
//                    .commit()
//        }
//
//        downloadArticles(url!!)
    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                endpoint = "everything"
                queryParams = "?q=$query"
                url = "$BASE_URL$endpoint$queryParams&apiKey=$API_KEY"

                downloadArticles(url!!)
            }
        }
    }

    override fun onSearchRequested(): Boolean {
        return super.onSearchRequested()
    }

    fun downloadArticles(url: String) {

//        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
//                Response.Listener {response ->
//                    articles = parseNewsAPI(response) as MutableList<NewsArticle>
//                    setupRecyclerView(newsarticle_list)
//                },
//
//                Response.ErrorListener {
//                    Log.e(TAG, it.toString())
//                }
//
//        )
//        VolleyService.getInstance(this).add(jsonObjectRequest)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {

        if(twoPane) {
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
        else {
            recyclerView.layoutManager = GridLayoutManager(this,2)
        }
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, historyItems, twoPane)
    }

    class SimpleItemRecyclerViewAdapter(
            private val parentActivity: HistoryItemsList,
            private val values: List<HistoryItem>,
            private val twoPane: Boolean
    ) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
//                showDetailsView(v)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.historyitem_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]

//            holder.image.setImageUrl(item.imageUrl, VolleyService.getInstance(parentActivity).imageLoader)//, VolleyService.getInstance(this).imageLoader))

//            holder.headline.text = item.headline

//            if(item.imageUrl == null) {
//                holder.image.setDefaultImageResId(R.drawable.no_image_available)
//            }

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val image:NetworkImageView = view.article_img
            val headline: TextView = view.headline
        }

//        fun showDetailsView(v: View) {
//            val article = v.tag as NewsArticle
//
//            if (twoPane) {
//                val fragment = NewsArticleDetailFragment.newInstance(article.headline, article.description, article.sourceName, article.imageUrl)
//
//                val trans = parentActivity.supportFragmentManager.beginTransaction()
//
//                trans.replace(R.id.newsarticle_detail_container, fragment)
//                trans.addToBackStack("fTrans")
//                trans.commit()
//            } else {
//                val intent = Intent(v.context, NewsArticleDetailActivity::class.java).apply {
//                    putExtra("ARTICLE", article)
//                }
//                val options:ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(parentActivity, v, "animate_image")
//                v.context.startActivity(intent, options.toBundle())
//            }
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the options menu from XML
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)

        // Get the SearchView and set the searchable configuration
//        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        (menu.findItem(R.id.menu_search).actionView as SearchView).apply {
//            // Assumes current activity is the searchable activity
//            setSearchableInfo(searchManager.getSearchableInfo(componentName))
//            setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
//        }

        // Define the listener
        val expandListener = object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                // Do something when action item collapses
                return true // Return true to collapse action view
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                // Do something when expanded
                return true // Return true to expand action view
            }
        }

        // Get the MenuItem for the action item
//        val actionMenuItem = menu?.findItem(R.id.menu_search)

        // Assign the listener to that action item
//        actionMenuItem?.setOnActionExpandListener(expandListener)

        // Any other things you have to do when creating the options menu...

        return true
    }


}