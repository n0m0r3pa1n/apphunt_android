[1mdiff --git a/app/src/main/java/com/apphunt/app/constants/Constants.java b/app/src/main/java/com/apphunt/app/constants/Constants.java[m
[1mindex 4ca4bdb..13d226d 100644[m
[1m--- a/app/src/main/java/com/apphunt/app/constants/Constants.java[m
[1m+++ b/app/src/main/java/com/apphunt/app/constants/Constants.java[m
[36m@@ -7,8 +7,8 @@[m [mpublic class Constants {[m
     public static final String PACKAGE_NAME = "com.apphunt.app";[m
 [m
     //TODO: use production url before release[m
[31m-//    public static final String MAIN_URL = "apphunt-dev.herokuapp.com";[m
[31m-    public static final String MAIN_URL = "apphunt.herokuapp.com";[m
[32m+[m[32m    public static final String MAIN_URL = "apphunt-dev.herokuapp.com";[m
[32m+[m[32m//    public static final String MAIN_URL = "apphunt.herokuapp.com";[m
 //    public static final String MAIN_URL = "10.0.3.2:8080";[m
 //    public static final String MAIN_URL = "12fe4f95.ngrok.io";[m
 [m
[1mdiff --git a/app/src/main/java/com/apphunt/app/constants/TrackingEvents.java b/app/src/main/java/com/apphunt/app/constants/TrackingEvents.java[m
[1mindex 413ac0c..3d192aa 100644[m
[1m--- a/app/src/main/java/com/apphunt/app/constants/TrackingEvents.java[m
[1m+++ b/app/src/main/java/com/apphunt/app/constants/TrackingEvents.java[m
[36m@@ -93,6 +93,7 @@[m [mpublic interface TrackingEvents extends LoginEvents, TrendingAppsEvents, History[m
     String UserInstalledApp = "user.installed.app";[m
     String UserRemovedApp = "user.removed.app";[m
     String UserOpenedAdd = "user.opened.add";[m
[32m+[m[32m    String UserOpenedPaidAdd = "user.opened.paid.ad";[m
     String UserDisabledDailyNotification = "user.disabled.daily.notification";[m
     String UserEnabledDailyNotification = "user.enabled.daily.notification";[m
     String UserEnabledInstalledAppNotification = "user.enabled.installed.app.notification";[m
[1mdiff --git a/app/src/main/java/com/apphunt/app/ui/fragments/DailyAppsFragment.java b/app/src/main/java/com/apphunt/app/ui/fragments/DailyAppsFragment.java[m
[1mindex 3b13ec8..ac71d59 100644[m
[1m--- a/app/src/main/java/com/apphunt/app/ui/fragments/DailyAppsFragment.java[m
[1m+++ b/app/src/main/java/com/apphunt/app/ui/fragments/DailyAppsFragment.java[m
[36m@@ -192,6 +192,7 @@[m [mpublic class DailyAppsFragment extends BaseFragment {[m
 [m
     @Subscribe[m
     public void onAppsLoaded(LoadAppsApiEvent event) {[m
[32m+[m[32m        appsLoadedEventCount++;[m
         if(event.getAppsList() == null) {[m
             shouldChangeDate = true;[m
         } else {[m
[36m@@ -202,7 +203,7 @@[m [mpublic class DailyAppsFragment extends BaseFragment {[m
             dailyAppsAdapter.addItem(0, new AppHuntAdItem());[m
         }[m
 [m
[31m-        if(appsLoadedEventCount % 2 == 0) {[m
[32m+[m[32m        if(appsLoadedEventCount == 2 || appsLoadedEventCount % 4 == 0) {[m
             dailyAppsAdapter.addItem(new PaidAdItem());[m
         }[m
 [m
[1mdiff --git a/app/src/main/java/com/apphunt/app/ui/views/ads/PaidAdView.java b/app/src/main/java/com/apphunt/app/ui/views/ads/PaidAdView.java[m
[1mindex 591097c..4440bec 100644[m
[1m--- a/app/src/main/java/com/apphunt/app/ui/views/ads/PaidAdView.java[m
[1m+++ b/app/src/main/java/com/apphunt/app/ui/views/ads/PaidAdView.java[m
[36m@@ -12,7 +12,9 @@[m [mimport android.widget.LinearLayout;[m
 [m
 import com.apphunt.app.R;[m
 import com.apphunt.app.api.apphunt.clients.rest.ApiClient;[m
[32m+[m[32mimport com.apphunt.app.constants.TrackingEvents;[m
 import com.apphunt.app.event_bus.BusProvider;[m
[32m+[m[32mimport com.apphunt.app.utils.FlurryWrapper;[m
 import com.crashlytics.android.Crashlytics;[m
 import com.google.android.gms.ads.AdListener;[m
 import com.google.android.gms.ads.AdRequest;[m
[36m@@ -89,6 +91,12 @@[m [mpublic class PaidAdView extends LinearLayout {[m
                 isAdLoaded = true;[m
                 Log.d(TAG, "onAdLoaded: ");[m
             }[m
[32m+[m
[32m+[m[32m            @Override[m
[32m+[m[32m            public void onAdOpened() {[m
[32m+[m[32m                super.onAdOpened();[m
[32m+[m[32m                FlurryWrapper.logEvent(TrackingEvents.UserOpenedPaidAdd);[m
[32m+[m[32m            }[m
         });[m
         adView.loadAd(adRequest);[m
     }[m
[1mdiff --git a/app/src/main/res/layout/layout_paid_ad.xml b/app/src/main/res/layout/layout_paid_ad.xml[m
[1mindex 8f76a2b..0bcd09b 100644[m
[1m--- a/app/src/main/res/layout/layout_paid_ad.xml[m
[1m+++ b/app/src/main/res/layout/layout_paid_ad.xml[m
[36m@@ -9,6 +9,8 @@[m
     android:layout_height="wrap_content">[m
     <com.google.android.gms.ads.AdView[m
         android:id="@+id/adView"[m
[32m+[m[32m        android:layout_marginTop="8dp"[m
[32m+[m[32m        android:layout_marginBottom="4dp"[m
         android:layout_width="wrap_content"[m
         android:layout_height="wrap_content"[m
         android:layout_centerHorizontal="true"[m
