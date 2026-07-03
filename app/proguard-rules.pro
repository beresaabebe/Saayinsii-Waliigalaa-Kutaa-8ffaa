# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\SDK\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# Facebook Audience Network
-keep class com.facebook.ads.** { *; }
-dontwarn com.facebook.ads.**

# Google Play Services Ads
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# Unity Ads
-keep class com.unity3d.ads.** { *; }
-keep class com.unity3d.services.** { *; }
-dontwarn com.unity3d.ads.**
-dontwarn com.unity3d.services.banners.BannerErrorCode
-dontwarn com.unity3d.services.banners.BannerErrorInfo
-dontwarn com.unity3d.services.banners.BannerView$IListener
-dontwarn com.unity3d.services.banners.BannerView
-dontwarn com.unity3d.services.banners.UnityBannerSize

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# App Update & Review
-keep class com.google.android.play.core.** { *; }

# R8 fix for Missing classes
-dontwarn com.facebook.infer.annotation.**
-dontwarn javax.annotation.**
-dontwarn org.checkerframework.**
