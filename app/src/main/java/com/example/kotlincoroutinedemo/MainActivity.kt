package com.example.kotlincoroutinedemo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private val RESULT_1: String = "Result #1"
    private val RESULT_2: String = "Result #2"
    private val RESULT_3: String = "Result #3"

    private lateinit var textHelloWorld: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnClickMe: Button = findViewById(R.id.btnClickMe)
        textHelloWorld = findViewById(R.id.textHelloWorld)

        btnClickMe.setOnClickListener {
            fakeAPIRequest()
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

    private fun fakeAPIRequest() {
        CoroutineScope(IO).launch {

            val result1 =
                withContext(Dispatchers.Default) {
                    getResultFromAPI1()
                }

            val result2 =
                withContext(Dispatchers.Default) {
                    getResultFromAPI2(result1)
                }

            val result3 =
                withContext(Dispatchers.Default) {
                    getResultFromAPI3(result2 = result2)
                }

            setTextOnMainThread(result3)

        }
    }

    private suspend fun getResultFromAPI3(result2: String): String {
        logThread("getResultFromAPI3")
        delay(1000)
        if (result2 == RESULT_2)
            return RESULT_3
        return "Error result3"
    }

    private suspend fun getResultFromAPI2(result1: String): String {
        logThread("getResultFromAPI2")
        delay(1000)
        if (result1 == RESULT_1)
            return RESULT_2
        return "Error result2"
    }

    private suspend fun getResultFromAPI1(): String {
        logThread("getResultFromAPI1")
        delay(1000)
        return RESULT_1
    }

    private fun logThread(methodName: String) {
        println("debug: current thread: $methodName: ${Thread.currentThread().name}")
    }

}