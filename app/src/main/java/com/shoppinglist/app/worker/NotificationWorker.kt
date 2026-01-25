package com.shoppinglist.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.shoppinglist.app.MainActivity
import com.shoppinglist.app.R
import com.shoppinglist.app.data.model.InvitationStatus
import com.shoppinglist.app.data.repository.ChatRepository
import com.shoppinglist.app.data.repository.InvitationRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import androidx.hilt.work.HiltWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface NotificationWorkerEntryPoint {
        fun invitationRepository(): InvitationRepository
        fun chatRepository(): ChatRepository
        // fun productRepository(): ProductRepository // Complex to query all products assigned to me without compound index
    }

    override suspend fun doWork(): Result {
        val appContext = applicationContext
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            NotificationWorkerEntryPoint::class.java
        )
        val invitationRepository = entryPoint.invitationRepository()
        // val chatRepository = entryPoint.chatRepository()

        try {
            // 1. Check Invitations
            val invitations = invitationRepository.getMyInvitations().firstOrNull()
            if (!invitations.isNullOrEmpty()) {
                sendNotification("הזמנה חדשה!", "יש לך ${invitations.size} הזמנות להצטרף לרשימות.")
            }

            // 2. Check Assignments (simplified: implies we need a query for "assignedTo == me" & "isCompleted == false")
            // Skipping complex queries to avoid crashes.
            
            // 3. New Messages (simplified)
            // Ideally we'd store "lastCheckTime" in DataStore/SharedPrefs and query > lastCheckTime
            
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "shopping_list_updates"

        val channel = NotificationChannel(
            channelId,
            "עדכונים והזמנות",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // using system icon as fallback
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}
