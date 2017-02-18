package bulgakov.arthur.avitoanalyticsm;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import bulgakov.arthur.avitoanalyticsm.content.Ad;
import bulgakov.arthur.avitoanalyticsm.utils.Constants;

/**
 * Created by A on 09.06.2016.
 */
public class GetAdTask extends AsyncTask<Void, String, String> {
   OnAdReceivedListener listener;
   Ad ad;

   public GetAdTask(OnAdReceivedListener listener, Ad ad) {
      this.listener = listener;
      this.ad = ad;
   }

   @Override
   protected String doInBackground(Void... arg0) {
      Document doc;
      try {
         doc = Jsoup.connect(ad.url)
                 .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36"
                         + " (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                 .timeout(Constants.CONNECTION_TIMEOUT_MSEC)
                 .get();
         Element element = doc.select(".description-preview-wrapper").first();
         ad.description = element.text();
      } catch (Exception e) {
         Log.d(Constants.APP_TAG, e.toString() + " from getAd ");
         if (e.toString().toLowerCase().contains("refused")) {
            ServiceSearchTracking.sendNotification(null, e.toString());
         }
      }
      return null;
   }

   @Override
   protected void onPostExecute(String result) {
//         super.onPostExecute(result);
      listener.onAdReceived(ad);
   }

   @Override
   protected void onProgressUpdate(String... progress) {
      super.onProgressUpdate(progress);
   }

   @Override
   protected void onPreExecute() {
      super.onPreExecute();
   }

   public interface OnAdReceivedListener {
      void onAdReceived(Ad adReceived);
   }
}