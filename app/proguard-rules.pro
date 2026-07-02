-keepattributes *Annotation*
-keep class com.radonshadow.focusdrift.data.remote.dto.** { *; }
-keep class com.radonshadow.focusdrift.data.local.database.entity.** { *; }

# Hilt / Dagger generated components and entry points
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper
-keep,allowobfuscation @interface dagger.hilt.android.lifecycle.HiltViewModel
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# Hilt-injected Workers and their generated factories
-keep class * extends androidx.work.ListenableWorker {
    public <init>(...);
}
-keep @androidx.hilt.work.HiltWorker class * { *; }
-keep class **_HiltModules$* { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }

# Room-generated implementations
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**

# Firebase Realtime Database deserializes DTOs via reflection using their public no-arg
# constructor and getters/setters -- already covered by the dto keep rule above.
-keepclassmembers class com.radonshadow.focusdrift.data.remote.dto.** {
    <init>();
    <fields>;
}

# Kotlin coroutines / Compose runtime stability
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
