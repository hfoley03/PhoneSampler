package com.example.harryerayaudiorecorder.data

import android.os.Handler
import android.os.Looper

class Timer(listener: OnTimerTickListener) {

    interface  OnTimerTickListener{
        fun onTimerTick(duration: String)
    }

    private var handler = Handler(Looper.getMainLooper())
    private  lateinit var runnable: Runnable

    private var duration = 0L
    private var delay = 100L

    init {
        runnable = Runnable{
            duration += delay
            handler.postDelayed(runnable, delay)
            listener.onTimerTick(formattTime())
        }
    }

    fun start(){
        handler.postDelayed(runnable, delay)
    }

    fun pause(){
        handler.removeCallbacks(runnable)
    }

    fun stop(){
        handler.removeCallbacks(runnable)
        duration = 0L
    }

    fun formattTime(): String{
        val milli = duration % 1000
        val secs = (duration/1000) % 60
        val mins = (duration / (1000 * 60)) % 60
        val hrs =  (duration / (1000 * 60* 60))

        var formatted = if(hrs>0)
            "%02d:%02d:%02d.%02d".format(hrs, mins, secs, milli)
        else
            "%02d:%02d.%02d".format(mins, secs, milli)

        return formatted

    }


}