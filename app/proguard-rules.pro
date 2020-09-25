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
-keepattributes SourceFile,LineNumberTable,*Annotation*,EnclosingMethod,Signature

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# keep all native method names
# https://www.guardsquare.com/en/products/proguard/manual/usage
# https://www.guardsquare.com/en/products/proguard/manual/examples#native
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# Google Gson library -- Start
-keep class com.google.gson.* { *; }
# Gson - End

# Glide -- Start
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
# Glide -- End

# MathView - Start
#-dontwarn com.x5.**
#-keep class com.x5.template.** { *; }
#-keep class com.x5.util.** { *; }
# MathView - End


# Flurry SDK
#-keep class com.flurry.** { *; }
#-dontwarn com.flurry.**
#-keepattributes *Annotation*,EnclosingMethod,Signature
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#}


# Crashlytics -- Start
# https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports-fabric-sdk?platform=android
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
# Crashlytics -- End


# OkHttp -- start
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# OkHttp -- end
