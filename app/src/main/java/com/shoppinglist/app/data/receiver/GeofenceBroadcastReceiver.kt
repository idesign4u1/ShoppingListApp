package com.shoppinglist.app.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.shoppinglist.app.data.notification.NotificationHelper

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent != null && geofencingEvent.hasError()) {
            return
        }

        if (geofencingEvent != null) {
            val geofenceTransition = geofencingEvent.geofenceTransition

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                // Trigger Notification
                NotificationHelper.sendNotification(
                    context, 
                    "אתה ליד הסופר!", 
                    "זה הזמן לפתוח את רשימת הקניות שלך."
                )
            }
        }
    }
}
