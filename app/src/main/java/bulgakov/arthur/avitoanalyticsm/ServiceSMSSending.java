package bulgakov.arthur.avitoanalyticsm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import bulgakov.arthur.avitoanalyticsm.content.Ad;
import bulgakov.arthur.avitoanalyticsm.content.Search;
import bulgakov.arthur.avitoanalyticsm.ui.MainActivity;
import bulgakov.arthur.avitoanalyticsm.utils.Constants;
import bulgakov.arthur.avitoanalyticsm.utils.Utils;


public class ServiceSMSSending extends Service {
   private static NotificationManager notificationManager;
   private static SharedPreferences prefs;
   private static Context context;
   private ArrayList<Ad> trackedAdsList;
   private WebView webView;

   public static void sendNotification(JSONArray jsonArray, String error) {
      Log.d(Constants.APP_TAG, "notifyng..");
      NotificationCompat.Builder builder = getPreBuilder();
      Intent intent = new Intent();
      String title = "Авито Аналитика, ошибка:";
      String text = error;
      String btitle = title;
      String btext = text;
      if (jsonArray != null) {
         intent = new Intent(context, MainActivity.class);
         intent.putExtra("ads_json_array", jsonArray.toString());
         title = Utils.stringFromRes(context, R.string.app_name);
         text = "Найдено объявлений: " + jsonArray.length();
         builder.setContentTitle(title);
         builder.setContentText(text);
         try {
            btitle = text;
            btext = "";
            for (int i = 0; i < jsonArray.length(); i++) {
               btext += "[" + (i + 1) + "] " + jsonArray.getJSONObject(i).getString("title") +
                       ", " + jsonArray.getJSONObject(i).getString("price");
               if (i != jsonArray.length() - 1)
                  btext += "\n";
            }
         } catch (JSONException ignored) {
         }
      }
      builder.setContentTitle(title);
      builder.setContentText(text);
      NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
      bigTextStyle.setBigContentTitle(btitle);
      bigTextStyle.bigText(btext);
      builder.setStyle(bigTextStyle);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
      PendingIntent pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
      Notification notification = builder.setContentIntent(pending).build();
      notification.defaults |= Notification.DEFAULT_SOUND;
      notificationManager.notify(0, notification);
   }

   private static NotificationCompat.Builder getPreBuilder() {
      return new NotificationCompat.Builder(context)
              .setAutoCancel(true)
              .setSmallIcon(R.drawable.ic_launcher)
              .setWhen(System.currentTimeMillis())
              .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
              .setVibrate(new long[]{0, 1000, 100, 1000, 100, 1000})
              .setLights(Color.BLUE, 3000, 3000);
   }

   @Override
   public IBinder onBind(Intent intent) {
      return (null);
   }

   @Override
   public void onCreate() {
      Log.d(Constants.APP_TAG, "onCreate Service");
      super.onCreate();
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      super.onStartCommand(intent, flags, startId);
      context = getApplicationContext();
      notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      prefs = PreferenceManager.getDefaultSharedPreferences(context);
      setAlarmManager();
      if (prefs.getBoolean("check_box_preference_tracking", false)) {
         trackedAdsList = new ArrayList<>();
         trackSearches(0);
      }
      return START_NOT_STICKY;
   }

   private void trackSearches(final int searchNum) {
      int searchesSize = prefs.getInt(Constants.SAVED_SEARCH_SIZE_KEY, 0);
      Log.d(Constants.APP_TAG, "TRACKING#" + searchNum);
      if (searchNum < searchesSize) {
         final Search search = new Search(prefs).fromJson(prefs.getString(Constants.SAVED_SEARCH_KEY
                 + searchNum, null));
         new GetAdsTask(new GetAdsTask.OnAdsReceivedListener() {
            @Override
            public void onAdsReceived(ArrayList<Ad> newAdsList, String flag) {
               if (newAdsList != null && !newAdsList.isEmpty()) {
                  while (newAdsList.size() > Constants.TRACKING_ADS_NUM)
                     newAdsList.remove(Constants.TRACKING_ADS_NUM);
                  String oldIdsJsonString = prefs.getString(Constants.SAVED_SEARCH_ADS_KEY + searchNum, null);
                  if (oldIdsJsonString != null && !oldIdsJsonString.isEmpty())
                     try {
                        JSONArray oldIdsJson = new JSONArray(oldIdsJsonString);
                        HashSet<String> oldIdsHashSet = new HashSet<>();
                        for (int i = 0; i < oldIdsJson.length(); i++)
                           oldIdsHashSet.add(oldIdsJson.getString(i));
                        for (Ad ad : newAdsList)
                           if (!oldIdsHashSet.contains(ad.getId())) {
                              trackedAdsList.add(ad);
                           }
                     } catch (JSONException e) {
                        Log.d(Constants.APP_TAG, "timer task json exception");
                     }
                  JSONArray jsonArrayIds = new JSONArray();
                  for (Ad ad : newAdsList) {
                     jsonArrayIds.put(ad.getId());
                  }
                  prefs.edit().putString(Constants.SAVED_SEARCH_ADS_KEY + searchNum,
                          jsonArrayIds.toString()).commit();
               }
               trackSearches(searchNum + 1);
            }
         }, 1, search, prefs).execute((Void) null);
      }
      if (searchNum == searchesSize && !trackedAdsList.isEmpty()) {
         JSONArray trackedAdsJson = new JSONArray();
         for (Ad ad : trackedAdsList) {
            if (ad.isPresent()) {
               trackedAdsJson.put(ad.toJson());
            }
         }
         if (trackedAdsJson.length() > 0) {
            ServiceSMSSending.sendNotification(trackedAdsJson, null);
         }
      }
   }

   private void setAlarmManager() {
      Intent intent = new Intent(context, ServiceSMSSending.class);
      PendingIntent pintent = PendingIntent.getService(context, 0, intent, 0);
      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      calendar.add(Calendar.MINUTE, 1);
      long timeMS = calendar.getTimeInMillis();
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
         alarmManager.set(AlarmManager.RTC_WAKEUP, timeMS, pintent);
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
         alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeMS, pintent);
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMS, pintent);
      }
   }
}