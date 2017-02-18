package bulgakov.arthur.avitoanalyticsm.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.ServiceSearchTracking;
import bulgakov.arthur.avitoanalyticsm.utils.Constants;
import bulgakov.arthur.avitoanalyticsm.utils.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentPreferences extends PreferenceFragment {
   public final static int FRAGMENT_NUM = 3;
   public static final String FRAGMENT_TAG = "3";

   //   private ListPreferenceCustom location;
//   private CheckBoxPreference adverts;
   private CheckBoxPreference tracking;
//   private EditTextPreference graphRangeDays;

   public static FragmentPreferences newInstance() {
      return new FragmentPreferences();
   }

   public FragmentPreferences() {
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      super.onCreateOptionsMenu(menu, inflater);
      inflater.inflate(R.menu.menu_fragment_preferences, menu);
   }

   @Override
   public void onPrepareOptionsMenu(Menu menu) {
      menu.findItem(R.id.menu_activity_main_action_settings).setVisible(false);
      super.onPrepareOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
//      switch (item.getItemId()) {
//      }
      return super.onOptionsItemSelected(item);
   }



   @Override
   public void onCreate(Bundle paramBundle) {
      super.onCreate(paramBundle);
      addPreferencesFromResource(R.xml.fragment_preferences);
      final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
      tracking = (CheckBoxPreference) findPreference("check_box_preference_tracking");
      tracking.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
         @Override
         public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (tracking.isChecked()) {
               for (int i = 0; i < prefs.getInt(Constants.SAVED_SEARCH_SIZE_KEY, 0); i++)
                  prefs.edit().remove(Constants.SAVED_SEARCH_ADS_KEY + i).commit();
               Intent serviceIntent = new Intent(getActivity(), ServiceSearchTracking.class);
               getActivity().startService(serviceIntent);
            }
            return true;
         }
      });
//      location= (ListPreferenceCustom) findPreference("list_preference_location");
//      location.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//         @Override
//         public boolean onPreferenceChange(Preference preference, Object newValue) {
//            return false;
//         }
//      });
//      graphRangeDays = (EditTextPreference) findPreference("edit_text_preference_graph_range_days_t");
//      graphRangeDays.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//         @Override
//         public boolean onPreferenceChange(Preference preference, Object newValue) {
//            int newGrd = Utils.toInt(newValue.toString());
//            if (newGrd != -1) {
//               prefs.edit().putInt(Constants.ET_GRAPH_RANGE_KEY, newGrd).commit();
//            }
//            return false;
//         }
//      });
   }

}