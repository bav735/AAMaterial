package bulgakov.arthur.avitoanalyticsm;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import bulgakov.arthur.avitoanalyticsm.content.Ad;
import bulgakov.arthur.avitoanalyticsm.content.Search;
import bulgakov.arthur.avitoanalyticsm.utils.Constants;
import bulgakov.arthur.avitoanalyticsm.utils.Utils;

/**
 * Created by A on 09.06.2016.
 */
public class GetAdsTask extends AsyncTask<Void, String, String> {
   private OnAdsReceivedListener listener;
   private int pageNum;
   private Search search;
   private ArrayList<Ad> adsList;
   private String flag;
   private SharedPreferences prefs;

   public GetAdsTask(OnAdsReceivedListener listener, int pageNum, Search search,
                     SharedPreferences prefs) {
      this.pageNum = pageNum;
      this.listener = listener;
      this.search = search;
      this.prefs = prefs;
   }

   @Override
   protected String doInBackground(Void... arg0) {
      Log.d(Constants.APP_TAG, "started");
      flag = Constants.GET_ADS_ERROR;
      String url = Constants.AVITO_URL + "/" + Utils.translit(search.location);
      if (!search.category.isEmpty()) {
         url += "/" + Utils.translit(search.category);
      }
      if (!search.kind.isEmpty()) {
         url += "/" + Utils.translit(search.kind);
      }
      url += "?p=" + pageNum + "&bt=" + search.searchInTitleKey + "&user=" + search.sellerTypeKey;
      if (!search.query.isEmpty())
         url += "&q=" + search.query.replace(' ', '+');
      if (search.priceTo != -1)
         url += "&pmax=" + search.priceTo;
      if (search.priceFrom != -1)
         url += "&pmin=" + search.priceFrom;
      Document doc;
      Log.d(Constants.APP_TAG, url);
      try {
         doc = Jsoup.connect(url)
                 .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36"
                         + " (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                 .timeout(Constants.CONNECTION_TIMEOUT_MSEC)
                 .get();
         Elements allElements = doc.select(".b-item.js-catalog-item-enum");
         adsList = new ArrayList<>();
         int tpos = 0;
         for (Element e : allElements) {
            Ad ad = new Ad().fromDocumentElement(e);
            if (ad.isCorrect() && (!prefs.getBoolean("check_box_preference_adverts", true) ||
                    ad.isNotAdvert() && e.className().equals("b-item js-catalog-item-enum"))) {
               ad.pos = tpos++;
               adsList.add(ad);
            }
         }
         flag = Constants.GET_ADS_OK;
      } catch (Exception e) {
         Log.d(Constants.APP_TAG, e.toString() + " from getAds ");
         if (e.toString().toLowerCase().contains("refused")) {
            ServiceSearchTracking.sendNotification(null, e.toString());
            flag = Constants.GET_ADS_REFUSED;
         }
         if (e.toString().contains("Status=404")) {
            flag = Constants.GET_ADS_404;
         }
      }
      return null;
   }

   @Override
   protected void onPostExecute(String result) {
//         super.onPostExecute(result);
      if (search.exchangeKey.equals("1")) {
         if (adsList != null && !adsList.isEmpty()) {
            getExchangeAdsList(0, new ArrayList<Ad>());
         } else {
            listener.onAdsReceived(adsList, flag);
         }
      } else {
         listener.onAdsReceived(adsList, flag);
      }
   }

   private void getExchangeAdsList(final int adPos, final ArrayList<Ad> exchangeAdsList) {
      Log.d(Constants.APP_TAG, "ad pos = " + adPos + " from " + adsList.size());
      if (adPos == adsList.size()) {
         flag = Constants.GET_ADS_OK;
         if (exchangeAdsList.isEmpty()) {
            flag = Constants.GET_ADS_EMPTY;
         }
         listener.onAdsReceived(exchangeAdsList, flag);
      } else {
         Log.d(Constants.APP_TAG, "geeting exchange");
         new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               new GetAdTask(new GetAdTask.OnAdReceivedListener() {
                  @Override
                  public void onAdReceived(Ad adReceived) {
                     Log.d(Constants.APP_TAG, "received exchange");
                     if (adReceived.isExchange()) {
                        Log.d(Constants.APP_TAG, "it;s exchange!");
                        exchangeAdsList.add(adReceived);
                     }
                     getExchangeAdsList(adPos + 1, exchangeAdsList);
                  }
               }, adsList.get(adPos)).execute((Void) null);
            }
         }, 200);
      }
   }

   @Override
   protected void onProgressUpdate(String... progress) {
      super.onProgressUpdate(progress);
   }

   @Override
   protected void onPreExecute() {
      super.onPreExecute();
   }

   public interface OnAdsReceivedListener {
      void onAdsReceived(ArrayList<Ad> adsListReceived, String flag);
   }
}