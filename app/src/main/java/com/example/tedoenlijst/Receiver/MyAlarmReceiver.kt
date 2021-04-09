package com.example.tedoenlijst.Receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tedoenlijst.MainActivity
import com.example.tedoenlijst.R
import io.karn.notify.Notify
import java.util.*

class MyAlarmReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle: Bundle? = intent!!.extras
        var message = bundle!!.getString("message")

        buildNotification(context!!, "Reminder Alert!", message.toString())
    }

    private fun buildNotification(context: Context, titlee: String, message: String){
        Notify
            .with(context)
            .meta {
                clickIntent = PendingIntent.getActivity(context,
                    0,
                    Intent(context, MainActivity::class.java),
                    0)
            }
            .content { // this: Payload.Content.Default
                title = titlee
                text = message
            }
            .show()
    }

}