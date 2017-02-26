package bulgakov.arthur.avitoanalyticsm.ui.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import bulgakov.arthur.avitoanalyticsm.GetAdsTask;
import bulgakov.arthur.avitoanalyticsm.JSInterface;
import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.content.Ad;
import bulgakov.arthur.avitoanalyticsm.content.Search;
import bulgakov.arthur.avitoanalyticsm.utils.Constants;
import bulgakov.arthur.avitoanalyticsm.utils.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentShowGraph extends Fragment {
   public final static int FRAGMENT_NUM = 7;
   public final static String FRAGMENT_TAG = "7";
   public final static int AVITO_RANGE = 30;
   private SharedPreferences prefs;
   public Search search;
   private GraphView graphView;
   private int daysRange;
   public HashMap<Long, ArrayList<Integer>> mapAvitoPoints;
   private DataPoint[] shopPoints;
   private DataPoint[] avitoCurrPoints;
   private DataPoint[] avitoDelPoints;
   private DataPoint[] myPoints;
   private long currTime;
   private double dateD;
   private double dateY;
   private double dateK;
   private String model;
   private String sellDate;
   //   private double shiftK;
   private double averageAvito;
   private double averageShop;
   private Random random;

   public static Fragment newInstance() {
      return new FragmentShowGraph();
   }

   public FragmentShowGraph() {
   }

   @Override
   public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      setHasOptionsMenu(true);
      prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
      random = new Random();
      daysRange = Utils.toInt(prefs.getString(Constants.ET_GRAPH_RANGE_KEY, null));
      dateD = 24 * 60 * 60 * 1000;
      dateY = 365.0 * dateD;
//      shiftK = 0;
      currTime = Calendar.getInstance().getTimeInMillis();
      View rootView = inflater.inflate(R.layout.fragment_show_graph, container, false);
      graphView = (GraphView) rootView.findViewById(R.id.fragment_show_graph_gv);
      if (search != null) {
         if (mapAvitoPoints == null) {
            mapAvitoPoints = new HashMap<>();
            Utils.showProgress(getActivity());
            getAvitoCurrent(1);
         } else {
            if (avitoCurrPoints != null) {
               addAvitoSeries();
            }
         }
      } else {
         Utils.toast(getActivity(), "Ошибка при построении кривой");
      }
      return rootView;
   }

   private void getAvitoCurrent(final int pageNum) {
      final Calendar rangedCalendar = getRangedCalendar(AVITO_RANGE);
      new GetAdsTask(new GetAdsTask.OnAdsReceivedListener() {
         @Override
         public void onAdsReceived(ArrayList<Ad> adsListReceived, String flag) {
            boolean isLimit = false;
            if (adsListReceived == null || adsListReceived.isEmpty()) {
               isLimit = true;
            } else {
               for (Ad ad : adsListReceived) {
                  if (ad.getPriceInt() != -1) {
                     long adTimeMS = getAdCalendar(ad).getTimeInMillis();
//                     Log.d(Constants.APP_TAG, "" + adTimeMS);
                     if (adTimeMS < rangedCalendar.getTimeInMillis()) {
                        isLimit = true;
                        break;
                     }
                     ArrayList<Integer> priceList;
                     if (!mapAvitoPoints.containsKey(adTimeMS)) {
                        priceList = new ArrayList<>();
                     } else {
                        priceList = mapAvitoPoints.get(adTimeMS);
                     }
                     priceList.add(ad.getPriceInt());
                     mapAvitoPoints.put(adTimeMS, priceList);
                  }
               }
               if (!isLimit) {
                  getAvitoCurrent(pageNum + 1);
               }
            }
            if (isLimit) {
               avitoCurrPoints = getAvitoCurrPoints();
               new Handler(Looper.getMainLooper()).post(new Runnable() {
                  @Override
                  public void run() {
                     addAvitoSeries();
                  }
               });
            }
         }
      }, pageNum, search, prefs).execute((Void) null);
   }

   private void addAvitoSeries() {
      Log.d(Constants.APP_TAG, "" + avitoCurrPoints.length);
      if (avitoCurrPoints.length > 0) {
         LineGraphSeries<DataPoint> avitoSeries = new LineGraphSeries<>(avitoCurrPoints);
         avitoSeries.setColor(ContextCompat.getColor(getActivity(), R.color.graph_avito));
         graphView.addSeries(avitoSeries);
         setGraphPoints(avitoCurrPoints);
      } else {
         Utils.toast(getActivity(), "Ошибка при загрузке графика либо ни одного объявления" +
                 "не найдено по выбранному поиску");
      }
      Utils.hideProgress(getActivity());
   }

   private void setGraphPoints(DataPoint[] points) {
      graphView.getViewport().setMinX(points[0].getX());
      graphView.getViewport().setMaxX(points[points.length - 1].getX());
      graphView.getGridLabelRenderer().setLabelFormatter(
              new DateAsXAxisLabelFormatter(getActivity()));
      graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
      graphView.getViewport().setXAxisBoundsManual(true);
      graphView.getViewport().setScalable(true);
   }

   private Calendar getAdCalendar(Ad ad) {
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      String date = ad.date.split(",", -1)[0];
      String hm = ad.date.split(",", -1)[1];
      String hour = hm.substring(1, 3);
      String min = hm.substring(4, 6);
      calendar.set(Calendar.HOUR_OF_DAY, Utils.toInt(hour));
      calendar.set(Calendar.MINUTE, Utils.toInt(min));
      if (ad.date.contains("Вчера")) {
         calendar.add(Calendar.DAY_OF_MONTH, -1);
      } else if (!ad.date.contains("Сегодня")) {
         String day = date.split("\\s+")[0];
         String month = date.split("\\s+")[1];
         calendar.set(Calendar.MONTH, getMonthNumber(month));
         calendar.set(Calendar.DAY_OF_MONTH, Utils.toInt(day));
         calendar.set(Calendar.DAY_OF_MONTH, Utils.toInt(day));
      }
      return calendar;
   }

   private int getMonthNumber(String month) {
      return getMonthNumber1(month) - 1;
   }

   private int getMonthNumber1(String month) {
      switch (month) {
         case "января":
            return 1;
         case "февраля":
            return 2;
         case "марта":
            return 3;
         case "апреля":
            return 4;
         case "мая":
            return 5;
         case "июня":
            return 6;
         case "июля":
            return 7;
         case "августа":
            return 8;
         case "сентября":
            return 9;
         case "октября":
            return 10;
         case "ноября":
            return 11;
         case "декабря":
            return 12;
         default:
            return 0;
      }
   }

   private DataPoint[] getMaPoints(DataPoint[] points) {
      double maSum = 0;
      int maPNum = Utils.toInt(prefs.getString(Constants.ET_GRAPH_MA_KEY, null));
      for (int i = 0; i < maPNum; i++) {
         maSum += points[i].getY();
      }
      DataPoint[] maPoints = new DataPoint[0];
      if (maPNum < points.length) {
         maPoints = new DataPoint[points.length];
         for (int i = 0; i < maPNum; i++) {
            maPoints[i] = new DataPoint(points[i].getX(), maSum / maPNum);
         }
         for (int i = maPNum; i < points.length; i++) {
            maSum += points[i].getY() - points[i - maPNum].getY();
            maPoints[i] = new DataPoint(points[i].getX(), maSum / maPNum);
         }
      }
      return maPoints;
   }

   private DataPoint[] getAvitoCurrPoints() {
      if (mapAvitoPoints != null) {
         ArrayList<Long> listX = new ArrayList<>(mapAvitoPoints.keySet());
         Collections.sort(listX);
         ArrayList<DataPoint> pointList = new ArrayList<>();
         for (long x : listX) {
            ArrayList<Integer> yList = mapAvitoPoints.get(x);
            int ySum = 0;
            for (Integer y : yList) {
               ySum += y;
            }
            pointList.add(new DataPoint(x, ySum / yList.size()));
         }
         return pointList.toArray(new DataPoint[pointList.size()]);
      } else {
         return new DataPoint[0];
      }
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//      DrawerLayout drawerLayout = ((MainActivity) getActivity()).getDrawerLayout();
//      if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START))
//         return;
      super.onCreateOptionsMenu(menu, inflater);
      inflater.inflate(R.menu.fragment2_menu, menu);
   }

   @Override
   public void onPrepareOptionsMenu(Menu menu) {
      menu.findItem(R.id.menu_activity_main_action_settings).setVisible(false);
      super.onPrepareOptionsMenu(menu);
   }

   private void actionShopPrices(final boolean del) {
      final View dialogStateView = getActivity().getLayoutInflater().inflate(
              R.layout.dialog_pick_model, null);
      final AlertDialog dialogModel = Utils.getAlertDialogBuilder(getActivity(),
              "Точное название модели:", null, dialogStateView).create();
      final EditText etModel = (EditText) dialogStateView.findViewById(R.id.dialog_pick_model_et);
      dialogModel.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            dialogModel.dismiss();
         }
      });
      dialogModel.setOnDismissListener(new DialogInterface.OnDismissListener() {
         @Override
         public void onDismiss(DialogInterface dialog) {
            model = etModel.getText().toString();
            if (!model.isEmpty()) {
               Utils.showProgress(getActivity());
               getShopPrices(del);
            } else {
               Utils.toast(getActivity(), "Ошибка, Ваша модель не была найдена");
            }
         }
      });
      dialogModel.show();
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.fragment2_action_shop_prices:
            actionShopPrices(false);
            return true;
         case R.id.fragment2_action_ma_del:
            if (avitoDelPoints != null) {
               LineGraphSeries<DataPoint> maSeries = new LineGraphSeries<>(getMaPoints(avitoDelPoints));
               maSeries.setColor(ContextCompat.getColor(getActivity(), R.color.graph_ma_del));
               graphView.addSeries(maSeries);
            } else {
               Utils.toast(getActivity(), "Ошибка при построении средней");
            }
            return true;
         case R.id.fragment2_action_ma_curr:
            if (avitoCurrPoints != null) {
               Log.d(Constants.APP_TAG, "" + avitoCurrPoints.length);
               LineGraphSeries<DataPoint> maSeries = new LineGraphSeries<>(getMaPoints
                       (avitoCurrPoints));
               maSeries.setColor(ContextCompat.getColor(getActivity(), R.color.graph_ma_curr));
               graphView.addSeries(maSeries);
            } else {
               Utils.toast(getActivity(), "Ошибка при построении средней");
            }
            return true;
         case R.id.fragment2_action_avito_del:
            if (avitoCurrPoints != null && avitoCurrPoints.length > 0) {
//               if (sellDate == null) {
//                  getSellDate();
//               } else {
//               Utils.showProgress(getActivity());
//               new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                  @Override
//                  public void run() {
               actionShopPrices(true);
//                  }
//               }, 20000);
//               }
            } else {
               Utils.toast(getActivity(), "Ошибка при построении кривой");
            }
            return true;
         case R.id.fragment2_action_avito_my:
            if (avitoCurrPoints != null && avitoCurrPoints.length > 0 && shopPoints != null
                    && shopPoints.length > 0) {
               myPoints = new DataPoint[shopPoints.length];
               for (int i = 0; i < shopPoints.length; i++) {
                  double delPrice = shopPoints[i].getY() * (0.9 - (currTime - dateK) / dateY * 0.1);
                  myPoints[i] = new DataPoint(shopPoints[i].getX(), delPrice);
               }
               LineGraphSeries<DataPoint> delSeries = new LineGraphSeries<>(myPoints);
               delSeries.setColor(ContextCompat.getColor(getActivity(), R.color.graph_my));
               graphView.addSeries(delSeries);
            } else {
               Utils.toast(getActivity(), "Ошибка при построении кривой");
            }
            return true;
         /*case R.id.fragment2_action_save_stats:
            if (avitoDelPoints != null && avitoDelPoints.length > 0 && shopPoints != null
                    && shopPoints.length > 0 && myPoints != null && myPoints.length > 0) {
               try {
                  File root = new File(Utils.getAppDir(getActivity()));
                  root.mkdirs();
                  File gpxfile = new File(root, model + ".txt");
                  gpxfile.createNewFile();
                  FileWriter writer = new FileWriter(gpxfile);
                  String sBody = "";
                  DataPoint[] maAvitoDelPoints = getMaPoints(avitoDelPoints);
                  for (int i = 0; i < shopPoints.length; i++) {
                     String pShop = "" + (int) shopPoints[i].getY();
                     String pAvito = "" + (int) maAvitoDelPoints[i].getY();
                     String pMy = "" + (int) myPoints[i].getY();
                     if (i == 0) {
                        sBody += "Psh";
                        int len = pShop.length();
                        while (sBody.length() < len) sBody += "=";
                        sBody += "  Pav";
                        len += pAvito.length() + 2;
                        while (sBody.length() < len) sBody += "=";
                        sBody += "  Pmy";
                        len += pMy.length() + 2;
                        while (sBody.length() < len) sBody += "=";
                        sBody += "  Pav-Pmy\n";
                     }
                     sBody += pShop + "  " + pAvito + "  " + pMy + "  " +
                             (Utils.toInt(pAvito) - Utils.toInt(pMy)) + "\n";
                  }
                  writer.append(sBody);
                  writer.flush();
                  writer.close();
                  Utils.toast(getActivity(), "Cохранено успешно");
               } catch (IOException e) {
                  Log.d(Constants.APP_TAG, "File write failed: " + e.toString());
                  Utils.toast(getActivity(), "Ошибка");
               }
            } else {
               Utils.toast(getActivity(), "Ошибка при построении кривой");
            }
            return true;*/
         case R.id.fragment2_action_save_graph:
            final Bitmap bitmap;
            graphView.setDrawingCacheEnabled(true);
            graphView.setDrawingCacheBackgroundColor(Color.WHITE);
            bitmap = Bitmap.createBitmap(graphView.getDrawingCache());
            Utils.saveBitmap(bitmap, Utils.getAppDir(getActivity()), model);
            graphView.setDrawingCacheEnabled(false);
            Utils.toast(getActivity(), "Сохранено");
            return true;
         case R.id.menu_fragment_preferences_dtw:
            if (avitoDelPoints != null && avitoDelPoints.length > 0 && shopPoints != null
                    && shopPoints.length > 0) {
            /*final File files = new File(Utils.getAppDir(getActivity()));
            for (int filei = 0; filei < files.listFiles().length; filei++) {
               File f = files.listFiles()[filei];
               Log.d(Constants.APP_TAG, f.getName());
               try {
                  BufferedReader br = new BufferedReader(new FileReader(f));
                  String line;
                  ArrayList<Integer> pShop = new ArrayList<>();
                  ArrayList<Integer> pMAvito = new ArrayList<>();
                  ArrayList<Integer> pAvito = new ArrayList<>();
                  ArrayList<Integer> pMy = new ArrayList<>();
                  br.readLine();
                  while ((line = br.readLine()) != null) {
                     String[] spl = line.split(" +");
                     pShop.add(Utils.toInt(spl[0]));
                     pMAvito.add(Utils.toInt(spl[1]));
                     pMy.add(Utils.toInt(spl[2]));
                     pAvito.add((int) (Utils.toInt(spl[1]) * Utils.randNear(random, 1.0, 0.03)));
                  }
                  int shift = 20 + random.nextInt(10);
                  for (int j = 0; j < shift; j++) {
                     pShop.remove(pShop.size() - 1 - j);
                     pMAvito.remove(0);
                  }
                  ArrayList<Pair<Integer, Integer>> pDtw = Utils.dtw(pShop, pMAvito);
                  String tDir = Utils.getAppDir(getActivity()) + "/t";
                  File root = new File(tDir);
                  root.mkdirs();
                  File gpxfile = new File(root, f.getName());
                  gpxfile.createNewFile();
                  FileWriter writer = new FileWriter(gpxfile);
                  String sBody = "";
                  double dtwsum = 0, evksum = 0;
                  for (int j = 0; j < Math.min(pShop.size(), pDtw.size()); j++) {
                     String sh = "" + pShop.get(j);
                     String mav = "" + pMAvito.get(j);
                     String av = "" + pAvito.get(j);
                     String my = "" + pMy.get(j);
                     int ish = pDtw.get(j).first;
                     int iav = pDtw.get(j).second;
                     double dtwdist = Utils.dist(ish, pShop.get(ish), iav, pMAvito.get(iav));
                     dtwsum += dtwdist;
                     double evkdist = pShop.get(j) - pAvito.get(j);
                     evksum += evkdist;
                     String dtw = "" + (int) dtwdist;
                     String evk = "" + (int) evkdist;
                     int dift = (int) Math.abs(((double) pAvito.get(j) / pMy.get(j) - 1) * 10000);
                     String dif = "" + dift / 100 + "." + dift % 100;
                     if (j == 0) {
                        sBody += "Psh";
                        int len = sh.length();
                        while (sBody.length() < len) sBody += "=";
                        sBody += "  Pav";
                        len += av.length() + 2;
                        while (sBody.length() < len) sBody += "=";
                        sBody += "  Pmav";
                        len += mav.length() + 2;
                        while (sBody.length() < len) sBody += "=";
                        sBody += "  Pdtw";
                        len += dtw.length() + 2;
                        while (sBody.length() < len) sBody += "=";
                        sBody += "  Pevk";
                        len += dtw.length() + 2;
                        while (sBody.length() < len) sBody += "=";
                        sBody += "  Pmy";
                        len += my.length() + 2;
                        while (sBody.length() < len) sBody += "=";
                        sBody += "  Pdif%";
                        sBody += "\n";
                     }
                     sBody += sh + "  " + av + "  " + mav + "  " + dtw + "  " + evk +
                             "  " + my + "  " + dif + "\n";
                  }
                  sBody += "Dtw sum = " + dtwsum + "\n" + "Evk sum = " + evksum;
                  writer.append(sBody);
                  writer.flush();
                  writer.close();
*/
               ArrayList<Integer> pShop = new ArrayList<>();
               ArrayList<Integer> pMAvito = new ArrayList<>();
               DataPoint[] avitoMaPoints = getMaPoints(avitoDelPoints);
               for (int i = 0; i < shopPoints.length; i++) {
                  pShop.add((int) shopPoints[i].getY());
                  pMAvito.add((int) avitoMaPoints[i].getY());
               }
               ArrayList<Pair<Integer, Integer>> pDtw = Utils.dtw(pShop, pMAvito);
//            LineGraphSeries<DataPoint> ss = new LineGraphSeries<>(shopPoints
//                          /*Utils.toDataPoint(pShop)*/);
//            graphView.addSeries(ss);
//            LineGraphSeries<DataPoint> as = new LineGraphSeries<>(getMaPoints(avitoDelPoints)
//                          /*Utils.toDataPoint(pMAvito)*/);
//            graphView.addSeries(as);
//            as.setColor(ContextCompat.getColor(getActivity(), R.color.graph_dtw));
               Log.d(Constants.APP_TAG, "dtw size=" + pDtw.size());
               for (int j = 0; j < pDtw.size(); j += 5) {
                  int ish = pDtw.get(j).first;
                  int iav = pDtw.get(j).second;
                  DataPoint[] td = new DataPoint[2];
                  td[0] = shopPoints[ish];
                  td[1] = avitoMaPoints[iav];
                  LineGraphSeries<DataPoint> ts = new LineGraphSeries<>(td);
                  ts.setColor(ContextCompat.getColor(getActivity(), R.color.graph_dtw));
                  graphView.addSeries(ts);
               }
//            final Bitmap bitmap2;
//            graphView.setDrawingCacheEnabled(true);
//            graphView.setDrawingCacheBackgroundColor(Color.WHITE);
//            bitmap2 = Bitmap.createBitmap(graphView.getDrawingCache());
//            Utils.saveBitmap(bitmap2, tDir, f.getName());
//            graphView.setDrawingCacheEnabled(false);
//            graphView.removeAllSeries();
//               } catch (Exception e) {
//                  Log.d(Constants.APP_TAG, e.toString());
//                  Utils.toast(getActivity(), "Ошибка");
//               }
//            }
            } else {
               Utils.toast(getActivity(), "Ошибка при построении кривой");
            }
            return true;
      }
      return super.onOptionsItemSelected(item);
   }

   private double getAverage(DataPoint[] points, int num) {
      int res = 0;
      for (int i = points.length - num; i < points.length; i++) {
         res += points[i].getY();
      }
      res /= num;
      return res;
   }

   private void getShopPrices(final boolean del) {
//      AlertDialog.Builder wvDialog = new AlertDialog.Builder(getActivity());
      final WebView webView = new WebView(getActivity());
      new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
         @Override
         public void run() {
            if (shopPoints == null) {
               webView.loadUrl("about:blank");
               Utils.hideProgress(getActivity());
               Utils.toast(getActivity(), "Ошибка, проверьте интернет-соединение");
               if (del) {
                  actionShopPrices(del);
               }
            }
         }
      }, 30000);
      final String urlSearch = "http://priceanalytic.com/search/?q=" + model.replace(' ', '+');
      webView.getSettings().setDomStorageEnabled(true);
      webView.getSettings().setJavaScriptEnabled(true);
      webView.getSettings().setLoadsImagesAutomatically(false);
      webView.addJavascriptInterface(new JSInterface(new JSInterface.OnPASearchListener() {
         @Override
         public void onPASearch(final String foundUrl) {
            Log.d(Constants.APP_TAG, "found->" + foundUrl);
            if (!foundUrl.isEmpty()) {
               new Handler(Looper.getMainLooper()).post(new Runnable() {
                  @Override
                  public void run() {
                     webView.loadUrl(foundUrl);
                  }
               });
            } else {
               Utils.toast(getActivity(), "Ошибка, Ваша модель не была найдена");
               Utils.hideProgress(getActivity());
            }
         }
      }, new JSInterface.OnGraphListener() {
         @Override
         public void onGraph(final double[] x, final double[] y) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
               @Override
               public void run() {
                  shopPoints = new DataPoint[x.length];
                  for (int i = 0; i < x.length; i++) {
                     shopPoints[i] = new DataPoint(x[i], y[i]);
                  }
                  if (!del) {
                     LineGraphSeries<DataPoint> shopSeries = new LineGraphSeries<>(shopPoints);
                     shopSeries.setColor(Color.BLUE);
                     graphView.addSeries(shopSeries);
                  }

                  averageShop = getAverage(shopPoints, AVITO_RANGE);
                  averageAvito = getAverage(avitoCurrPoints, avitoCurrPoints.length);
                  dateK = currTime - (0.9 - averageAvito / averageShop) / 0.1 * dateY;
//                  shiftK = (currTime - dateK) / dateY * dateD * 1;
                  setGraphPoints(shopPoints);
                  if (del) {
                     avitoDelPoints = Utils.getAvitoDelPoints(shopPoints, (currTime - dateK) / dateY,
                             random);
                     LineGraphSeries<DataPoint> delSeries = new LineGraphSeries<>(avitoDelPoints);
                     delSeries.setColor(ContextCompat.getColor(getActivity(), R.color.graph_del));
                     graphView.addSeries(delSeries);
                  }
                  Utils.hideProgress(getActivity());
               }
            });
         }
      }), "JSInterface");
      webView.setWebViewClient(new WebViewClient() {
         @Override
         public WebResourceResponse shouldInterceptRequest(WebView wv, String url) {
//            Log.d(Constants.APP_TAG, "intercepted: " + url);
            return super.shouldInterceptRequest(wv, url);
         }

         @Override
         public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.d(Constants.APP_TAG, "error: " + errorCode + " " + description + " " + failingUrl);
         }

         @Override
         public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(Constants.APP_TAG, "link loaded=" + url);
            if (url.equals(urlSearch)) {
               webView.loadUrl("javascript:" +
                       "var t = document.getElementsByClassName('table-condensed')[0];" +
                       "var td = null;" +
                       "var s = null;" +
                       "var c = 0;" +
                       "var f = 0;" +
                       "for (var i = 1; i < t.rows.length; i++) {" +
                       "   td = t.rows[i].cells[1].getElementsByTagName('a')[0];" +
                       "   s = td.innerHTML.toLowerCase().replace(/\\s+/g, '');" +
                       "   c = s.localeCompare('" + model.toLowerCase().replace(" ", "") + "');" +
                       "   if (c == 0) {" +
                       "      window.JSInterface.onPASearch(td.href);" +
                       "      f = 1;" +
                       "      break;" +
                       "   }" +
                       "}" +
                       "if (f == 0) {" +
                       "   window.JSInterface.onPASearch('');" +
                       "}");
            } else {
               final String rDate = getJSDate(Calendar.getInstance());
               final String lDate = getJSDate(getRangedCalendar(daysRange));
               webView.loadUrl("javascript:" +
                       "var chart = Highcharts.charts[0];" +
                       "chart.xAxis[0].setExtremes(" + lDate + ", " + rDate + ");" +
                       "var p = chart.series[2].points;" +
                       "var x = new Array(p.length);" +
                       "var y = new Array(p.length);" +
                       "for (var i = 0; i < p.length; i++) {" +
                       "   x[i] = p[i].x;" +
                       "   y[i] = p[i].y;" +
                       "}" +
                       "window.JSInterface.onGraph(x, y);");
            }
         }
      });
      webView.loadUrl(urlSearch);
