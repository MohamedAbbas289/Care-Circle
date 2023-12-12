package com.example.carecircle.ui.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.carecircle.R
import com.example.carecircle.ui.authentication.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.security.SecureRandom

private const val TAG = "FirebaseMessage"

class FirebaseMessage : FirebaseMessagingService() {

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "com.example.carecircle"
    private val description = "Test notification"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        noitfy()
        val user = message.data["user"]
        val body = message.data["body"]
        val title = message.data["title"]
        val type = message.data["type"]

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, SignUpActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, flags())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)

            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setContentText(body)
                .setSubText(title + ": " + user)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
        } else {
            builder = Notification.Builder(this)
                .setContentText(body)
                .setSubText(title + ": " + user)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(SecureRandom().nextInt(), builder.build())
        Log.e(TAG, "onMessageReceived: ${message.data.toString()}")
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val map: HashMap<String, Any> = HashMap()
        map["token"] = token
        FirebaseDatabase.getInstance().getReference().child("user")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .updateChildren(map)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun noitfy() {
        val im = NotificationManager.IMPORTANCE_DEFAULT
        val channal = NotificationChannel("com.example.carecircle", "com.example.carecircle", im)
        val notification = getSystemService(NotificationManager::class.java)
        notification.createNotificationChannel(channal)
    }

    private fun flags(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            0
        }
    }

}