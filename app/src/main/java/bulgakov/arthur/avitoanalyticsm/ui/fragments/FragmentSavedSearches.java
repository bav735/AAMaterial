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

import java.util.ArrayList;

import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.content.Search;
import bulgakov.arthur.avitoanalyticsm.ui.MainActivity;
import bulgakov.arthur.avitoanalyticsm.ui.adapters.ListAdapterSearch;
import bulgakov.arthur.avitoanalyticsm.utils.Constants;
import bulgakov.arthur.avitoanalyticsm.utils.FragmentProcessor;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentSavedSearches extends Fragment {
   public final static int FRAGMENT_NUM = 0;
   public static final String FRAGMENT_TAG = "0";

   private RecyclerView searchLv;
   private SharedPreferences prefs;

   public static Fragment newInstance() {
      return new FragmentSavedSearches();
   }

   public FragmentSavedSearches() {
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      Log.d(Constants.APP_TAG, "Fragment1 onCreate");
      super.onCreate(savedInstanceState);
//      action = getActivity().getIntent().getAction();
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      Log.d(Constants.APP_TAG, "Fragment1 onCreatView");
      setHasOptionsMenu(true);
      prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
      View rootView = inflater.inflate(R.layout.fragment_saved_searches, container, false);
      ButterKnife.bind(this, rootView);
      searchLv = (RecyclerView) rootView.findViewById(R.id.fragment1_layout_rv);
      // use this setting to improve performance if you know that changes
      // in content do not change the layout size of the RecyclerView
      searchLv.setHasFixedSize(true);
      // use a linear layout manager for vertical list
      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
      searchLv.setLayoutManager(layoutManager);
      return rootView;
   }

   @OnClick(R.id.fragment0_layout_fab_add)
   public void onFabClicked(View view) {
      FragmentProcessor fp = new FragmentProcessor(getActivity(), FragmentSearch.FRAGMENT_NUM);
      FragmentSearch fragment = (FragmentSearch) fp.get();
      fragment.searchFrom = null;
      fp.replace(fragment);
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
      ArrayList<Search> searchList = new ArrayList<>();
      for (int i = 0; i < prefs.getInt(Constants.SAVED_SEARCH_SIZE_KEY, 0); i++) {
         Search search = new Search(prefs).fromJson(prefs.getString(Constants.SAVED_SEARCH_KEY + i, null));
         search.pos = i;
         searchList.add(search);
      }
      ListAdapterSearch searchAdapter = new ListAdapterSearch((MainActivity) getActivity(), searchList);
      searchLv.setAdapter(searchAdapter);
   }
}