package bulgakov.arthur.avitoanalyticsm.utils;

/**
 * Created by A on 24.04.2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.ui.MainActivity;

public class Utils {
   static final String TAG = Constants.APP_TAG;
   public static final String MIME_TYPE_AUDIO = "audio/*";
   public static final String MIME_TYPE_TEXT = "text/*";
   public static final String MIME_TYPE_IMAGE = "image/*";
   public static final String MIME_TYPE_VIDEO = "video/*";
   public static final String MIME_TYPE_APP = "application/*";
   public static final String HIDDEN_PREFIX = ".";

   public static final String DATE_FORMAT = "yyyy/MMMM/dd HH:mm";
   /**
    * File (not directories) filter.
    *
    * @author paulburke
    */
   public static FileFilter sFileFilter = new FileFilter() {
      @Override
      public boolean accept(File file) {
         final String fileName = file.getName();
         // Return files only (not directories) and skip hidden files
         return file.isFile() && !fileName.startsWith(HIDDEN_PREFIX);
      }
   };
   /**
    * Folder (directories) filter.
    *
    * @autho r paulburke
    */
   public static FileFilter sDirFilter = new FileFilter() {
      @Override
      public boolean accept(File file) {
         final String fileName = file.getName();
         // Return directories only and skip hidden directories
         return file.isDirectory() && !fileName.startsWith(HIDDEN_PREFIX);
      }
   };
   private static final boolean DEBUG = false; // Set to true to enable logging
   /**
    * File and folder comparator. TODO Expose sorting option method
    *
    * @author paulburke
    */
   public static Comparator<File> sComparator = new Comparator<File>() {
      @Override
      public int compare(File f1, File f2) {
         // Sort alphabetically by lower case, which is much cleaner
         return f1.getName().toLowerCase().compareTo(
                 f2.getName().toLowerCase());
      }
   };
   //   public static Context appContext = null;
