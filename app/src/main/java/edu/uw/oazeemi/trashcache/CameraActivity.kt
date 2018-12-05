package edu.uw.oazeemi.trashcache

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files.createDirectory
import java.nio.file.Files.exists
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {
    private var camera: Camera? = null
    private var preview: CameraPreview? = null
    private val TAG = "CameraActivity"
    private val mDatabase = FirebaseDatabase.getInstance().reference;
    private var itemDetected:ItemDetected? = null;
    private val REQUEST_CODE = 1

    private  val currentUser = FirebaseAuth.getInstance().getCurrentUser()

    private val mPicture = Camera.PictureCallback { data, _ ->
        try {
            val imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            val image: FirebaseVisionImage =  FirebaseVisionImage.fromBitmap(imageBitmap)
            labelImagesCloud(image)
        } catch (e: FileNotFoundException) {
            Log.d(TAG, "File not found: ${e.message}")
        } catch (e: IOException) {
            Log.d(TAG, "Error accessing file: ${e.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        setupPermissions()
        camera = getCameraInstance()
        //camera!!.parameters!!.setRotation(90)
        preview = CameraPreview(this, camera!!)
        val previewLayout = findViewById<FrameLayout>(R.id.camera_preview)
        previewLayout.addView(preview)
        val captureButton: ImageButton = findViewById(R.id.button_capture)
        val params = camera?.parameters
        params?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        camera?.parameters = params
        captureButton.setOnClickListener {
            // get an image from the camera
            camera?.takePicture(null, null, mPicture)
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission denied")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
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

    override fun onPause() {
        super.onPause()
        releaseCamera()
    }

    private fun getCameraInstance(): Camera? {
        return try {
            Camera.open() // attempt to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, e.localizedMessage)
            null // returns null if camera is unavailable
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

                                val itemTextView = findViewById<TextView>(R.id.item_detected)
                                itemTextView.text = itemDetected?.itemName.toString()

                                mDatabase.child("itemsDetected").push().setValue(itemDetected) //add to the list
                                val intent = Intent(this@CameraActivity, RecycleInformation::class.java)
                                intent.putExtra("item", "$itemName")
                                startActivity(intent)
                            }
                        })
                .addOnFailureListener(
                        object : OnFailureListener {
                            override fun onFailure(e: Exception) {
                                Log.v("ChoiceActivity", "${e.message}")
                            }
                        })

    }
    private fun releaseCamera() {
        camera?.release() // release the camera for other applications
        camera = null
    }
}
