package com.example.imageclassification

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.example.imageclassification.ml.ModelFlowerClassificationNew
import kotlinx.android.synthetic.main.activity_detect_flower.*
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.image.TensorImage

class DetectFlowerActivity : AppCompatActivity() {

    private val TAG = "DetectFlowerActivity"
    val CAMERA_PIC_REQUEST = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect_flower)

        val tfliteOptions = Interpreter.Options()
        tfliteOptions.addDelegate(GpuDelegate())
        tfliteOptions.setNumThreads(5)

    }

    /**
     * Open CAMERA using Intent
     */
    fun startCameraIntent(view: View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        startActivityForResult(intent, CAMERA_PIC_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            val bitmap: Bitmap = data?.extras?.get("data") as Bitmap

            val output: Bitmap
            if (bitmap.getWidth() >= bitmap.getHeight())
                output = Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight()
                )
            else
                output = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
                )
            iv_captured_photo.setImageBitmap(output)
            imageDetection(output)
        }

    }


    private fun imageDetection(bitmap: Bitmap) {

        val model = ModelFlowerClassificationNew.newInstance(this)

        // Creates inputs for reference.
        val image = TensorImage.fromBitmap(bitmap)

        // Runs model inference and gets result.
        val outputs = model.process(image)
        val probability = outputs.probabilityAsCategoryList
        Log.d(TAG, "objectDetection: " + probability.toString())
        // Releases model resources if no longer used.
        var flowerName: String = ""
        var score = 0.0F
        for (i in 0 until probability.size) {
            if (probability[i].score > score) {
                score = probability[i].score
                flowerName = probability[i].label
            }
        }
        tv_result.text = flowerName
        tv_probability.text = probability.toString()
        model.close()
    }
}