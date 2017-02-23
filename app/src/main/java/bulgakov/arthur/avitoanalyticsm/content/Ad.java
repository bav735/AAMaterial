package bulgakov.arthur.avitoanalyticsm.content;

import android.content.Context;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import java.util.Calendar;

import bulgakov.arthur.avitoanalyticsm.JSInterface;
import bulgakov.arthur.avitoanalyticsm.utils.Constants;
import bulgakov.arthur.avitoanalyticsm.utils.Utils;

/**
 * Created by A on 30.11.2015.
 */
public class Ad {
   //   public String number = null;
   public String title;
   public String description = "";
   public String price = "Цена: ";
   public String url;
   public String date;
   public String phoneNumber;
   public int pos;

   public Ad() {
   }

   public Ad fromJson(String jsonString) {
      try {
         JSONObject jsonObject = new JSONObject(jsonString);
         date = jsonObject.getString("date");
         title = jsonObject.getString("title");
         description = jsonObject.getString("description");
         price = jsonObject.getString("price");
         url = jsonObject.getString("url");
         return this;
      } catch (JSONException e) {
         Log.d(Constants.APP_TAG, "json error from ad");
         return null;
      }
   }

   public Ad fromDocumentElement(Element element) {
      url = Constants.AVITO_URL + element.select("a[href]").attr("href");
      title = element.select(".header-text").text();
      price = element.select(".item-price").text();
      if (price.isEmpty())
         price = "не указана";
      date = element.select(".info-date").text();
      return this;
   }

   public JSONObject toJson() {
      try {
         JSONObject jsonObject = new JSONObject();
         jsonObject.put("title", title);
         jsonObject.put("description", description);
         jsonObject.put("price", price);
         jsonObject.put("url", url);
         jsonObject.put("date", date);
         return jsonObject;
      } catch (JSONException e) {
         Log.d(Constants.APP_TAG, "tojson error ad");
         return null;
      }
   }

   public boolean isNotAdvert() {
      return !(title).toLowerCase().contains("в наличии") &&
              !(title).toLowerCase().contains("новые") &&
              !(title).toLowerCase().contains("доставка") &&
              !(title).toLowerCase().contains("рассрочка") &&
              !(title).toLowerCase().contains("магазин");
   }

   public int getPriceInt() {
//      Log.d(ActivityMain.APP_TAG, price);
      String priceString = "";
      for (int i = 0; i < price.length(); i++) {
         char c = price.charAt(i);
         if (c >= '0' && c <= '9')
            priceString += c;
      }
      return Utils.toInt(priceString);
   }

   public boolean titleMatches(Search search) {
      if (search.searchInTitleKey.equals("0"))
         return true;
      //split to words
      String[] queryWords = search.query.toLowerCase().split("\\s+");
      String[] titleWords = title.toLowerCase().split("\\s+");
      for (String qWord : queryWords) {
         boolean hasWord = false;
         for (String tWord : titleWords) {
            if (tWord.equals(qWord))
               hasWord = true;
         }
         if (!hasWord)
            return false;
      }
      return true;
   }

   public boolean isCorrect() {
      return title != null &&
              price != null &&
//              Utils.toInt(getId()) != -1 &&
              url != null &&
              date != null;
   }

   public String getId() {
      return date;
//      return url.substring(url.lastIndexOf("_") + 1);
   }

   public boolean isPresent() {
      if (!date.contains("Сегодня"))
         return false;
      int adH = Utils.toInt(date.substring(date.length() - 5, date.length() - 3));
      int adM = Utils.toInt(date.substring(date.length() - 2, date.length()));
      int currH = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
      int currM = Calendar.getInstance().get(Calendar.MINUTE);
//      Log.d(ActivityMain.APP_TAG, "ad->" + adH + ":" + adM + ", curr->" + currH + ":" + currM);
      return (currH - adH) * 60 + currM - adM <= Constants.AD_TIMEOUT_MIN;
   }

   public boolean isExchange() {
      String text = (title + description).toLowerCase();
      Log.d(Constants.APP_TAG, "text=" + text);
      if (!text.contains("обмен")) {
         return false;
      }
      for (String s : Constants.NOT_EXCHANGE_KEYWORDS) {
         if (Utils.containsIgnoreCase(text, s)) {
            return false;
         }
      }
      int i = text.indexOf("обмен");
      if (i != -1) {
         i = text.indexOf("на", i + 5);
         if (i != -1) {
            for (String s : Constants.NOT_EXCHANGE_MODELS) {
               if (text.indexOf(s, i + 2) != -1) {
                  return false;
               }
            }
         }
      }
      return true;
   }

   public void getPhoneNumber(Context context, final OnGotMPNListener listener) {
      final WebView webView = new WebView(context);
      webView.getSettings().setDomStorageEnabled(true);
      webView.getSettings().setJavaScriptEnabled(true);
      webView.getSettings().setLoadsImagesAutomatically(false);
      webView.addJavascriptInterface(new JSInterface(new JSInterface.OnGotReceiverNumberListener() {
         @Override
         public void onGotReceiverNumber(String gotPhoneNumber, String adLink) {
            Log.d(Constants.APP_TAG, "номер->" + gotPhoneNumber + ", from " + adLink +
                    ", phone?=" + Utils.isMobilePhoneNumber(gotPhoneNumber));
            phoneNumber = gotPhoneNumber;
            listener.onGotMPN(Utils.isMobilePhoneNumber(phoneNumber));
         }
      }), "JSInterface");
      webView.setWebViewClient(new WebViewClient() {
         @Override
         public WebResourceResponse shouldInterceptRequest(WebView wv, String url) {
            if ((url.startsWith("https://m.avito.") || url.startsWith("https://www.avito."))
                    && !url.contains(".css") && !url.contains("favicon")) {
               return super.shouldInterceptRequest(wv, url);
            }
            return new WebResourceResponse("", "", null);
         }

         @Override
         public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.d(Constants.APP_TAG, "error: " + errorCode + " " + description + " " + failingUrl);
            if (failingUrl.contains("tel:")) {
               phoneNumber = failingUrl.substring(4);
               listener.onGotMPN(Utils.isMobilePhoneNumber(phoneNumber));
            }
         }

         @Override
         public void onPageFinished(WebView view, String gotUrl) {
            super.onPageFinished(view, gotUrl);
            Log.d(Constants.APP_TAG, "link loaded=" + gotUrl);
            if (gotUrl.equals(url)) {
               webView.loadUrl("javascript:var e = document.getElementsByClassName('button-text')[0];" +
                       "e.addEventListener('DOMSubtreeModified', function() {" +
                       "window.JSInterface.gotNumber(e.innerHTML, '" + gotUrl + "');" +
                       "}, false);" +
                       "document.getElementsByClassName('person-action')[0].click();");
            }
         }
      });
      webView.loadUrl(url);
   }

   /*private String formatPrice(String price) {
      if (price.contains(".") && price.indexOf('.') != price.length() - 1) {
         int i = price.length() - 2;
         while (price.charAt(i) != '.')
            i--;
         return price.substring(i + 1);
      }
      return price;
   }*/

   public interface OnGotMPNListener {
      void onGotMPN(boolean gotMobilePhoneNumber);
   }
}
