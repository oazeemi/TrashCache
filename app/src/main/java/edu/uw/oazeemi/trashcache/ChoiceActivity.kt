package edu.uw.oazeemi.trashcache
import android.content.Intent
import android.graphics.Bitmap
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.R.attr.data
import android.hardware.Camera
import android.support.v4.app.NotificationCompat.getExtras
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class ChoiceActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 111
    private val mDatabase = FirebaseDatabase.getInstance().reference;
    private var itemDetected:ItemDetected? = null;

    private  val currentUser = FirebaseAuth.getInstance().getCurrentUser()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice)

        val photoSearchBtn: Button = findViewById(R.id.btn_photo_search)
        photoSearchBtn.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        val locationSearch: Button = findViewById(R.id.btn_location)
        locationSearch.setOnClickListener {
            val intent =  Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        return when(item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
        return true
    }


}
