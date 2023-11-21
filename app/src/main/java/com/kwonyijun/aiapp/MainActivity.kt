package com.kwonyijun.aiapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val questionText = findViewById<EditText>(R.id.question_EditText)
        val submitButton = findViewById<Button>(R.id.submit_Button)
        val responseText = findViewById<TextView>(R.id.response_TextView)


        submitButton.setOnClickListener {
            val question = questionText.text.toString()
            Toast.makeText(this, question, Toast.LENGTH_LONG).show()
            getResponse(question){response ->
                runOnUiThread{
                    responseText.text = response
                }
            }
        }
    }

    // using OkHttp (api call)
    // fun getResponse(question: String, param: (Any) -> Unit) { // param is a fun of any type, Unit is similar to void
    fun getResponse(question: String, callback: (String) -> Unit) {
        // using OPENAI
        val openAIUrl = "https://api.openai.com/v1/completions"
        val apiKey = "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"

        val requestBody = """
            {
            "model" : "text-davinci-003",
            "prompt" : "$question",
            "max_tokens" : 7,
            "temperature" : 0
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(openAIUrl)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", "API failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()

                // pass the response of the openAI
                val jsonObject = JSONObject(body)
                val jsonArray:JSONArray = jsonObject.getJSONArray("choices")
                val textResult = jsonArray.getJSONObject(0).getString("text")
                callback(textResult)
            }
        })
    }
}