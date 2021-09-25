package com.example.kotlincoroutinedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private val RESULT_1: String = "Result #1"
    private val RESULT_2: String = "Result #2"
    private val TIMEOUT: Long = 1900L

    private lateinit var textHelloWorld: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnClickMe: Button = findViewById(R.id.btnClickMe)
        textHelloWorld = findViewById(R.id.textHelloWorld)

        btnClickMe.setOnClickListener {
            CoroutineScope(IO).launch {
                fakeAPIRequest()
            }
        }
    }

    private fun setHelloWorldText(input: String) {
        val currentText = textHelloWorld.text.toString()
        textHelloWorld.text = currentText + "\n$input"
    }

    private suspend fun setTextOnMainThread(text: String) {
        withContext(Main) {
            setHelloWorldText(text)
        }
    }

    private suspend fun fakeAPIRequest() {
        val job = withTimeoutOrNull(TIMEOUT){
            val result1 = getResultFromAPI1()
            println("debug: $result1")
            setTextOnMainThread(result1)

            val result2 = getResultFromAPI2()
            println("debug: $result2")
            setTextOnMainThread(result2)
        }

        if (job == null) {
            val cancelMessage = "Cancelling job.... job tooks longer than $TIMEOUT"
            println("debug: $cancelMessage")
            setTextOnMainThread(cancelMessage)
        }

    }

    private suspend fun getResultFromAPI2(): String {
        logThread("getResultFromAPI2")
        delay(1000)
        return RESULT_2
    }

    private suspend fun getResultFromAPI1(): String {
        logThread("getResultFromAPI1")
        delay(1000)
        return RESULT_1
    }

    private fun logThread(methodName: String) {
        println("debug: $methodName: ${Thread.currentThread().name}")
    }

}