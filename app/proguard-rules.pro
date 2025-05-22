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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# --- Kotlinx Serialization ---
# Keep classes annotated with @Serializable and their members needed for serialization.
# This is crucial if you're using kotlinx.serialization for JSON, etc.
-keepnames class * implements kotlinx.serialization.Serializable
-keepclassmembers class * implements kotlinx.serialization.Serializable {
    static <fields>; # Keep static fields, often used for default instances or serializers
    <init>(...);    # Keep constructors
}
# Keep companion objects of serializable classes and their serializer() method
-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
# Keep generated serializers (common pattern for kotlinx.serialization)
-keep class **$$serializer { *; }
-keep class kotlinx.serialization.internal.* # Keep internal serialization classes

# --- Jetpack Compose ---
# While AGP and R8 have good defaults for Compose, these can be helpful
# if you encounter issues with Composable functions being removed or obfuscated incorrectly.
# Usually, 'proguard-android-optimize.txt' handles much of this.
-keepclasseswithmembers public class * {
    @androidx.compose.runtime.Composable <methods>;
}
-keepclassmembers public class * {
    @androidx.compose.runtime.Composable <methods>;
}
# Keep classes that are part of the Compose runtime or UI toolkit if issues arise
# -keep class androidx.compose.** { *; } # Use cautiously, can prevent shrinking

# --- Hilt (Dependency Injection) ---
# Hilt generally works well with R8, and its Gradle plugin handles most rules.
# However, if you use @EntryPoint or other advanced Hilt features and face issues,
# you might need to consult Hilt's documentation for specific rules.
# The following are common general rules, but often not strictly needed if Hilt plugin is active.
 -keepclassmembers class * {
     @javax.inject.Inject <init>(...);
     @javax.inject.Inject <fields>;
     @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
 }
 -keep @dagger.hilt.android.HiltAndroidApp class *
 -keep @dagger.hilt.android.AndroidEntryPoint class *
 -keep @dagger.hilt.android.lifecycle.HiltViewModel class *
 -keep @dagger.Module class *
 -keep @dagger.Provides @interface *
 -keep @javax.inject.Inject @interface *

# --- General Kotlin ---
# Keep Kotlin metadata for reflection if you rely on it heavily.
# This can increase APK size, so use if specifically needed.
# -keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,InnerClasses,Signature,EnclosingMethod,KotlinMetaData

# Keep all data classes and their properties if you use them with reflection extensively
# or with libraries that rely on reflective access to all properties.
# Be specific with package names to avoid keeping too much.
# -keep class com.yourpackage.data.model.** { *; }

# --- AndroidX Core / Lifecycle / Navigation ---
# These libraries are generally well-behaved with R8 and often include their own consumer rules.
# You typically don't need to add specific rules for them unless you hit a specific issue.

# --- Debugging Stack Traces (Optional but Recommended for Release) ---
# Uncomment this to preserve line number information for more useful stack traces
# from your release builds (e.g., in Firebase Crashlytics).
# This slightly increases APK size but is invaluable for debugging.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name from the stack trace (minor obfuscation).
# -renamesourcefileattribute SourceFile # Usually, you want the original source file name for debugging

# --- Keep Custom Views (if any) ---
# If you have custom views inflated from XML with custom attributes.
# -keep public class * extends android.view.View {
#    public <init>(android.content.Context);
#    public <init>(android.content.Context, android.util.AttributeSet);
#    public <init>(android.content.Context, android.util.AttributeSet, int);
# }

# --- Keep Parcelable classes and their CREATOR field (if any) ---
# -keep class * implements android.os.Parcelable {
#  public static final android.os.Parcelable$Creator *;
# }

# --- Specific classes you know must not be obfuscated or removed ---
# Example:
# -keep class com.example.MyImportantClass
# -keepclassmembers class com.example.AnotherClass {
#     public void importantMethod(...);
# }