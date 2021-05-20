package com.example.linearregression;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


/**
 * @author Arnab Kundu
 */
public class LinearRegressionActivity extends AppCompatActivity {

    private static final String TAG = "LinearRegressionActivit";
    private EditText et_x_input;
    private Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_regression);
        et_x_input = findViewById(R.id.et_x_input);

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Load model file from assets folder or ml folder
     *
     * @return MappedByteBuffer
     * @throws IOException If model file does not exist
     */
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("model_linear_regression.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declareLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declareLength);
    }

    /**
     * Predict Y value for corresponding given X value
     *
     * @param inputString X value
     * @return Y value
     */
    private float doInference(String inputString) {
        float[] inputVal = new float[1];
        inputVal[0] = Float.parseFloat(inputString);
        float[][] output = new float[1][1];
        tflite.run(inputVal, output);
        return output[0][0];
    }


    /**
     * onCLick
     *
     * @param view
     */
    public void getResult(View view) {
        //Log.d(TAG, "onCreate: " + doInference(et_x_input.getText().toString()));
        Toast.makeText(this, "Y: " + doInference(et_x_input.getText().toString()), Toast.LENGTH_SHORT).show();
    }
}