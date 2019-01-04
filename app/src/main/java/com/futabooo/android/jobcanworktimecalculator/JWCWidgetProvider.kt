package com.futabooo.android.jobcanworktimecalculator

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.widget.RemoteViews
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.BufferedReader
import java.io.InputStreamReader

class JWCWidgetProvider : AppWidgetProvider() {

  private lateinit var document: Document
  private lateinit var html: List<String>
  private lateinit var attendance: Attendance

  companion object {

    internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {

      val client = JWCClient(context)
      GlobalScope.launch(Dispatchers.Main) {
        // Please insert client_id, email, password
        client.login("", "", "")
            .await()
        val responseBody = client.attendance()
            .await()

        val html = BufferedReader(InputStreamReader(responseBody.byteStream()))
            .readLines()
            .filter(String::isNotBlank)
            .toList()

        val document = Jsoup.parse(TextUtils.join("", html))
        val attendance = Attendance(document)

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.jwc_widget).apply {
          setTextViewText(R.id.date, attendance.date)
          setTextViewText(R.id.required_days, attendance.requiredWorkDay)
          setTextViewText(R.id.required_work_time, attendance.requiredWorkTime())
          setTextViewText(R.id.actual_work_day, attendance.actualWorkDay)
          setTextViewText(R.id.actual_work_time, attendance.actualWorkTime)
          setTextViewText(R.id.holiday, attendance.holiday)
          setTextViewText(R.id.saving_time, attendance.savingTime())
          setTextViewText(R.id.estimated_time, attendance.estimatedTime())
        }

        val intent = Intent(context, JWCWidgetProvider::class.java).apply {
          putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
      }
    }
  }

  override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
    // There may be multiple widgets active, so update all of them
    appWidgetIds.forEach {
      updateAppWidget(context, appWidgetManager, it)
    }
  }

  override fun onEnabled(context: Context) {
    // Enter relevant functionality for when the first widget is created
  }

  override fun onDisabled(context: Context) {
    // Enter relevant functionality for when the last widget is disabled
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    super.onReceive(context, intent)
  }
}