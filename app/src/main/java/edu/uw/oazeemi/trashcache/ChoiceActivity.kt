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
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions
import android.widget.Toast
import android.R.attr.data
import android.support.v4.app.NotificationCompat.getExtras
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector
import kotlinx.android.synthetic.main.activity_choice.*


class ChoiceActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 111

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

//            mImageLabe
///
            val image: FirebaseVisionImage =  FirebaseVisionImage.fromBitmap(imageBitmap)
            labelImage(image)
            labelImagesCloud(image)
        }
    }

    fun labelImage(image: FirebaseVisionImage) {
        val options = FirebaseVisionLabelDetectorOptions.Builder()
                .setConfidenceThreshold(0.8f)
                .build()

        val detector = FirebaseVision.getInstance()
                .getVisionLabelDetector(options)

            val result = detector.detectInImage(image)
                    .addOnSuccessListener { labels ->
                        // Task completed successfully
                        // [START_EXCLUDE]
                        // [START get_labels]
                        for (label in labels) {
                            val text = label.label
                            val entityId = label.entityId
                            val confidence = label.confidence
                            Log.i("imageLabel", text.toString())
                            Log.i("imageLabel", entityId.toString())
                            Log.i("imageLabel", confidence.toString())
                        }




                        // [END get_labels]
                        // [END_EXCLUDE]
                    }
                    .addOnFailureListener(
                            object : OnFailureListener {
                                override fun onFailure(e: Exception) {
                                    // Task failed with an exception
                                    // ...
                                }
                            })         // [END run_detector]
//        val result2 = result.result
//        Log.d("test1234", result2)
        }

    private fun labelImagesCloud(image: FirebaseVisionImage) {
        // [START set_detector_options_cloud]
        val options2 = FirebaseVisionCloudDetectorOptions.Builder()
                .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                .setMaxResults(30)
                .build()
        // [END set_detector_options_cloud]

        // [START get_detector_cloud]
        val detector: FirebaseVisionCloudLabelDetector = FirebaseVision.getInstance().getVisionCloudLabelDetector(options2)
        //         .getVisionCloudLabelDetector(options);
        // Or, to change the default settings:
        //
        // [END get_detector_cloud]

        // [START run_detector_cloud]
        val result = detector.detectInImage(image)
                .addOnSuccessListener(
                        object : OnSuccessListener<List<FirebaseVisionCloudLabel>> {
                            override fun onSuccess(labels: List<FirebaseVisionCloudLabel>) {
                                // Task completed successfully
                                // [START_EXCLUDE]
                                // [START get_labels_cloud]
                                for (label in labels) {
                                    val text = label.label
                                    val entityId = label.entityId
                                    val confidence = label.confidence
                                    Log.i("imageLabel", text.toString())
                                    Log.i("imageLabel", entityId.toString())
                                    Log.i("imageLabel", confidence.toString())
                                }
                                // [END get_labels_cloud]
                                // [END_EXCLUDE]
                            }

                        })
                .addOnFailureListener(
                        object : OnFailureListener {
                            override fun onFailure(e: Exception) {
                                // Task failed with an exception
                                // ...
                            }
                        })
        val result2 = result.toString()
        Log.d("test1234", result2)
        // [END run_detector_cloud]
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
