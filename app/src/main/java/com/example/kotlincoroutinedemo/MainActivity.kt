package com.example.kotlincoroutinedemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 400 // ms

    private lateinit var progressBar: ProgressBar
    private lateinit var btnStartJob: Button
    private lateinit var textHelloWorld: TextView

    private lateinit var job: CompletableJob

    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        btnStartJob = findViewById(R.id.btnStartJob)
        textHelloWorld = findViewById(R.id.textHelloWorld)

        btnStartJob.setOnClickListener {
            if (!::job.isInitialized) initJob()
            progressBar.startOrCancelJob(job)
        }
    }

    @DelicateCoroutinesApi
    private fun initJob() {
        updateJobCompleteTextView("")
        job = Job()
        job.invokeOnCompletion {
            it?.message.let {
                var msg = it
                if (msg.isNullOrEmpty()) {
                    msg = "Unknown Cancellation error."
                }
                println("$job was cancelled. reason $msg")
                showToast(this, msg)
            }
        }
        progressBar.max = PROGRESS_MAX
        progressBar.progress = PROGRESS_START
    }

    private fun ProgressBar.startOrCancelJob(job: Job) {
        if (this.progress > 0) {
            println("$job is already active. cancelling.....")
            resetJob()
        } else {
            btnStartJob.text = "Cancel Job"
            CoroutineScope(IO + job).launch {
                println("Coroutine $this is activated with $job")
                for (i in PROGRESS_START..PROGRESS_MAX) {
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startOrCancelJob.progress = i
                }
                updateJobCompleteTextView("Job Completed.")
            }
        }
    }

    private fun resetJob() {
        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Resetting Job"))
        }
        initJob()
    }

    private fun updateJobCompleteTextView(input: String) {
        GlobalScope.launch(Main) {
            textHelloWorld.setText(input)
        }
    }

}

@DelicateCoroutinesApi
fun showToast(context: Context, msg: String) {
    GlobalScope.launch(Main) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}