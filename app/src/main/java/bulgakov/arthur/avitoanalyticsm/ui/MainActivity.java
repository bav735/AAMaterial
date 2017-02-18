package bulgakov.arthur.avitoanalyticsm.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;

import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.ServiceSearchTracking;
import bulgakov.arthur.avitoanalyticsm.content.Ad;
import bulgakov.arthur.avitoanalyticsm.ui.fragments.FragmentPreferences;
import bulgakov.arthur.avitoanalyticsm.ui.fragments.FragmentShowAds;
import bulgakov.arthur.avitoanalyticsm.utils.Constants;
import bulgakov.arthur.avitoanalyticsm.utils.FragmentProcessor;
import bulgakov.arthur.avitoanalyticsm.utils.Utils;

/**
 * Lists all available quotes. This Activity supports a single pane (= smartphones) and a two pane mode (= large screens with >= 600dp width).
 * <p/>
 * Created by Andreas Schrade on 14.12.2015.
 */
public class MainActivity extends AppCompatActivity {
   protected static final int NAV_DRAWER_ITEM_INVALID = -1;

   private DrawerLayout drawerLayout;
   private Toolbar actionBarToolbar;
   public CharSequence actionBarTitle;
   public static ProgressDialog pd;
   //   private ImageView imageView;
   private SharedPreferences prefs;
   private NavigationView navigationView;
   private Intent intent;

   @Override
   protected void onPostCreate(Bundle savedInstanceState) {
      super.onPostCreate(savedInstanceState);
      setupNavDrawer();
//      Utils.replaceToFragment(this, FragmentPreferences.FRAGMENT_NUM);
      openDrawer();
   }

   /**
    * Sets up the navigation drawer.
    */
   private void setupNavDrawer() {
      drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
      if (drawerLayout == null) {
         // current activity does not have a drawer.
         return;
      }
      navigationView = (NavigationView) findViewById(R.id.nav_view);
      if (navigationView != null) {
         setupDrawerSelectListener(navigationView);
//         navigationView.setCheckedItem(Constants
//                 .fragmentMenuItemIds[FragmentPreferences.FRAGMENT_NUM]);
      }
   }

   /**
    * Creates the item click listener.
    *
    * @param navigationView the navigation view
    */
   private void setupDrawerSelectListener(NavigationView navigationView) {
      navigationView.setNavigationItemSelectedListener(
              new NavigationView.OnNavigationItemSelectedListener() {
                 @Override
                 public boolean onNavigationItemSelected(MenuItem menuItem) {
                    drawerLayout.closeDrawers();
//                  if (menuItem.getTitle().equals(actionBarTitle)) {
                    //  // Already selected
//                       Log.d(Constants.APP_TAG, "a");
//                       closeDrawer();
//                  }
                    goToNavDrawerItem(menuItem.getItemId());
                    return true;
                 }
              }

      );
   }

   @Override
   protected void onNewIntent(Intent newIntent) {
      Log.d(Constants.APP_TAG, "onnewintent showadsactivity");
      intent = newIntent;
   }

   /**
    * Handles the navigation item click
    *
    * @param itemId the selected navigation item
    */
   private void goToNavDrawerItem(int itemId) {
      if (Constants.fragments == null)
         return;
      int pos = -1;
      for (int i = 0; i < Constants.fragmentMenuItemIds.length; i++) {
         if (Constants.fragmentMenuItemIds[i] == itemId) {
            pos = i;
         }
      }
      if (pos == -1) {
         Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/bav735"));
         startActivity(browserIntent);
      } else {
         FragmentProcessor fp = new FragmentProcessor(this, pos);
         fp.replace(fp.get());
         actionBarTitle = Constants.fragmentTitles[pos];
         actionBarToolbar.setTitle(actionBarTitle);
      }
   }

   @Override
   public void onBackPressed() {
      FragmentManager fm = getFragmentManager();
      if (!fm.popBackStackImmediate()) {
//      fm.popBackStack();
//      } else {
         finish();
      }
   }

   protected ActionBar getActionBarToolbar() {
      if (actionBarToolbar == null) {
         actionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
         if (actionBarToolbar != null) {
            setSupportActionBar(actionBarToolbar);
         }
      }
      return getSupportActionBar();
   }

   protected void openDrawer() {
      if (drawerLayout == null)
         return;
      drawerLayout.openDrawer(GravityCompat.START);
   }

