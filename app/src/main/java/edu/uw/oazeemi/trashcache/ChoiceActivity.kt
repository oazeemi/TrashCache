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
        photoSearchBtn.setOnClickListener { dispatchTakePictureIntent() }

        val locationSearch: Button = findViewById(R.id.btn_location)
        locationSearch.setOnClickListener {
            val intent =  Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }
    }
    private fun dispatchTakePictureIntent() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent: Intent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            val image: FirebaseVisionImage =  FirebaseVisionImage.fromBitmap(imageBitmap)

            labelImagesCloud(image)
        }
    }


    private fun labelImagesCloud(image: FirebaseVisionImage) {
        val options = FirebaseVisionCloudDetectorOptions.Builder()
                .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                .setMaxResults(2)
                .build()
        val detector: FirebaseVisionCloudLabelDetector = FirebaseVision.getInstance().getVisionCloudLabelDetector(options)

        val result = detector.detectInImage(image)
                .addOnSuccessListener(
                        object : OnSuccessListener<List<FirebaseVisionCloudLabel>> {
                            override fun onSuccess(labels: List<FirebaseVisionCloudLabel>) {

                                var item = labels[0]
                                var itemName = item.label
                                var itemConfidence = item.confidence
                                if (currentUser != null) {
                                    itemDetected = ItemDetected(itemName, itemConfidence, currentUser.uid)
                                }

                                Toast.makeText(this@ChoiceActivity, "${itemDetected.toString()}", Toast.LENGTH_LONG).show()

                                mDatabase.child("itemsDetected").push().setValue(itemDetected) //add to the list
                            }
                        })
                .addOnFailureListener(
                        object : OnFailureListener {
                            override fun onFailure(e: Exception) {
                                Log.v("ChoiceActivity", "${e.message}")
                            }
                        })

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
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
