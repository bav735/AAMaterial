package bulgakov.arthur.avitoanalyticsm.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.content.Ad;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentShowAd extends Fragment {
   public final static int FRAGMENT_NUM = 7;
   public final static String FRAGMENT_TAG = "7";
   public Ad ad;

   public static Fragment newInstance() {
      return new FragmentShowAd();
   }

   public FragmentShowAd() {
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      setHasOptionsMenu(true);
      View rootView = inflater.inflate(R.layout.fragment_show_ad, container, false);
      TextView tvDate = (TextView) rootView.findViewById(R.id.fragment_show_ad_tv_date);
      TextView tvDescription = (TextView) rootView.findViewById(R.id.fragment_show_ad_tv_description);
      Button btnLink = (Button) rootView.findViewById(R.id.fragment_show_ad_btn_link);
      TextView tvPrice = (TextView) rootView.findViewById(R.id.fragment_show_ad_tv_price);
      TextView tvTitle = (TextView) rootView.findViewById(R.id.fragment_show_ad_tv_title);
      tvDate.setText(ad.date);
      tvDescription.setText(ad.description);
      tvPrice.setText(ad.price);
      tvTitle.setText(ad.title);
      btnLink.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ad.url));
            getActivity().startActivity(browserIntent);
         }
      });
      return rootView;
   }

   @Override
   public void onResume() {
      super.onResume();
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//      DrawerLayout drawerLayout = ((MainActivity) getActivity()).getDrawerLayout();
//      if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START))
//         return;
      super.onCreateOptionsMenu(menu, inflater);
//      inflater.inflate(R.menu.fragment3_menu, menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      return super.onOptionsItemSelected(item);
   }

   @Override
   public void onAttach(Activity activity) {
      super.onAttach(activity);
//      ((ActivityMain) activity).onSectionAttached(FRAGMENT_NUM);
   }
}