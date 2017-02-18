package bulgakov.arthur.avitoanalyticsm.ui.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.content.Ad;
import bulgakov.arthur.avitoanalyticsm.ui.MainActivity;
import bulgakov.arthur.avitoanalyticsm.ui.adapters.ListAdapterAd;
import bulgakov.arthur.avitoanalyticsm.utils.Constants;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentFavouriteAds extends Fragment {
   public final static int FRAGMENT_NUM = 1;
   public static final String FRAGMENT_TAG = "1";
   private SharedPreferences prefs;
   private RecyclerView adLv;

   public static Fragment newInstance() {
      return new FragmentFavouriteAds();
   }

   public FragmentFavouriteAds() {
   }

   @Override
   public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
      View rootView = inflater.inflate(R.layout.fragment_favourite_ads, container, false);
      adLv = (RecyclerView) rootView.findViewById(R.id.fragment2_layout_rv);
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
      ArrayList<Ad> adsList = new ArrayList<>();
      for (int i = 0; i < prefs.getInt(Constants.FAVOURITE_AD_SIZE_KEY, 0); i++) {
         Ad ad = new Ad().fromJson(prefs.getString(Constants.FAVOURITE_AD_KEY + i, null));
         ad.pos = i;
         adsList.add(ad);
      }
      ListAdapterAd lvAdapter = new ListAdapterAd((MainActivity) getActivity(), adsList, this);
      adLv.setAdapter(lvAdapter);
//      adLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//         @Override
//         public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
//            try {
//               Activity activity = getActivity();
//               Intent intent = new Intent(activity, ActivityShowOneAd.class);
//               intent.putExtra("ad", adsList.get(pos).toJson().toString());
//               intent.putExtra("pos", pos);
//               activity.startActivity(intent);
//            } catch (JSONException e) {
//               Log.d(Constants.APP_TAG, "json error while putting extra in ActivityShowAds");
//            }
//         }
//      });
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         default:
            return super.onOptionsItemSelected(item);
      }
   }

}