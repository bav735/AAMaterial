package bulgakov.arthur.avitoanalyticsm;

import android.webkit.JavascriptInterface;

/**
 * Created by A on 16.05.2016.
 */
public class JSInterface {
   OnPASearchListener onPASearchListener;
   OnYMSearchListener onYMSearchListener;
   OnGraphListener onGraphListener;
   OnSellDateListener onSellDateListener;
   OnGotReceiverNumberListener listener;

   public JSInterface(OnPASearchListener l1, OnGraphListener l2) {
      onPASearchListener = l1;
      onGraphListener = l2;
   }

   public JSInterface(OnYMSearchListener l1, OnSellDateListener l2) {
      onYMSearchListener = l1;
      onSellDateListener = l2;
   }

   public JSInterface(OnGotReceiverNumberListener l) {
      listener = l;
   }

   @JavascriptInterface
   public void gotNumber(String receiverNumber, String adLink) {
      if (!receiverNumber.isEmpty())
         listener.onGotReceiverNumber(receiverNumber, adLink);
   }

   @JavascriptInterface
   public void onPASearch(String foundUrl) {
      onPASearchListener.onPASearch(foundUrl);
   }

   @JavascriptInterface
   public void onYMSearch(String foundUrl) {
      onYMSearchListener.onYMSearch(foundUrl);
   }

   @JavascriptInterface
   public void onGraph(double[] x, double[] y) {
      onGraphListener.onGraph(x, y);
   }

   @JavascriptInterface
   public void onSellDate(String sellDate) {
      onSellDateListener.onSellDate(sellDate);
   }

   public interface OnPASearchListener {
      void onPASearch(String foundUrl);
   }

   public interface OnYMSearchListener {
      void onYMSearch(String foundUrl);
   }

   public interface OnGraphListener {
      void onGraph(double[] x, double[] y);
   }

   public interface OnSellDateListener {
      void onSellDate(String sellDate);
   }

   public interface OnGotReceiverNumberListener {
      void onGotReceiverNumber(String receiverNumber, String adLink);
   }
}