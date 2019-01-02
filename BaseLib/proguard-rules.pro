# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\androidsdk\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and orderManager by changing the proguardFiles
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

#BaseRecyclerViewAdapterHelper
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers public class * extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(android.view.View);
}

-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**


#-dontwarn java.beans.**
#-dontwarn java.awt.**
#-dontwarn javax.swing.**
#
#-dontwarn net.soureceforge.pinyin4j.**
#-dontwarn demo.**
#-keep class demo.**{*;}
#-libraryjars libs/pinyin4j-2.5.0.jar
#-keep class net.sourceforge.pinyin4j.** { *;}
#-keep class net.sourceforge.pinyin4j.format.**{*;}
#-keep class net.sourceforge.pinyin4j.format.exception.**{*;}
#-keep class demo.** { *;}

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
