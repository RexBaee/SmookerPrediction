package com.example.smookerprediction

import android.annotation.SuppressLint
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiModelActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "smookerstatus.tflite"

    private lateinit var resultText: TextView
    private lateinit var age: EditText
    private lateinit var systolic: EditText
    private lateinit var relaxation: EditText
    private lateinit var fastingbloodsugar: EditText
    private lateinit var cholesterol: EditText
    private lateinit var triglyceride: EditText
    private lateinit var hemoglobin: EditText
    private lateinit var urineprotein: EditText
    private lateinit var serumcreatinine: EditText
    private lateinit var checkButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi_model)

        resultText = findViewById(R.id.txtResult)
        age = findViewById(R.id.age)
        systolic = findViewById(R.id.systolic)
        relaxation = findViewById(R.id.relaxation)
        fastingbloodsugar = findViewById(R.id.fastingbloodsugar)
        cholesterol = findViewById(R.id.cholesterol)
        triglyceride = findViewById(R.id.triglyceride)
        hemoglobin = findViewById(R.id.hemoglobin)
        urineprotein = findViewById(R.id.urineprotein)
        serumcreatinine = findViewById(R.id.serumcreatinine)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                age.text.toString(),
                systolic.text.toString(),
                relaxation.text.toString(),
                fastingbloodsugar.text.toString(),
                cholesterol.text.toString(),
                triglyceride.text.toString(),
                hemoglobin.text.toString(),
                urineprotein.text.toString(),
                serumcreatinine.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Perokok"
                }else if (result == 1){
                    resultText.text = "Bukan Perokok"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(10)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String, input8: String, input9: String): Int{
        val inputVal = FloatArray(9)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        inputVal[7] = input8.toFloat()
        inputVal[8] = input9.toFloat()
        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}