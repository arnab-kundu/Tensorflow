package com.example.logisticregression;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Logistic Regression
 *
 * @author Arnab Kundu
 */

public class LogisticRegressionActivity extends AppCompatActivity {

    private static final String TAG = "asd";
    private Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistic_regression);

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        getResult();
    }


    /**
     * Load model file from assets folder or ml folder
     *
     * @return MappedByteBuffer
     * @throws IOException If model file does not exist
     */
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("model_logistic_regression.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declareLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declareLength);
    }

    /**
     * Input: An one dimensional float array of length 8
     * <p>
     * That contains some parameter of a person to determine his/her diabetic level.
     * </p>
     * <p>
     * Output: An one dimensional float array of length 1
     * <p>
     * That contains the confidence in between 0.1 to 0.9
     * If value is greater than 0.5 then person is diabetic patient
     * else person is not a diabetic patient
     * </p>
     * Data: Data available in this path sampledata/pima-diabetes.csv
     *
     * @return confidence
     */
    private float doInference() {
        float[] inputVal = new float[8];
        /*inputVal[0] = 6.0F;
        inputVal[1] = 148.0F;
        inputVal[2] = 72.0F;
        inputVal[3] = 35.0F;
        inputVal[4] = 0.0F;
        inputVal[5] = 33.6F;
        inputVal[6] = 0.6F;
        inputVal[7] = 50.0F;*/

        /*inputVal[0] = 1.0F;
        inputVal[1] = 85.0F;
        inputVal[2] = 66.0F;
        inputVal[3] = 29.0F;
        inputVal[4] = 0.0F;
        inputVal[5] = 26.6F;
        inputVal[6] = 0.351F;
        inputVal[7] = 31.0F;*/

        inputVal[0] = 8.0F;
        inputVal[1] = 183.0F;
        inputVal[2] = 64.0F;
        inputVal[3] = 0.0F;
        inputVal[4] = 0.0F;
        inputVal[5] = 23.3F;
        inputVal[6] = 0.672F;
        inputVal[7] = 32.0F;

        float[][] output = new float[1][1];
        tflite.run(inputVal, output);
        return output[0][0];
    }

    public void getResult() {
        Log.d(TAG, "getResult: ");
        Toast.makeText(this, "Confidence: " + roundOfTwoDecimal(doInference()) + "%", Toast.LENGTH_SHORT).show();
    }

    private double roundOfTwoDecimal(double decimalNumber) {
        return Math.round(decimalNumber * 100);
    }
}