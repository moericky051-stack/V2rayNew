# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

-keep class com.example.data.model.** { *; }
-keepclassmembers class com.example.data.model.** { *; }

# Keep Coroutines
-keepclassmembers class * extends kotlinx.coroutines.CoroutineScope { *; }

# Keep Room entities
-keep class androidx.room.RoomDatabase
-dontwarn androidx.room.**

# Keep Jetpack Compose
-keep class androidx.compose.** { *; }

# Keep Gson / Serialization
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
