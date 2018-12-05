package edu.uw.oazeemi.trashcache

import android.content.Context
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
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

    private val mPicture = Camera.PictureCallback { data, _ ->
        try {
            val imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            val image: FirebaseVisionImage =  FirebaseVisionImage.fromBitmap(imageBitmap)
            labelImage(image)
            labelImagesCloud(image)
//            fos.write(data)
//            fos.close()
        } catch (e: FileNotFoundException) {
            Log.d(TAG, "File not found: ${e.message}")
        } catch (e: IOException) {
            Log.d(TAG, "Error accessing file: ${e.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        camera = getCameraInstance()
        camera!!.parameters!!.setRotation(90)
        preview = CameraPreview(this, camera!!)
        val previewLayout = findViewById<FrameLayout>(R.id.camera_preview)
        previewLayout.addView(preview)
        val captureButton: Button = findViewById(R.id.button_capture)
        captureButton.setOnClickListener {
            // get an image from the camera
            camera?.takePicture(null, null, mPicture)
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

    fun labelImage(image: FirebaseVisionImage) {
        val options = FirebaseVisionLabelDetectorOptions.Builder()
                .setConfidenceThreshold(0.8f)
                .build()

        val detector = FirebaseVision.getInstance()
                .getVisionLabelDetector(options)

        val result = detector.detectInImage(image)
                .addOnSuccessListener { labels ->
                    for (label in labels) {
                        val text = label.label
                        val entityId = label.entityId
                        val confidence = label.confidence
                        Log.i("imageLabel", text.toString())
                        Log.i("imageLabel", entityId.toString())
                        Log.i("imageLabel", confidence.toString())
                    }

                }
                .addOnFailureListener(
                        object : OnFailureListener {
                            override fun onFailure(e: Exception) {
                                // Task failed with an exception
                                // ...
                            }
                        })
    }

    private fun labelImagesCloud(image: FirebaseVisionImage) {
        // [START set_detector_options_cloud]
        val options2 = FirebaseVisionCloudDetectorOptions.Builder()
                .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                .setMaxResults(30)
                .build()

        val detector: FirebaseVisionCloudLabelDetector = FirebaseVision.getInstance().getVisionCloudLabelDetector(options2)

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
                            }

                        })
                .addOnFailureListener(
                        object : OnFailureListener {
                            override fun onFailure(e: Exception) {
                                // Task failed with an exception
                                // ...
                            }
                        })
    }

    private fun releaseCamera() {
        camera?.release() // release the camera for other applications
        camera = null
    }
}
