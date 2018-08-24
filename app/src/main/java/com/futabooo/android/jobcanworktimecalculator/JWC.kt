package com.futabooo.android.jobcanworktimecalculator

import android.app.Application
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class JWC : Application() {

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.USE_CRASHLYTICS) {
      Fabric.with(this, Crashlytics())
    }

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    } else {
      Timber.plant(CrashReportingTree())
    }
  }
}