//      wvDialog.setView(webView);
//      wvDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
//         @Override
//         public void onClick(DialogInterface dialog, int id) {
//            dialog.dismiss();
//         }
//      });
//      wvDialog.show();
   }

   private void getSellDate() {
      AlertDialog.Builder wvDialog = new AlertDialog.Builder(getActivity());
      final WebView webView = new WebView(getActivity());
      new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
         @Override
         public void run() {
            if (sellDate == null) {
               webView.loadUrl("about:blank");
               Utils.hideProgress(getActivity());
               Utils.toast(getActivity(), "Ошибка, проверьте интернет-соединение");
            }
         }
      }, 8000);
      final String urlSearch = "http://m.market.yandex.ru/search.xml?text=" +
              model.replace(" ", "%20");
      webView.getSettings().setDomStorageEnabled(true);
      webView.getSettings().setJavaScriptEnabled(true);
      webView.getSettings().setLoadsImagesAutomatically(false);
      webView.addJavascriptInterface(new JSInterface(new JSInterface.OnYMSearchListener() {
         @Override
         public void onYMSearch(final String foundUrl) {
            Log.d(Constants.APP_TAG, "found->" + foundUrl);
            if (!foundUrl.isEmpty()) {
               new Handler(Looper.getMainLooper()).post(new Runnable() {
                  @Override
                  public void run() {
                     webView.loadUrl(foundUrl);
                  }
               });
            } else {
               Utils.toast(getActivity(), "Ошибка, Ваша модель не была найдена");
               Utils.hideProgress(getActivity());
            }
         }
      }, new JSInterface.OnSellDateListener() {
         @Override
         public void onSellDate(String sellDate) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
               @Override
               public void run() {

               }
            });
         }
      }), "JSInterface");
      webView.setWebViewClient(new WebViewClient() {
         @Override
         public WebResourceResponse shouldInterceptRequest(WebView wv, String url) {
//            Log.d(Constants.APP_TAG, "intercepted: " + url);
            return super.shouldInterceptRequest(wv, url);
         }

         @Override
         public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.d(Constants.APP_TAG, "error: " + errorCode + " " + description + " " + failingUrl);
         }

         @Override
         public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(Constants.APP_TAG, "link loaded=" + url);
            if (url.equals(urlSearch)) {
               webView.loadUrl("javascript:" +
                       "var t = document.getElementsByClassName('b-minicards')[0];" +
                       "var td = null;" +
                       "var s = null;" +
                       "var c = 0;" +
                       "var f = 0;" +
                       "for (var i = 0; i < t.rows.length; i++) {" +
                       "   td = t.rows[i].cells[0].getElementsByTagName('a')[0];" +
                       "   s = td.innerHTML.toLowerCase().replace(/\\s+/g, '');" +
                       "   c = s.localeCompare('" + model.toLowerCase().replace(" ", "") + "');" +
                       "   if (f == 0) {" +
                       "      window.JSInterface.onPASearch(s);" +
                       "      f = 1;" +
                       "      break;" +
                       "   }" +
                       "}" +
                       "if (f == 0) {" +
                       "   window.JSInterface.onYMSearch('');" +
                       "}");
            } else {
//               final String rDate = getJSDate(Calendar.getInstance());
//               final String lDate = getJSDate(getRangedCalendar(daysRange));
//               webView.loadUrl("javascript:" +
//                       "var chart = Highcharts.charts[0];" +
//                       "chart.xAxis[0].setExtremes(" + lDate + ", " + rDate + ");" +
//                       "var p = chart.series[2].points;" +
//                       "var x = new Array(p.length);" +
//                       "var y = new Array(p.length);" +
//                       "for (var i = 0; i < p.length; i++) {" +
//                       "   x[i] = p[i].x;" +
//                       "   y[i] = p[i].y;" +
//                       "}" +
//                       "window.JSInterface.onGraph(x, y);");
            }
         }
      });
      webView.loadUrl(urlSearch);
      wvDialog.setView(webView);
      wvDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int id) {
            dialog.dismiss();
         }
      });
      wvDialog.show();
   }

   private Calendar getRangedCalendar(int range) {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.DAY_OF_MONTH, -range);
      return calendar;
   }

   private String getJSDate(Calendar calendar) {
      return "Date.UTC(" + calendar.get(Calendar.YEAR) + "," + calendar.get(Calendar.MONTH)
              + "," + calendar.get(Calendar.DAY_OF_MONTH) + ")";
   }
}