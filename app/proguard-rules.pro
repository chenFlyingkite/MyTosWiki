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
#-renamesourcefileattribute SourceFile

# Google Gson library -- Start
-keep class com.google.gson.** { *; }
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

# Chunk in MathView
# https://github.com/tomj74/chunk-templates/
# https://github.com/kennytm/CatSaver/blob/master/app/proguard-rules.pro

###### Chunk

-dontwarn sun.misc.BASE64Decoder
-dontwarn sun.misc.BASE64Encoder
# ^ we don't use base64 filters

-assumenosideeffects class com.csvreader.CsvReader { *; }
# ^ we are not using the *.csv files for localization, it's safe to ignore it. (this saves ~8 KB)

-dontwarn org.cheffo.jeplite.**
# ^ we don't use the `calc` filter, only `qcalc` which doesn't need the expression parser

-dontwarn com.madrobot.beans.**
-dontwarn java.beans.**
# ^ we don't use Java Beans.

-assumenosideeffects class com.x5.template.MacroTag {
    *** *Json*(...);
}
# ^ we don't use macros. Don't let it introduce yet another JSON library. (this saves ~4 KB)



# OkHttp
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.** { *; }
# Ignore warnings: https://github.com/square/okhttp/wiki/FAQs
-dontwarn com.squareup.okhttp.internal.huc.**
# Ignore warnings: https://github.com/square/okio/issues/60
-dontwarn okio.**
# --- End : OkHttp ---


# Flurry SDK
#-keep class com.flurry.** { *; }
#-dontwarn com.flurry.**
#-keepattributes *Annotation*,EnclosingMethod,Signature
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#}

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# Crashlytics -- Start
# https://docs.fabric.io/android/crashlytics/dex-and-proguard.html
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-printmapping mapping.txt
# Crashlytics -- End