//   public static RoomActivity roomActivity = null;
//   public static SharedPreferences preferences;

   private Utils() {
   } //private constructor to enforce Singleton pattern

   /**
    * Gets the extension of a file name, like ".png" or ".jpg".
    *
    * @param uri
    * @return Extension including the dot("."); "" if there is no extension;
    * null if uri was null.
    */
   public static String getExtension(String uri) {
      if (uri == null) {
         return null;
      }

      int dot = uri.lastIndexOf(".");
      if (dot >= 0) {
         return uri.substring(dot);
      } else {
         // No extension.
         return "";
      }
   }

   /**
    * @return Whether the URI is a local one.
    */
   public static boolean isLocal(String url) {
      if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
         return true;
      }
      return false;
   }

   /**
    * @return True if Uri is a MediaStore Uri.
    * @author paulburke
    */
   public static boolean isMediaUri(Uri uri) {
      return "media".equalsIgnoreCase(uri.getAuthority());
   }

   /**
    * Convert File into Uri.
    *
    * @param file
    * @return uri
    */
   public static Uri getUri(File file) {
      if (file != null) {
         return Uri.fromFile(file);
      }
      return null;
   }

   /**
    * Returns the path only (without file name).
    *
    * @param file
    * @return
    */
   public static File getPathWithoutFilename(File file) {
      if (file != null) {
         if (file.isDirectory()) {
            // no file to be split off. Return everything
            return file;
         } else {
            String filename = file.getName();
            String filepath = file.getAbsolutePath();

            // Construct path without file name.
            String pathwithoutname = filepath.substring(0,
                    filepath.length() - filename.length());
            if (pathwithoutname.endsWith("/")) {
               pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
            }
            return new File(pathwithoutname);
         }
      }
      return null;
   }

   /**
    * @param uri The Uri to check.
    * @return Whether the Uri authority is {@link UtilsLocalStorageProvider}.
    * @author paulburke
    */
   public static boolean isLocalStorageDocument(Uri uri) {
      return UtilsLocalStorageProvider.AUTHORITY.equals(uri.getAuthority());
   }

   /**
    * @param uri The Uri to check.
    * @return Whether the Uri authority is ExternalStorageProvider.
    * @author paulburke
    */
   public static boolean isExternalStorageDocument(Uri uri) {
      return "com.android.externalstorage.documents".equals(uri.getAuthority());
   }

   /**
    * @param uri The Uri to check.
    * @return Whether the Uri authority is DownloadsProvider.
    * @author paulburke
    */
   public static boolean isDownloadsDocument(Uri uri) {
      return "com.android.providers.downloads.documents".equals(uri.getAuthority());
   }

   /**
    * @param uri The Uri to check.
    * @return Whether the Uri authority is MediaProvider.
    * @author paulburke
    */
   public static boolean isMediaDocument(Uri uri) {
      return "com.android.providers.media.documents".equals(uri.getAuthority());
   }

   /**
    * @param uri The Uri to check.
    * @return Whether the Uri authority is Google Photos.
    */
   public static boolean isGooglePhotosUri(Uri uri) {
      return "com.google.android.apps.photos.content".equals(uri.getAuthority());
   }

   /**
    * Get the value of the data column for this Uri. This is useful for
    * MediaStore Uris, and other file-based ContentProviders.
    *
    * @param uri           The Uri to query.
    * @param selection     (Optional) Filter used in the query.
    * @param selectionArgs (Optional) Selection arguments used in the query.
    * @return The value of the _data column, which is typically a file path.
    * @author paulburke
    */
   public static String getDataColumn(Context context, Uri uri, String selection,
                                      String[] selectionArgs) {

      Cursor cursor = null;
      final String column = "_data";
      final String[] projection = {
              column
      };

      try {
         cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                 null);
         if (cursor != null && cursor.moveToFirst()) {
            if (DEBUG)
               DatabaseUtils.dumpCursor(cursor);

            final int column_index = cursor.getColumnIndexOrThrow(column);
            return cursor.getString(column_index);
         }
      } finally {
         if (cursor != null)
            cursor.close();
      }
      return null;
   }

   /**
    * Get the file size in a human-readable string.
    *
    * @param size
    * @return
    * @author paulburke
    */
   public static String getReadableFileSize(int size) {
      final int BYTES_IN_KILOBYTES = 1024;
      final DecimalFormat dec = new DecimalFormat("###.#");
      final String KILOBYTES = " KB";
      final String MEGABYTES = " MB";
      final String GIGABYTES = " GB";
      float fileSize = 0;
      String suffix = KILOBYTES;

      if (size > BYTES_IN_KILOBYTES) {
         fileSize = size / BYTES_IN_KILOBYTES;
         if (fileSize > BYTES_IN_KILOBYTES) {
            fileSize = fileSize / BYTES_IN_KILOBYTES;
            if (fileSize > BYTES_IN_KILOBYTES) {
               fileSize = fileSize / BYTES_IN_KILOBYTES;
               suffix = GIGABYTES;
            } else {
               suffix = MEGABYTES;
            }
         }
      }
      return String.valueOf(dec.format(fileSize) + suffix);
   }

   /**
    * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
    * should not be called on the UI thread.
    *
    * @param uri
    * @param mimeType
    * @return
    * @author paulburke
    */
   public static Bitmap getThumbnail(Context context, Uri uri, String mimeType) {
      if (DEBUG)
         Log.d(Constants.APP_TAG, "Attempting to get thumbnail");

      if (!isMediaUri(uri)) {
         Log.e(Constants.APP_TAG, "You can only retrieve thumbnails for images and videos.");
         return null;
      }

      Bitmap bm = null;
      if (uri != null) {
         final ContentResolver resolver = context.getContentResolver();
         Cursor cursor = null;
         try {
            cursor = resolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
               final int id = cursor.getInt(0);
               if (DEBUG)
                  Log.d(TAG, "Got thumb ID: " + id);

               if (mimeType.contains("video")) {
                  bm = MediaStore.Video.Thumbnails.getThumbnail(
                          resolver,
                          id,
                          MediaStore.Video.Thumbnails.MINI_KIND,
                          null);
               } else if (mimeType.contains(Utils.MIME_TYPE_IMAGE)) {
                  bm = MediaStore.Images.Thumbnails.getThumbnail(
                          resolver,
                          id,
                          MediaStore.Images.Thumbnails.MINI_KIND,
                          null);
               }
            }
         } catch (Exception e) {
            if (DEBUG)
               Log.e(TAG, "getThumbnail", e);
         } finally {
            if (cursor != null)
               cursor.close();
         }
      }
      return bm;
   }

   /**
    * Get the Intent for selecting content to be used in an Intent Chooser.
    *
    * @return The intent for opening a file with Intent.createChooser()
    * @author paulburke
    */
   public static Intent createGetContentIntent() {
      // Implicitly allow the user to select a particular kind of data
      final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
      // The MIME data type filter
      intent.setType("*/*");
      // Only return URIs that can be opened with ContentResolver
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      return intent;
   }

   public static File saveBitmap(Bitmap bitmap, String dirPath, String fileName) {
      try {
         File dir = new File(dirPath);
         dir.mkdirs();
         File file = new File(dir, fileName + ".png");
         if (file.exists())
            file.delete();
         FileOutputStream fOut = new FileOutputStream(file);
         bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
         fOut.flush();
         fOut.close();
         return file;
      } catch (Exception e) {
         Log.d(Constants.APP_TAG, e.toString());
         return null;
      }
   }

   public static Bitmap getBitmapByPath(String path) throws Exception {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inPreferredConfig = Bitmap.Config.ARGB_8888;
      return BitmapFactory.decodeFile(path, options);
   }

   public static String getSaveDir(Activity activity) {
      return Environment.getExternalStorageDirectory().getAbsolutePath()
              + "/" + activity.getString(R.string.app_dir) + "/saved";
   }

   public static String getAppDir(Activity activity) {
      return Environment.getExternalStorageDirectory().getAbsolutePath()
              + "/" + activity.getString(R.string.app_dir);
   }

   public static String getTempDir(Activity activity) {
      return Environment.getExternalStorageDirectory().getAbsolutePath()
              + "/" + activity.getString(R.string.app_dir) + "/temp";
   }

   public static String getCacheDir(Activity activity) {
      return Environment.getExternalStorageDirectory().getAbsolutePath()
              + "/" + activity.getString(R.string.app_dir) + "/cached";
   }

   public static boolean isNoInternet(Context context) {
      try {
         ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
         return activeNetwork == null || !activeNetwork.isAvailable()
                 || !activeNetwork.isConnected();
      } catch (Exception e) {
         return true;
      }
   }

   public static int dpToPx(int dp, Context context) {
      return Math.round(dp * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
   }

   //picture of w*h -> picture of W*h1 or w1*H (proportional scaling)
   public static LinearLayout.LayoutParams scaleToLl(int w, int h, int W, int H) {
      float k;
      if ((float) h * W / w > H)
         k = (float) H / h;
      else
         k = (float) W / w;
      h *= k;
      w *= k;
      LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(w, h);
      return layoutParams;
   }

   public static int displayWidth;
   public static int displayHeight;

   public static void getDisplaySize(Activity roomActivity) {
      Display display = roomActivity.getWindowManager().getDefaultDisplay();

      if (Build.VERSION.SDK_INT >= 17) {
         //new pleasant way to get real metrics
         DisplayMetrics realMetrics = new DisplayMetrics();
         display.getRealMetrics(realMetrics);
         displayWidth = realMetrics.widthPixels;
         displayHeight = realMetrics.heightPixels;

      } else if (Build.VERSION.SDK_INT >= 14) {
         //reflection for this weird in-between time
         try {
            Method mGetRawH = Display.class.getMethod("getRawHeight");
            Method mGetRawW = Display.class.getMethod("getRawWidth");
            displayWidth = (Integer) mGetRawW.invoke(display);
            displayHeight = (Integer) mGetRawH.invoke(display);
         } catch (Exception e) {
            //this may not be 100% accurate, but it's all we've got
            displayWidth = display.getWidth();
            displayHeight = display.getHeight();
//            Log.d("!", "Couldn't use reflection to get the real display metrics.");
         }

      } else {
         //This should be close, as lower API devices should not have window navigation bars
         displayWidth = display.getWidth();
         displayHeight = display.getHeight();
      }
   }

   public static Bitmap getResizedBitmap(Bitmap bitmap) {
      if (bitmap == null)
         return null;
      int maxSize = 1000;
      int width = bitmap.getWidth();
      int height = bitmap.getHeight();

      float bitmapRatio = (float) width / (float) height;
      if (bitmapRatio > 0) {
         width = maxSize;
         height = (int) (width / bitmapRatio);
      } else {
         height = maxSize;
         width = (int) (height * bitmapRatio);
      }
      return Bitmap.createScaledBitmap(bitmap, width, height, true);
   }

   public static String stringFromRes(Context context, int id) {
      return context.getString(id);
   }

   public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
      int w = bitmap.getWidth();
      int h = bitmap.getHeight();

      Matrix mtx = new Matrix();
      mtx.postRotate(degree);

      try {
         Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
         bitmap.recycle();
         return newBitmap;
      } catch (OutOfMemoryError error) {
         return null;
      }
   }

   public static boolean isInt(String s) {
      try {
         Integer.parseInt(s);
      } catch (Exception e) {
         return false;
      }
      return true;
   }

   public static int toInt(String s) {
      try {
         return Integer.parseInt(s);
      } catch (Exception e) {
         return -1;
      }
   }

   public static boolean dateCompare(String unbanDateStr) {
      if (unbanDateStr == null) return true;
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
      try {
         Date unbanDate = sdf.parse(unbanDateStr);
         return new Date().after(unbanDate);
      } catch (ParseException e) {
         Log.d("!", "wrong date format");
         return true;
      }
   }

   public static int getIdByLink(String link) {
      int i = link.lastIndexOf("_");
      if (i == -1) return -1;
      link = link.substring(i + 1);
      if (link.length() < 9 || !Utils.isInt(link)) return -1;
      return Utils.toInt(link);
   }

   public static void removeArrayPrefByPos(SharedPreferences prefs, String itemKey, int pos) {
      String sizeKey = getSizeKey(itemKey);
      int size = prefs.getInt(sizeKey, 0);
      for (int i = pos; i < size; i++) {
         prefs.edit().putString(itemKey + i, prefs.getString(itemKey + (i + 1), null)).commit();
      }
      prefs.edit().remove(itemKey + (size - 1)).putInt(sizeKey, size - 1).commit();
   }

   public static void addArrayPref(SharedPreferences prefs, String itemKey, String item) {
      Log.d(Constants.APP_TAG, "added!");
      String sizeKey = getSizeKey(itemKey);
      int size = prefs.getInt(sizeKey, 0);
      prefs.edit().putString(itemKey + size, item).putInt(sizeKey, size + 1).commit();
   }

   private static String getSizeKey(String itemKey) {
      switch (itemKey) {
         case Constants.SAVED_SEARCH_KEY:
            return Constants.SAVED_SEARCH_SIZE_KEY;
         case Constants.FAVOURITE_AD_KEY:
            return Constants.FAVOURITE_AD_SIZE_KEY;
         default:
            return Constants.SAVED_SEARCH_ADS_SIZE_KEY;
      }
   }

   public static void log(String text, Context ctx) {
      File file = new File(ctx.getExternalFilesDir(null), "service_log.txt");
      if (!file.exists()) {
         Log.d(Constants.APP_TAG, "creating app folder...");
         try {
            if (!file.createNewFile())
               Log.d(Constants.APP_TAG, "log file not created!");
            else
               Log.d(Constants.APP_TAG, "log file created!");
         } catch (IOException e) {
            Log.d(Constants.APP_TAG, "Error writing ");
         }
      } else
         Log.d(Constants.APP_TAG, "folder exists = " + file.getAbsolutePath());
      SimpleDateFormat sdfTime = new SimpleDateFormat(DATE_FORMAT);
      text = "At " + sdfTime.format(new Date()) + " -> " + text;
      try {
         BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
         buf.append(text);
         buf.newLine();
         buf.close();
      } catch (IOException e) {
         Log.d(Constants.APP_TAG, e.toString());
      }
   }

   public static String translit(String s) {
      if (s.equals("Резюме"))
         return "rezume";
      HashMap<Character, String> f = new HashMap<>();
      f.put('а', "a");
      f.put('б', "b");
      f.put('в', "v");
      f.put('г', "g");
      f.put('д', "d");
      f.put('е', "e");
      f.put('ё', "e");
      f.put('ж', "zh");
      f.put('з', "z");
      f.put('и', "i");
      f.put('й', "y");
      f.put('к', "k");
      f.put('л', "l");
      f.put('м', "m");
      f.put('н', "n");
      f.put('о', "o");
      f.put('п', "p");
      f.put('р', "r");
      f.put('с', "s");
      f.put('т', "t");
      f.put('у', "u");
      f.put('ф', "f");
      f.put('х', "h");
      f.put('ц', "ts");
      f.put('ч', "ch");
      f.put('ш', "sh");
      f.put('щ', "sch");
      f.put('ъ', "");
      f.put('ы', "y");
      f.put('ь', "");
      f.put('э', "e");
      f.put('ю', "yu");
      f.put('я', "ya");
      f.put(' ', "_");
      s = s.toLowerCase().replace(",", "");
      String res = "";
      for (int i = 0; i < s.length(); i++) {
         char c = s.charAt(i);
         if (f.containsKey(c))
            res += f.get(c);
         else
            res += c;
      }
      return res;
   }

   public static String getStringElement(String s, String keyword, char l, char r) {
      int i = s.indexOf(keyword);
      if (i == -1)
         return null;
      while (s.charAt(i) != l) i++;
      i++;
      int j = i;
      while (s.charAt(j) != r) j++;
      return s.substring(i, j).replace("&nbsp;", " ");
   }

   public static String getStringElement(String s, char l, char r) {
      return s.substring(s.lastIndexOf(l) + 1, s.indexOf(r));
   }

   /*public static void setAirplaneMode(Context context, boolean enable) {
      if (Build.VERSION.SDK_INT < 17) {
         AirplaneModeSDK8.setAirplaneMode(context, enable);
      } else {
         AirplaneModeSDK17.setAirplaneMode(context, enable);
      }
   }

   public static void systemAppMoverDialog(final Context context) {
      if (!AirplaneModeSDK17.isSystemApp(context)) {
         Log.d(TAG, "not system app");
         AlertDialog.Builder builder = new AlertDialog.Builder(context);
         builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               // User cancelled the dialog
            }
         });
         builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               Pair<Integer, String> ret = moveDataSystemApp("base.apk",
                       "/data/app/bulgakov.arthur.avitoanalytics-*", "/system/priv-app", "root", false);
               if (0 != ret.first) {
                  errorDialog(context, ret.second);
               }
            }
         });
         builder.setMessage("Move app to system?");
         builder.setTitle("Question:");
         // Create the AlertDialog
         AlertDialog dialog = builder.create();
         dialog.show();
      } else {
         final boolean updatedSystemApp = AirplaneModeSDK17.isUpdatedSystemApp(context);
         AlertDialog.Builder builder = new AlertDialog.Builder(context);
         // Add the buttons
         builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               // User cancelled the dialog
            }
         });
         builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               Pair<Integer, String> ret = moveDataSystemApp("base.apk", "/system/priv-app",
                       "/data/app/bulgakov.arthur.avitoanalytics-*", "system", updatedSystemApp);
               if (0 != ret.first) {
                  errorDialog(context, ret.second);
               }
            }
         });
         builder.setMessage("Move app to system?");
         builder.setTitle("Question:");
         // Create the AlertDialog
         AlertDialog dialog = builder.create();
         dialog.show();
      }
   }

   private static void errorDialog(Context context, final String errorMessage) {
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      // Add the buttons
      builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialog, int id) {
            // User cancelled the dialog
         }
      });
      builder.setMessage("Error:\n" + errorMessage);
      builder.setTitle("Error moving app");
      // Create the AlertDialog
      AlertDialog dialog = builder.create();
      dialog.show();
   }

   static boolean isSuperUser() {
      boolean isSuperUser = false;
      Pair<Integer, String> ret;
      // check for /system/xbin/su
      ret = ProcessHelper.runCmd(false, "sh", "-c", "[ -f /system/xbin/su ]");
      isSuperUser = (0 == ret.first);
      if (!isSuperUser) {
         // check for /system/bin/su
         ret = ProcessHelper.runCmd(false, "sh", "-c", "[ -f /system/bin/su ]");
         isSuperUser = (0 == ret.first);
      }
      Log.d(TAG, "isSuperUser = " + isSuperUser);
      return isSuperUser;
   }

   static private Pair<Integer, String> moveDataSystemApp(String app, String from, String to, String owner, boolean updatedSystemApp) {
      int exitCode = -1;
      StringBuilder sb = new StringBuilder();

      // make /system/app writable
      Pair<Integer, String> ret = ProcessHelper.runCmd(true, "su", "-c", "mount -o remount,rw /system");
      sb.append(ret.second);
      if (0 != (exitCode = ret.first)) return new Pair<Integer, String>(exitCode, sb.toString());

      if (!updatedSystemApp) {
         // move MiniStatus to /to/app
         ret = ProcessHelper.runCmd(true, "su", "-c", "cp " + from + "/" + app + " " + to);
         sb.append(ret.second);
         if (0 != (exitCode = ret.first)) return new Pair<Integer, String>(exitCode, sb.toString());

         // set permissions
         ret = ProcessHelper.runCmd(true, "su", "-c", "chmod 644 " + to + "/" + app);
         sb.append(ret.second);
         if (0 != (exitCode = ret.first)) return new Pair<Integer, String>(exitCode, sb.toString());

         // set owner
         ret = ProcessHelper.runCmd(true, "su", "-c", "chown " + owner + ":" + owner + " " + to + "/" + app);
         sb.append(ret.second);
         if (0 != (exitCode = ret.first)) return new Pair<Integer, String>(exitCode, sb.toString());
      }

      // remove MiniStatus from /from/app
      ret = ProcessHelper.runCmd(true, "su", "-c", "rm " + from + "/" + app);
      sb.append(ret.second);
      if (0 != (exitCode = ret.first)) return new Pair<Integer, String>(exitCode, sb.toString());

      // reboot
      ret = ProcessHelper.runCmd(true, "su", "-c", "reboot");
      sb.append(ret.second);
      if (0 != (exitCode = ret.first)) return new Pair<Integer, String>(exitCode, sb.toString());

      return new Pair<Integer, String>(exitCode, sb.toString());
   }*/

   public static void toast(final Activity activity, final String s) {
      activity.runOnUiThread(new Runnable() {
         @Override
         public void run() {
            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
         }
      });
   }

   public static void snack(View view, String s) {
      Snackbar.make(view, s, Snackbar.LENGTH_LONG).setAction("Action", null).show();
   }

   public static String getCurrentDate() {
      Date curDate = new Date();
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
      return format.format(curDate);
   }

   public static AlertDialog.Builder getAlertDialogBuilder(Activity activity, String title, String message, View view) {
      return new AlertDialog.Builder(activity).setTitle(title).setMessage(message).setView(view);
   }

   public static void showProgress(Activity activity) {
      activity.runOnUiThread(new Runnable() {
         @Override
         public void run() {
            MainActivity.pd.show();
         }
      });
   }

   public static void hideProgress(Activity activity) {
      activity.runOnUiThread(new Runnable() {
         @Override
         public void run() {
            MainActivity.pd.hide();
         }
      });
   }

   public static DataPoint[] getAvitoDelPoints(DataPoint[] shopPoints, double k, Random random) {
      DataPoint[] avitoDelPoints = new DataPoint[shopPoints.length];
      for (int i = 0; i < shopPoints.length; i++) {
         double delPrice = shopPoints[i].getY() * (randNear(random, 0.9, 0.04) -
                 k * randNear(random, 0.1, 0.02));
         avitoDelPoints[i] = new DataPoint(shopPoints[i].getX(), delPrice);
      }
      return avitoDelPoints;
   }

   public static ArrayList<Pair<Integer, Integer>> dtw(ArrayList<Integer> a1,
                                                       ArrayList<Integer> a2) {
      int n = a1.size();
      int m = a2.size();
      int d[][] = new int[n][m]; // the euclidian distances matrix
      for (int i = 0; i < n; i++)
         for (int j = 0; j < m; j++)
            d[i][j] = (int) Math.pow(a1.get(i) - a2.get(j), 2);
      // determinate of minimal distance
      int dw[][] = new int[n][n];
      dw[0][0] = d[0][0];
      for (int i = 1; i < n; i++) dw[i][0] = d[i][0] + dw[i - 1][0];
      for (int j = 1; j < m; j++) dw[0][j] = d[0][j] + dw[0][j - 1];
      for (int i = 1; i < n; i++)
         for (int j = 1; j < m; j++)
            if (dw[i - 1][j - 1] <= dw[i - 1][j])
               if (dw[i - 1][j - 1] <= dw[i][j - 1]) dw[i][j] = d[i][j] + dw[i - 1][j - 1];
               else dw[i][j] = d[i][j] + dw[i][j - 1];
            else if (dw[i - 1][j] <= dw[i][j - 1]) dw[i][j] = d[i][j] + dw[i - 1][j];
            else dw[i][j] = d[i][j] + dw[i][j - 1];
      int i = n - 1, j = m - 1;
      // determinate of warping path
      ArrayList<Pair<Integer, Integer>> path = new ArrayList<>();
      do {
         if (i > 0 && j > 0) {
            if (dw[i - 1][j - 1] <= dw[i - 1][j])
               if (dw[i - 1][j - 1] <= dw[i][j - 1]) {
                  i--;
                  j--;
               } else j--;
            else if (dw[i - 1][j] <= dw[i][j - 1]) i--;
            else j--;
         } else if (i == 0) {
            j--;
         } else {
            i--;
         }
         path.add(new Pair<>(j, i));
      } while (i != 0 || j != 0);
      return path;
   }

   public static double randNear(Random random, double v, double rK) {
      return v - random.nextDouble() * 2 * rK + rK;
   }

   public static DataPoint[] toDataPoint(ArrayList<Integer> a) {
      DataPoint[] d = new DataPoint[a.size()];
      for (int i = 0; i < a.size(); i++) {
         d[i] = new DataPoint(i, a.get(i));
      }
      return d;
   }

   public static double dist(int x1, int y1, int x2, int y2) {
      return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
   }

   public static boolean isCategoryLeaf(String category) {
      for (int i = 1; i < Constants.treeParentNum.length; i++) {
         if (Constants.categories[Constants.treeParentNum[i]].equals(category))
            return false;
      }
      return true;
   }

   public static boolean containsIgnoreCase(String src, String what) {
      final int length = what.length();
      if (length == 0)
         return true; // Empty string is contained

      final char firstLo = Character.toLowerCase(what.charAt(0));
      final char firstUp = Character.toUpperCase(what.charAt(0));

      for (int i = src.length() - length; i >= 0; i--) {
         // Quick check before calling the more expensive regionMatches() method:
         final char ch = src.charAt(i);
         if (ch != firstLo && ch != firstUp)
            continue;

         if (src.regionMatches(true, i, what, 0, length)) {
            return true;
         }
      }

      return false;
   }
}
