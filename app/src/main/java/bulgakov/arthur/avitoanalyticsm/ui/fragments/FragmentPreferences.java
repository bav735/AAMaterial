package bulgakov.arthur.avitoanalyticsm.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.content.MPNParsing;
import bulgakov.arthur.avitoanalyticsm.content.Search;
import bulgakov.arthur.avitoanalyticsm.utils.Constants;
import bulgakov.arthur.avitoanalyticsm.utils.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentPreferences extends PreferenceFragment {
   public final static int FRAGMENT_NUM = 4;
   public static final String FRAGMENT_TAG = "4";

   //   private ListPreferenceCustom location;
//   private CheckBoxPreference adverts;
   private CheckBoxPreference tracking;
   private CheckBoxPreference smsSending;
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
            if (!tracking.isChecked() && !smsSending.isChecked()) {
               for (int i = 0; i < prefs.getInt(Constants.SAVED_SEARCH_SIZE_KEY, 0); i++)
                  prefs.edit().remove(Constants.SAVED_SEARCH_ADS_KEY + i).commit();
            }
            return true;
         }
      });
      smsSending = (CheckBoxPreference) findPreference("check_box_preference_sms_sending");
      smsSending.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
         @Override
         public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (!smsSending.isChecked()) {
               if (!tracking.isChecked()) {
                  for (int i = 0; i < prefs.getInt(Constants.SAVED_SEARCH_SIZE_KEY, 0); i++)
                     prefs.edit().remove(Constants.SAVED_SEARCH_ADS_KEY + i).commit();
               }
               SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
               Log.d(Constants.APP_TAG, "sms sending checked!");
               if (prefs.getInt(Constants.MPN_PARSING_SIZE_KEY, 0) == 0) {
                  showDialogNewMPNParsing(prefs);
                  return false;
               }
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

   private void showDialogNewMPNParsing(final SharedPreferences prefs) {
      final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
      dialogBuilder.setTitle("Новая рассылка");
      LayoutInflater inflater = getActivity().getLayoutInflater();
      View dialogView = inflater.inflate(R.layout.dialog_new_mpn_parsing, null);
      dialogBuilder.setView(dialogView);
      final EditText etDescription = (EditText) dialogView.findViewById(R.id.dialog_mpn_parsing_et_description);
      final EditText etMessage = (EditText) dialogView.findViewById(R.id.dialog_mpn_parsing_et_message);
      dialogBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            smsSending.setChecked(false);
         }
      });
      dialogBuilder.setPositiveButton("Создать", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            String description = etDescription.getText().toString();
            String message = etMessage.getText().toString();
            if (!description.isEmpty() && !message.isEmpty()) {
               int searchesSize = prefs.getInt(Constants.SAVED_SEARCH_SIZE_KEY, 0);
               ArrayList<Search> searches = new ArrayList<>();
               for (int i = 0; i < searchesSize; i++) {
                  searches.add(new Search().fromJson(prefs.getString(Constants
                          .SAVED_SEARCH_KEY + i, null)));
               }
               MPNParsing newPNP = new MPNParsing(description, message, searches);
               newPNP.pos = prefs.getInt(Constants.MPN_PARSING_SIZE_KEY, 0);
               Utils.addArrayPref(prefs, Constants.MPN_PARSING_KEY, newPNP.toJson().toString());
               smsSending.setChecked(true);
            } else {
               Utils.toast(getActivity(), "заполните все поля");
               smsSending.setChecked(false);
            }
         }
      });
      dialogBuilder.setCancelable(false);
      dialogBuilder.show();
   }
}