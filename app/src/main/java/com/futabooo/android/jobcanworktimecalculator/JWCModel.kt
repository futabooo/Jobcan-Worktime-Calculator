package com.futabooo.android.jobcanworktimecalculator

import org.jsoup.nodes.Document

class Attendance constructor(private val document: Document) {

  val date: String = document.select("table.left:contains(ユーザー情報) tbody tr:eq(0) td").text()
  val requiredWorkDay: String = document.select("table.left:contains(ユーザー情報) tbody tr:eq(3) td").text().removeSuffix("日").trim()
  val requiredWorkTime: String = document.select("table.left:contains(労働時間) tbody tr:eq(1) td").text().replace(":", ".")
  val actualWorkDay: String = document.select("table.right:contains(基本項目) tbody tr:eq(0) td").text()
  val actualWorkTime: String = document.select("table.left:contains(労働時間) tbody tr:eq(0) td").text().replace(":", ".")
  val holiday: String = document.select("table.right:contains(消化した休暇) tbody:eq(3) td")
      .map { it.text().toFloat() }
      .sum()
      .toString()

  fun savingTime(): String {
    val daysLeft = requiredWorkDay.toInt() - actualWorkDay.toInt()
    val actual = actualWorkTime.replace(":", ".").toFloat()
    val savingTime = actual - (requiredWorkTime.toFloat() - (daysLeft * 8))
    return String.format("%1.2f", savingTime)
  }

  fun estimatedTime(): String {
    val daysLeft = requiredWorkDay.toInt() - actualWorkDay.toInt()
    val actual = actualWorkTime.toFloat()
    val shortTime = requiredWorkTime.toFloat() - actual
    return String.format("%1.2f", shortTime / daysLeft)
  }
}