   protected void closeDrawer() {
      if (drawerLayout == null)
         return;
      drawerLayout.closeDrawer(GravityCompat.START);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      setupToolbar();
      Constants.initialize(getApplicationContext());

      pd = new ProgressDialog(this, R.style.CustomProgressDialog);
      pd.setCancelable(false);
      pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//      imageView = (ImageView) findViewById(R.id.activity_main_iv);
//      imageView.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View v) {
//            Utils.snack(imageView, "Нажмите \"назад\", чтобы выйти из приложения");
//         }
//      });
      final FragmentManager fm = getFragmentManager();
      fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
         @Override
         public void onBackStackChanged() {
            FragmentManager fragmentManager = getFragmentManager();
            int currBSSize = fragmentManager.getBackStackEntryCount();
            actionBarTitle = getString(R.string.app_name);
//            if (currBSSize == 1) {
//               imageView.setVisibility(View.GONE);
//            }
            if (currBSSize == 0) {
//               imageView.setVisibility(View.VISIBLE);
//               Utils.snack(imageView, "Нажмите \"назад\", чтобы выйти из приложения");
               Utils.toast(MainActivity.this, "Нажмите \"назад\", чтобы выйти из приложения");
               uncheckNavigationView();
            } else {
               Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
               if (fragment != null) {
                  int fragmentNum = Utils.toInt(fragment.getTag());
                  actionBarTitle = Constants.fragmentTitles[fragmentNum];
                  if (navigationView != null && fragmentNum < Constants.fragmentMenuItemIds.length) {
                     navigationView.setCheckedItem(Constants.fragmentMenuItemIds[fragmentNum]);
                  } else {
                     uncheckNavigationView();
                  }
               }
            }
            actionBarToolbar.setTitle(actionBarTitle);
         }
      });
      intent = getIntent();
      startService(new Intent(this, ServiceSearchTracking.class));
   }

   private void uncheckNavigationView() {
      if (navigationView == null) {
         return;
      }
      Menu menu = navigationView.getMenu();
      for (int i = 0; i < Constants.fragmentMenuItemIds.length; i++) {
         menu.getItem(i).setChecked(false);
      }
   }

   private void setupToolbar() {
      final ActionBar ab = getActionBarToolbar();
      ab.setHomeAsUpIndicator(R.drawable.ic_menu);
      ab.setDisplayHomeAsUpEnabled(true);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_activity_main, menu);
      return true;
   }

   public DrawerLayout getDrawerLayout() {
      return drawerLayout;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case android.R.id.home:
            openDrawer();
            return true;
         case R.id.menu_activity_main_action_settings:
            FragmentProcessor fp = new FragmentProcessor(this, FragmentPreferences.FRAGMENT_NUM);
            fp.replace(fp.get());
            return true;
      }
      return super.onOptionsItemSelected(item);
   }

   @Override
   protected void onResume() {
      Log.d(Constants.APP_TAG, "onresume showadsactivity");
      super.onResume();
      Log.d(Constants.APP_TAG, "" + (intent != null));
      if (intent != null && intent.hasExtra("ads_json_array")) {
         Log.d(Constants.APP_TAG, "notified!");
         try {
            JSONArray jsonArray = new JSONArray(intent.getStringExtra("ads_json_array"));
            ArrayList<Ad> adsList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
               Ad ad = new Ad().fromJson(jsonArray.getJSONObject(i).toString());
               ad.pos = i;
               adsList.add(ad);
            }
            FragmentProcessor fp = new FragmentProcessor(this, FragmentShowAds.FRAGMENT_NUM);
            FragmentShowAds fragment = (FragmentShowAds) fp.get();
            fragment.adsList = adsList;
            fp.replace(fragment);
         } catch (Exception e) {
            Log.d(Constants.APP_TAG, "activity show ads exception getlist");
         }
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      Log.d(Constants.APP_TAG, "ActivityMain onActivityResult");
      String tag = Constants.fragmentTags[getFragmentPosByTitle()];
      if (tag != null) {
         Fragment fragment = getFragmentManager().findFragmentByTag(tag);
         if (fragment != null)
            fragment.onActivityResult(requestCode, resultCode, data);
         else
            Toast.makeText(this, "fragment is null activity on result",
                    Toast.LENGTH_LONG).show();
      } else
         Toast.makeText(this, "tag is null activity on result",
                 Toast.LENGTH_LONG).show();
   }

   private int getFragmentPosByTitle() {
      for (int i = 0; i < Constants.fragments.length; i++) {
         if (Constants.fragmentTitles[i].equals(actionBarTitle)) {
            return i;
         }
      }
      return -1;
   }
}