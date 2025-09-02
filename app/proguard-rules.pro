-keep class org.apache.cordova.** { *; }
-dontwarn org.apache.cordova.**

-keep class android.webkit.** { *; }
-dontwarn android.webkit.**

-keep class com.proptr.proptrader.android.** { *; }
-dontwarn com.proptr.proptrader.android.**

-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keep class coil.** { *; }
-dontwarn coil.**

-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

-keep class org.apache.cordova.CordovaPlugin { *; }
-keep class org.apache.cordova.CordovaWebView { *; }

-keep class com.proptr.proptrader.android.**Plugin { *; }

-keep class org.apache.cordova.** { *; }
-dontwarn org.apache.cordova.**

-keep class com.appsflyer.** { *; }
-keep class kotlin.jvm.internal.** { *; }

-keep class com.appsflyer.** { *; }
-keep class kotlin.jvm.internal.** { *; }

-dontwarn com.appsflyer.**
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$Companion { *; }
-keepnames class **$Companion

# OneSignal SDK keep rules
-keep class com.onesignal.** { *; }
-dontwarn com.onesignal.**

# Firebase Messaging keep rules
-keep class com.google.firebase.messaging.** { *; }
-dontwarn com.google.firebase.messaging.**
-keep class com.google.firebase.iid.** { *; }
-dontwarn com.google.firebase.iid.**