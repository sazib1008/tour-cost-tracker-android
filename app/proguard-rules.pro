# Add project specific ProGuard rules here.
-dontwarn javax.annotation.**
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.squareup.moshi.* <methods>;
}
