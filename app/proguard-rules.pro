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

# Preserve useful crash stack information when R8 is enabled.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Room stores Board as Gson JSON. Keep these field names stable so saved games
# survive app updates from non-minified to minified builds.
-keep class ropa.miragaya.sudokupremium.domain.model.Board { *; }
-keep class ropa.miragaya.sudokupremium.domain.model.Cell { *; }

# Keep typed navigation routes stable for Navigation Compose serialization.
-keep class ropa.miragaya.sudokupremium.ui.navigation.** { *; }
