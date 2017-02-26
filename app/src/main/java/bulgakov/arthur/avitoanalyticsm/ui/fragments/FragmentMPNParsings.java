package bulgakov.arthur.avitoanalyticsm.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
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

import java.io.IOException;
import java.util.ArrayList;

import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.content.MPNParsing;
import bulgakov.arthur.avitoanalyticsm.ui.MainActivity;
import bulgakov.arthur.avitoanalyticsm.ui.adapters.ListAdapterMPNParsing;
import bulgakov.arthur.avitoanalyticsm.utils.Constants;
import bulgakov.arthur.avitoanalyticsm.utils.Utils;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentMPNParsings extends Fragment {
   public final static int FRAGMENT_NUM = 3;
   public static final String FRAGMENT_TAG = "3";

   private RecyclerView mpnParsingRv;
   private SharedPreferences prefs;

   public static Fragment newInstance() {
      return new FragmentMPNParsings();
   }

   public FragmentMPNParsings() {
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      Log.d(Constants.APP_TAG, "Fragment8 onCreate");
      super.onCreate(savedInstanceState);
//      action = getActivity().getIntent().getAction();
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      Log.d(Constants.APP_TAG, "Fragment8 onCreatView");
      setHasOptionsMenu(true);
      prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
      View rootView = inflater.inflate(R.layout.fragment_mpn_parsing, container, false);
      ButterKnife.bind(this, rootView);
      mpnParsingRv = (RecyclerView) rootView.findViewById(R.id.fragment_mpn_parsing_rv);
      // use this setting to improve performance if you know that changes
      // in content do not change the layout size of the RecyclerView
      mpnParsingRv.setHasFixedSize(true);
      // use a linear layout manager for vertical list
      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
      mpnParsingRv.setLayoutManager(layoutManager);
      return rootView;
   }

   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
      Log.d(Constants.APP_TAG, "Fragment1 onActivityCreated");
      super.onActivityCreated(savedInstanceState);
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      Log.d(Constants.APP_TAG, "Fragment1 onActivityResult");
      super.onActivityResult(requestCode, resultCode, data);
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//      DrawerLayout drawerLayout = ((MainActivity) getActivity()).getDrawerLayout();
//      if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START))
//         return;
      super.onCreateOptionsMenu(menu, inflater);
//      inflater.inflate(R.menu.fragment1_menu, menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         default:
            return super.onOptionsItemSelected(item);
      }
   }

   @Override
   public void onAttach(Activity activity) {
      Log.d(Constants.APP_TAG, "onAttach Fragment1");
      super.onAttach(activity);
      if (activity == null)
         Log.d(Constants.APP_TAG, "activity is null!");
//      ((ActivityMain) activity).onSectionAttached(FRAGMENT_NUM);
   }

   @Override
   public void onResume() {
      super.onResume();
      ArrayList<MPNParsing> mpnParsings = new ArrayList<>();
      for (int i = 0; i < prefs.getInt(Constants.MPN_PARSING_SIZE_KEY, 0); i++) {
         MPNParsing mpnParsing = new MPNParsing().fromJson(prefs.getString(
                 Constants.MPN_PARSING_KEY + i, null));
         mpnParsing.pos = i;
         mpnParsings.add(mpnParsing);
      }
      ListAdapterMPNParsing listAdapterMPNParsing = new ListAdapterMPNParsing((MainActivity) getActivity(), mpnParsings);
      mpnParsingRv.setAdapter(listAdapterMPNParsing);
   }

   @OnClick(R.id.fragment_mpn_parsing_fab)
   public void onFabClicked(View view) {
      try {
         Utils.saveToDB(getActivity(), Constants.MPN_PARSING_KEY);
         Utils.snack(view, "Успешно сохранено");
      } catch (IOException e) {
         Utils.snack(view, "Не удалось сохраненить");
         Log.d(Constants.APP_TAG, e.toString());
      }
   }
}