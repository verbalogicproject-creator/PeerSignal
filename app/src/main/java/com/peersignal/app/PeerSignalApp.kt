package com.peersignal.app

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

// @HiltAndroidApp is what builds the SingletonComponent. Without it, every
// @AndroidEntryPoint activity throws at onCreate:
//   "Hilt Activity must be attached to an @HiltAndroidApp Application"
// This compiles cleanly either way -- the check is purely at runtime.
@HiltAndroidApp
class PeerSignalApp : Application() {

    // Installed in attachBaseContext, not onCreate: Hilt's generated component
    // setup runs inside super.onCreate(), so a handler installed there would
    // miss a crash originating in dependency-graph construction.
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        CrashLog.install(this)
    }
}
