package com.peersignal.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// @HiltAndroidApp is what builds the SingletonComponent. Without it, every
// @AndroidEntryPoint activity throws at onCreate:
//   "Hilt Activity must be attached to an @HiltAndroidApp Application"
// This compiles cleanly either way -- the check is purely at runtime.
@HiltAndroidApp
class PeerSignalApp : Application()
