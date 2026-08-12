# Referenced by app/build.gradle.kts release buildType.

# --- Readable release stack traces ---
# CrashLog writes uncaught exceptions to disk, but under R8 those traces are
# obfuscated and effectively useless without these. SourceFile is rewritten to a
# constant so class names still shrink; the mapping file is what restores them.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- kotlinx.serialization ---
# Keep generated serializers and the @Serializable classes they back.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.peersignal.app.**$$serializer { *; }
-keepclassmembers class com.peersignal.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.peersignal.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Ktor ---
-dontwarn io.ktor.**
-dontwarn org.slf4j.**
-keepclassmembers class io.ktor.** { volatile <fields>; }

# --- Room ---
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-dontwarn androidx.room.paging.**

# --- Hilt / Dagger ---
-dontwarn dagger.hilt.**

# --- Kotlin coroutines ---
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
-dontwarn kotlinx.coroutines.**
