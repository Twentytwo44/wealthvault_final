# Keep runtime annotations used by Kotlin serialization and Koin's generated
# definitions. Concrete application classes remain shrinkable and obfuscatable.
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeInvisibleParameterAnnotations,AnnotationDefault,InnerClasses,EnclosingMethod,Signature

# Ktor/Kotlin serialization can inspect serializer metadata when a model is
# transported through a generic API boundary.
-keep class kotlinx.serialization.** { *; }
-keep class kotlinx.serialization.json.** { *; }

# Voyager restores Screen/ScreenModel state by type on process recreation.
-keep class cafe.adriel.voyager.** { *; }

# SQLDelight generated adapters are referenced through generated interfaces.
-keep class app.cash.sqldelight.** { *; }

# The LINE SDK's optional data-binding artifact references its generated BR
# class even when that optional UI is not packaged in this application.
-dontwarn com.linecorp.linesdk.BR

# Never allow R8 to retain or print secret values from logging helpers.
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
}
