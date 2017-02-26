package bulgakov.arthur.avitoanalyticsm.ui.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import bulgakov.arthur.avitoanalyticsm.GetAdsTask;
import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.content.Ad;
import bulgakov.arthur.avitoanalyticsm.content.Search;
import bulgakov.arthur.avitoanalyticsm.ui.MainActivity;
import bulgakov.arthur.avitoanalyticsm.ui.adapters.ListAdapterAd;
import bulgakov.arthur.avitoanalyticsm.utils.Constants;
import bulgakov.arthur.avitoanalyticsm.utils.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentShowAds extends Fragment {
   public final static int FRAGMENT_NUM = 6;
   public static final String FRAGMENT_TAG = "6";
   private SharedPreferences prefs;
   private RecyclerView adLv;
   public ArrayList<Ad> adsList;
   private int adsLimit;

   public Search search;

   public static Fragment newInstance() {
      return new FragmentShowAds();
   }

   public FragmentShowAds() {
   }

   @Override
   public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
      View rootView = inflater.inflate(R.layout.fragment_show_ads, container, false);
      adLv = (RecyclerView) rootView.findViewById(R.id.fragment_show_ads_rv);
      // use this setting to improve performance if you know that changes
      // in content do not change the layout size of the RecyclerView
      adLv.setHasFixedSize(true);
      // use a linear layout manager for vertical list
      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
      adLv.setLayoutManager(layoutManager);
      return rootView;
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
   public void onResume() {
      super.onResume();
      if (adsList == null) {
         adsLimit = Utils.toInt(prefs.getString(Constants.ET_ADS_RANGE_KEY, null));
         Log.d(Constants.APP_TAG, "" + adsLimit);
         adsList = new ArrayList<>();
         Utils.showProgress(getActivity());
         getAds(1);
      } else {
         setAdapter();
      }
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      return super.onOptionsItemSelected(item);
   }

   private void getAds(final int pageNum) {
      final long start = System.currentTimeMillis();
      Log.d(Constants.APP_TAG, "pageNum=" + pageNum);
      new GetAdsTask(new GetAdsTask.OnAdsReceivedListener() {
         @Override
         public void onAdsReceived(final ArrayList<Ad> adsListReceived, final String flag) {
            if (adsListReceived != null) {
               Log.d(Constants.APP_TAG, "got size=" + adsListReceived.size());
            }
            Log.d(Constants.APP_TAG, "current size=" + adsList.size());
            Log.d(Constants.APP_TAG, "flag=" + flag);
            boolean endGettingAds = false;
            switch (flag) {
               case Constants.GET_ADS_OK:
                  adsList.addAll(adsListReceived);
                  break;
               case Constants.GET_ADS_EMPTY:
                  break;
               default:
                  endGettingAds = true;
            }
            if (adsList.size() >= adsLimit) {
               endGettingAds = true;
            }
            if (!endGettingAds) {
               getAds(pageNum + 1);
            } else {
               getActivity().runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                     long end = System.currentTimeMillis();
                     String message = "";
                     if (adsList != null && !adsList.isEmpty()) {
                        setAdapter();
                     } else {
                        message = "Код результата: " + flag + " / ";
                     }
                     Toast.makeText(getActivity(), message + "Время вычисления = "
                             + (end - start) + " мс", Toast.LENGTH_LONG).show();
                     Utils.hideProgress(getActivity());
                  }
               });
            }
         }
      }, pageNum, search, prefs).execute((Void) null);
   }

   private void setAdapter() {
      ListAdapterAd lvAdapter = new ListAdapterAd((MainActivity) getActivity(), adsList,
              FragmentShowAds.this);
      adLv.setAdapter(lvAdapter);
   }
}