# =============================================================================
# JO Social Hub — ProGuard / R8 rules
# =============================================================================
#
# R8 is the default shrinker for release builds. These rules tell it what NOT
# to remove or rename. Every rule is justified so future maintainers know why
# it exists.
#
# General principle:
#   - Keep only what R8 cannot infer on its own (reflection, JNI, serialization)
#   - Let R8 shrink and obfuscate everything else
# =============================================================================


# -----------------------------------------------------------------------------
# 1. DEBUG INFO — keep line numbers in crash stack traces
# -----------------------------------------------------------------------------
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile


# -----------------------------------------------------------------------------
# 2. KOTLIN — coroutines, sealed classes, data classes
# -----------------------------------------------------------------------------

# Coroutines use reflection to find the `invokeSuspend` method
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.** {
    volatile <fields>;
}

# Kotlin Serialization / metadata — needed for inline functions & reflection
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod


# -----------------------------------------------------------------------------
# 3. ROOM — entities, DAOs, database
# -----------------------------------------------------------------------------

# Room generates implementation classes at compile time via KSP.
# The entity fields must keep their names so Room can map columns correctly.
-keep class com.example.data.Account           { *; }
-keep class com.example.data.Post              { *; }
-keep class com.example.data.ChatMessage       { *; }
-keep class com.example.data.BrandKit          { *; }
-keep class com.example.data.MediaItem         { *; }
-keep class com.example.data.User              { *; }
-keep class com.example.data.SocialProfile     { *; }
-keep class com.example.data.AuthenticationLog { *; }

# Keep DAO interfaces (accessed via reflection by Room at runtime)
-keep interface com.example.data.*Dao { *; }

# Room cursor adapters — generated code uses reflection
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static ** INSTANCE;
    public abstract ** *Dao();
}


# -----------------------------------------------------------------------------
# 4. RETROFIT + OKHTTP — API service interfaces
# -----------------------------------------------------------------------------

# Retrofit uses reflection to create interface implementations
-keep interface com.example.data.api.GeminiApiService { *; }

# Retrofit core reflection
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }


# -----------------------------------------------------------------------------
# 5. MOSHI — JSON serialisation / deserialisation
# -----------------------------------------------------------------------------

# Moshi uses reflection on @JsonClass annotated data classes.
# KSP generates adapters, but the data class fields must stay named.
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}

# Moshi generated adapter classes (created by moshi-kotlin-codegen KSP)
-keep class **JsonAdapter { *; }

# Moshi Kotlin reflection adapter
-keep class com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


# -----------------------------------------------------------------------------
# 6. HILT — dependency injection
# -----------------------------------------------------------------------------

# Hilt generates component and module implementations — keep them all
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keepclasseswithmembers class * {
    @javax.inject.Inject <init>(...);
}


# -----------------------------------------------------------------------------
# 7. FIREBASE — AI, AppCheck
# -----------------------------------------------------------------------------

# Firebase uses reflection internally; its own consumer rules handle most of it,
# but we keep the top-level package from being stripped
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**


# -----------------------------------------------------------------------------
# 8. JETPACK COMPOSE — runtime and compiler
# -----------------------------------------------------------------------------

# Compose compiler plugin generates classes that must survive obfuscation
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Composable function names for tooling / crash reporting
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}


# -----------------------------------------------------------------------------
# 9. COIL — image loading
# -----------------------------------------------------------------------------
-dontwarn coil.**
-keep class coil.** { *; }

# -----------------------------------------------------------------------------
# 9b. BCRYPT — at.favre.lib password hashing
# -----------------------------------------------------------------------------
-keep class at.favre.lib.crypto.bcrypt.** { *; }
-dontwarn at.favre.lib.**


# -----------------------------------------------------------------------------
# 10. PROJECT — domain and use case layer
# -----------------------------------------------------------------------------

# Keep sealed Result class and its subclasses (used in when expressions)
-keep class com.example.domain.model.Result { *; }
-keep class com.example.domain.model.Result$* { *; }
-keep class com.example.domain.model.PostAnalysisResult { *; }

# Keep use case classes — injected by Hilt, called via operator fun invoke
-keep class com.example.domain.usecase.** { *; }

# Keep ViewModel — Hilt ViewModel factory uses reflection
-keep class com.example.ui.JovimViewModel { *; }
-keep class com.example.ui.JovimUiState   { *; }
-keep enum  com.example.ui.AppScreen      { *; }


# -----------------------------------------------------------------------------
# 11. SUPPRESS KNOWN HARMLESS WARNINGS
# -----------------------------------------------------------------------------
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
-dontwarn javax.annotation.**
-dontwarn sun.misc.Unsafe
