# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
# Add any project specific keep options here:
-keep class org.spongycastle.** { *; }
-keep class com.aditya.filebrowser.** { *; }
-keep class com.roughike.bottombar.** { *; }

-keep class com.beardedhen.androidbootstrap.** { *; }
#-keep class com.simplecityapps.recycleview_fastscroll.** { *; }
-dontwarn org.spongycastle.**
-dontwarn com.roughike.bottombar.**
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
