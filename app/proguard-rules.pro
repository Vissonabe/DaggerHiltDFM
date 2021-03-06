# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Keep Activity and other class names
-keepnames public class * extends android.app.Application
-keepnames public class * extends android.app.Activity
-keepnames public class * extends android.app.Fragment
-keepnames public class * extends android.app.Service
-keepnames public class * extends android.content.BroadcastReceiver

-keepnames class androidx.fragment.app.FragmentContainerView
-keepnames class androidx.navigation.fragment.NavHostFragment
-keepnames class androidx.navigation.dynamicfeatures.fragment.DynamicNavHostFragment
-keep class * extends androidx.fragment.app.Fragment{}
-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable