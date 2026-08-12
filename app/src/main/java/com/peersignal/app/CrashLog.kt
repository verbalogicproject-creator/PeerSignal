package com.peersignal.app

import android.content.Context
import android.os.Build
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Writes uncaught exceptions to the app's external files directory so a crash
 * can be diagnosed without adb.
 *
 * This exists because logcat is unreadable from the development environment on
 * this device: the toolchain runs inside Termux/PRoot, which cannot read another
 * app's log buffer without READ_LOGS. Without this file, a launch crash gives no
 * evidence at all and the next fix is guesswork.
 *
 * The trace lands at
 *   /storage/emulated/0/Android/data/com.peersignal.app/files/crash.txt
 * which is reachable by direct path even though Android/data is not listable.
 */
object CrashLog {

    fun install(context: Context) {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            try {
                write(context, thread, error)
            } catch (_: Throwable) {
                // Never let the reporter mask the original crash.
            }
            // Chain, so the system still shows its dialog and the process dies.
            previous?.uncaughtException(thread, error)
        }
    }

    private fun write(context: Context, thread: Thread, error: Throwable) {
        val dir = context.getExternalFilesDir(null) ?: return
        if (!dir.exists()) dir.mkdirs()

        val stack = StringWriter().also { sw ->
            PrintWriter(sw).use { error.printStackTrace(it) }
        }.toString()

        val report = buildString {
            appendLine("thread : ${thread.name}")
            appendLine("type   : ${error.javaClass.name}")
            appendLine("message: ${error.message}")
            appendLine("device : ${Build.MANUFACTURER} ${Build.MODEL}, API ${Build.VERSION.SDK_INT}")
            appendLine()
            append(stack)
        }

        File(dir, "crash.txt").writeText(report)
    }
}
