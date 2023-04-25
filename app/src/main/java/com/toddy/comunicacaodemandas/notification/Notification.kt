package com.toddy.comunicacaodemandas.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.toddy.comunicacaodemandas.R
import com.toddy.comunicacaodemandas.ui.activity.MainActivity

const val notificationID = 1
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class Notification : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val resultIntent = Intent(context, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? =
            TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(resultIntent)
                getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .setContentIntent(resultPendingIntent)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)

    }


}