package com.example.logisticregression;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.logisticregression.databinding.ActivityLogisticRegressionBinding;

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
    private ActivityLogisticRegressionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_logistic_regression);
        binding = ActivityLogisticRegressionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
    private float doInference(
            float pregnant,
            float glucose,
            float blood_pressure,
            float skin_thickness,
            float insulin,
            float bmi,
            float diabetes_pedigree_function,
            float age) {

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

        /*inputVal[0] = 8.0F;
        inputVal[1] = 183.0F;
        inputVal[2] = 64.0F;
        inputVal[3] = 0.0F;
        inputVal[4] = 0.0F;
        inputVal[5] = 23.3F;
        inputVal[6] = 0.672F;
        inputVal[7] = 32.0F;*/


        inputVal[0] = pregnant;
        inputVal[1] = glucose;
        inputVal[2] = blood_pressure;
        inputVal[3] = skin_thickness;
        inputVal[4] = insulin;
        inputVal[5] = bmi;
        inputVal[6] = diabetes_pedigree_function;
        inputVal[7] = age;

        float[][] output = new float[1][1];
        tflite.run(inputVal, output);
        return output[0][0];
    }

    public void getResult() {
        Log.d(TAG, "getResult: ");
        //Toast.makeText(this, "Confidence: " + roundOfTwoDecimal(doInference()) + "%", Toast.LENGTH_SHORT).show();
    }

    private double roundOfTwoDecimal(double decimalNumber) {
        return Math.round(decimalNumber * 100);
    }

    public void predict(View view) {
        float isDiabetic = doInference(
                Float.parseFloat(binding.pregnant.getText().toString()),
                Float.parseFloat(binding.glucose.getText().toString()),
                Float.parseFloat(binding.bloodPressure.getText().toString()),
                Float.parseFloat(binding.skinThickness.getText().toString()),
                Float.parseFloat(binding.insulin.getText().toString()),
                Float.parseFloat(binding.bmi.getText().toString()),
                Float.parseFloat(binding.diabetesPedigreeFunction.getText().toString()),
                Float.parseFloat(binding.age.getText().toString())
        );

        Toast.makeText(this, "Confidence: " + roundOfTwoDecimal(isDiabetic) + "%", Toast.LENGTH_SHORT).show();
    }

    public void bmiInfo(View view) {
        Toast.makeText(this, "What is normal BMI?\n" +
                "If your BMI is 18.5 to 24.9, it falls within the normal or Healthy Weight range. If your BMI is 25.0 to 29.9, it falls within the overweight range. If your BMI is 30.0 or higher, it falls within the obese range.", Toast.LENGTH_SHORT).show();
    }
}