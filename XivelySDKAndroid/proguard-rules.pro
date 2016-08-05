# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames

-dontskipnonpubliclibraryclasses

-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclasseswithmembernames public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclasseswithmembernames class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclasseswithmembernames enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclasseswithmembernames class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# Xively Android settings

# There's no way to keep all @Observes methods, so use the On*Event convention to identify event handlers
-keepclasseswithmembernames class * {
    void *(**On*Event);
}

#Gson
-keep class com.google.gson.** { *; }

#Retrofit
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keep class rx.** { *; }
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.** { *; }
-keep class com.google.appengine.**

-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn retrofit.**
-dontwarn rx.**

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

#keep ws requests and responses for Retrofit
-keep class com.xively.internal.rest.** { public *; }

#Paho
-keep class org.eclipse.paho.** { *; }

#Xively SDK - API
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-keepparameternames

-keep class com.xively.XiException { *; }
-keep class com.xively.XiException$** { *; }
-keep class com.xively.XiSdkConfig { *; }
-keep enum com.xively.XiSdkConfig$** { *; }
-keep class com.xively.XiService { *; }
-keep class com.xively.XiServiceCreator { *; }
-keep class com.xively.XiServiceCreatorCallback { *; }
-keep class com.xively.XiSession { *; }
-keep class com.xively.XiSession$** { *; }
-keep class com.xively.auth.** { *; }
-keep class com.xively.messaging.** { *; }
-keep class com.xively.timeseries.** { *; }

-keepclasseswithmembernames class com.xively.XiException { *; }
-keepclasseswithmembernames class com.xively.XiException$** { *; }
-keepclasseswithmembernames class com.xively.XiSdkConfig { *; }
-keepclasseswithmembernames enum com.xively.XiSdkConfig$** { *; }
-keepclasseswithmembernames class com.xively.XiService { *; }
-keepclasseswithmembernames class com.xively.XiServiceCreator { *; }
-keepclasseswithmembernames class com.xively.XiServiceCreatorCallback { *; }
-keepclasseswithmembernames class com.xively.XiSession { *; }
-keepclasseswithmembernames class com.xively.XiSession$** { *; }
-keepclasseswithmembernames class com.xively.auth.** { *; }
-keepclasseswithmembernames class com.xively.messaging.** { *; }
-keepclasseswithmembernames class com.xively.timeseries.** { *; }

#Xively SDK - internal
# Temporarily keep some internal classes for development purposes
-keep class com.xively.internal.account.XivelyAccount { *; }
-keep class com.xively.internal.XiSessionImpl { *; }

#Obfuscate everything else internal and flatten package hierarchy
-keeppackagenames !com.xively.internal**
-repackageclasses ''
