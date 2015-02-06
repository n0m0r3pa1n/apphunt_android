# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/vini/Dev/lib/android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}



-optimizationpasses 5

#When not preverifing in a case-insensitive filing system, such as Windows. Because this tool unpacks your processed jars, you should then use:
#-dontusemixedcaseclassnames

#Specifies not to ignore non-public library classes. As of version 4.5, this is the default setting
-dontskipnonpubliclibraryclasses

#Preverification is irrelevant for the dex compiler and the Dalvik VM, so we can switch it off with the -dontpreverify option.
-dontpreverify

#Specifies to write out some more information during processing. If the program terminates with an exception, this option will print out the entire stack trace, instead of just the exception message.
#-verbose

#The -optimizations option disables some arithmetic simplifications that Dalvik 1.0 and 1.5 can't handle. Note that the Dalvik VM also can't handle aggressive overloading (of static fields).
#To understand or change this check http://proguard.sourceforge.net/index.html#/manual/optimizations.html
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#To repackage classes on a single package
-repackageclasses ''

#Uncomment if using annotations to keep them.
-keepattributes *Annotation*

# --------------------------------------------------------------------------------------------------
# basic settings
# --------------------------------------------------------------------------------------------------
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}


-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @ccom.google.android.gms.common.annotation.KeepName *;
}

#To keep parcelable classes (to serialize - deserialize objects to sent through Intents)
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepnames class * implements java.io.Serializable {
    public static final ** CREATOR;
}

-keep class com.facebook.** { *; }
-keepattributes Signature

# --------------------------------------------------------------------------------------------------
# basic settings END
# --------------------------------------------------------------------------------------------------


-dontwarn **CompatHoneycomb
-dontwarn android.support.v4.**
-dontwarn com.squareup.okhttp.*
-dontnote libcore.icu.ICU
-dontnote sun.misc.Unsafe

-keep class android.support.v4.** { *; }

# --------------------------------------------------------------------------------------------------
# More additional options from stackoverflow
# --------------------------------------------------------------------------------------------------
-keep class com.apphunt.app.**{ *; }
-keep class com.squareup.okhttp.**{ *; }
-keep class java.nio.**{ *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-dontwarn rx.*
-dontwarn rx.**
-keep class sun.misc.Unsafe { *; }
-dontwarn com.facebook.android.BuildConfig
-dontwarn okio.**
-dontwarn org.xmlpull.v1.**
-dontwarn com.squareup.**
-keep class com.squareup.** { *; }
-dontwarn com.squareup.okhttp.*
-dontwarn retrofit.appengine.UrlFetchClient
-keepattributes Annotation
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keepattributes Signature
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class pl.droidsonroids.gif.GifInfoHandle{<init>(long,int,int,int); }



# for gson (GSON @Expose annotation)
-keepattributes *Annotation*,Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep public class com.google.gson.Gson
-keep class com.google.gson.** { *; }
-keep class retrofit.http.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

# for google analytics & campaign tool
-keep class com.google.android.gms.analytics.**
-keep class com.google.analytics.tracking.**
-dontwarn com.google.android.gms.analytics.**
-dontwarn com.google.analytics.tracking.**


#Keep classes that are referenced on the AndroidManifest
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService


#To remove debug logs:
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

#To avoid changing names of methods invoked on layout's onClick.
# Uncomment and add specific method names if using onClick on layouts
#-keepclassmembers class * {
# public void onClickButton(android.view.View);
#}

#Maintain java native methods
#-keepclasseswithmembernames class * {
#    native <methods>;
#}

#To maintain custom components names that are used on layouts XML.
#Uncomment if having any problem with the approach below
#-keep public class custom.components.package.and.name.**

#To maintain custom components names that are used on layouts XML:
#-keep public class * extends android.view.View {
#    public <init>(android.content.Context);
#    public <init>(android.content.Context, android.util.AttributeSet);
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#    public void set*(...);
#}

#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet);
#}

#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#}

#Maintain enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#To keep parcelable classes (to serialize - deserialize objects to sent through Intents)
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#Keep the R
-keepclassmembers class **.R$* {
    public static <fields>;
}

###### ADDITIONAL OPTIONS NOT USED NORMALLY

#To keep callback calls. Uncomment if using any
#http://proguard.sourceforge.net/index.html#/manual/examples.html#callback
#-keep class mypackage.MyCallbackClass {
#   void myCallbackMethod(java.lang.String);
#}

#Uncomment if using Serializable
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
