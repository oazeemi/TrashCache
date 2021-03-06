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

        val testButton: Button = findViewById(R.id.button)
        testButton.setOnClickListener {
            val intent = Intent(this, RecycleInformation::class.java)
            startActivity(intent)
        }
    }


}
