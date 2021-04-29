-dontobfuscate

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**
-dontwarn com.fasterxml.jackson.databind.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepattributes *Annotation*,EnclosingMethod,Signature

-keep class com.fasterxml.jackson.** { *; }
-keep class org.codehaus.** { *; }
-keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
    public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** e(...);
    public static *** i(...);
    public static *** v(...);
}

-keep class com.newrelic.** { *; }
-dontwarn com.newrelic.**
-keepattributes Exceptions, Signature, InnerClasses, LineNumberTable, SourceFile, EnclosingMethod

-keep class io.card.** { *; }

-keep class com.entersekt.proto.** { *; }
-keep class com.entersekt.scan2pay.** { *; }
-keep class com.entersekt.sdk.** { *; }
-keep class android.security.keystore.** { *; }
-keep class android.security.keystore.*$* { *; }
-keep class android.security.keystore.KeyGenParameterSpec$Builder { *; }
-keepclassmembers class android.security.keystore.KeyGenParameterSpec$Builder { *; }
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.*$* { *; }
-keep class net.sqlcipher.database.SQLiteCompiledSql { *; }
-keep class net.sqlcipher.database.SQLiteDatabase$* { *; }

-keep class * extends android.app.** { *; }
-keep class * extends android.content.**
-keep class * implements com.barclays.absa.banking.framework.framework.net.ServiceAgent
-keep class * implements com.barclays.absa.banking.framework.parsers.ResponseParser
-keep class android.app.job.JobScheduler { *; }
-keep class android.support.v4.app.JobIntentService$JobServiceEngineImpl { *; }
-keep class android.app.AlarmManager$AlarmClockInfo
-keep class android.app.usage.NetworkStatsManager
-keep class android.content.RestrictionsManager
-keep class com.google.common.flogger.backend.google.GooglePlatform
-keep class com.google.common.flogger.backend.system.DefaultPlatform
-keep class org.chromium.net.aa

-keep class * extends android.app.backup.BackupAgentHelper
-keep class * extends android.preference.*Preference*

-keep class * implements javax.annotation.**
-keep class * implements android.os.Parcelable { *; }

-keep class * implements java.io.Serializable { *; }
-keep class android.os.Bundle
-keep class androidx.databinding.*

-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }
-keep class com.firebase.** { *; }
-keep class com.google.zxing.** { *; }
-keep class com.journeyapps.barcodescanner.** { *; }

-keep class com.adobe.mobile.**

-keep public class com.android.vending.licensing.ILicensingService
-keep class * extends android.app.NotificationChannel { *; }
-keep class * extends android.app.usage.UsageStatsManager { *; }
-keep class * extends android.app.usage.UsageStats { *; }

-keep class com.barclays.absa.banking.framework.data.ApplicationFlowType { *; }
-keep class com.barclays.absa.banking.boundary.sureCheck.**
-keep class za.co.absa.twoFactor.**
-keep class android.support.constraint.** { *; }
-keep class android.support.constraint.solver.** { *; }
-keep class android.support.constraint.solver.widgets.** { *; }

-keep class com.barclays.absa.banking.framework.twoFactorAuthentication.** { *; }
-keep class com.barclays.absa.banking.framework.AbstractResponseListener { *; }
-keep class com.barclays.absa.banking.framework.api.request.** { *; }
-keep class com.barclays.absa.banking.framework.app.** { *; }
-keep class com.barclays.absa.banking.framework.featureSwitching.** { *; }

-keep class com.barclays.absa.banking.boundary.** { *; }

-keep class com.barclays.absa.banking.shared.BaseModel

# Express
-keep class za.co.absa.networking.dto.** { *; }
-keep class * extends za.co.absa.networking.dto.BaseResponse { *; }
-keep class * implements za.co.absa.networking.hmac.utils.StubHelper { *; }
-keep class * implements za.co.absa.networking.hmac.service.ApiService { *; }
-keep class * implements za.co.absa.networking.hmac.service.BaseRequest { *; }
-keep class * implements za.co.absa.networking.dto.BaseResponseHeader { *; }
-keep class za.co.absa.networking.hmac.service.** { *; }

-keep class * implements com.barclays.absa.banking.shared.BaseModel  { *; }
-keep class com.barclays.absa.banking.newToBank.services.dto.**  { *; }

-keep class com.barclays.absa.banking.presentation.shared.bottomSheet.** { *; }
-keep class com.barclays.absa.banking.presentation.shared.bottomSheet.BottomSheet$Builder { *; }

-keep class * extends com.imimobile.connect.core.** { *; }
-keep class com.imimobile.** { *; }
-keep class org.eclipse.** { *; }

-keep class com.airbnb.lottie.** { *; }

-keep class com.oneidentity.sdk.android.** { *;}
-keep class okhttp3.** { *; }

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
#-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontwarn okhttp3.internal.**

-dontwarn com.squareup.okhttp.**
-dontwarn javax.management.**
-dontwarn retrofit.**
-dontwarn org.mortbay.util.ajax.**
-dontwarn org.mortbay.log.**
-dontwarn org.slf4j.**
-dontwarn javax.imageio.*
-dontwarn javax.naming.*
-dontwarn okhttp3.OkUrlFactory

#rules specific to IBM Trusteer SDK integration (device profiling)
-keep class !**.atas,!**.tas,com.trusteer.** { *; }
-keep class org.azeckoski.reflectutils.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

#-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep public class com.trusteer.tas.tas {
  public static int TasSetSignalForFunction(int, java.lang.String);
}
-keep public class com.trusteer.tas.atas {
  static private void ExceptionNotice(java.lang.String);
  static private void RunOverlayFoundCallback(java.lang.String,java.lang.String,java.lang.String);
  static private com.trusteer.tas.TAS_EXTERNAL_NET_CALLBACK_OUTPUTS RunExternalNetCallback(com.trusteer.tas.TAS_EXTERNAL_NET_CALLBACK_INPUTS);
}

-dontwarn android.**

# IMI SDK
-dontwarn org.eclipse.jetty.**
-dontwarn com.google.firebase.messaging.FirebaseMessaging
-dontwarn javax.servlet.**

-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.InputMerger
-keep public class * extends androidx.work.ListenableWorker {
    public <init>(...);
}
-keep class androidx.work.WorkerParameters