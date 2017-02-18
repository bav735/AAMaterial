package bulgakov.arthur.avitoanalyticsm.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.content.Search;
import bulgakov.arthur.avitoanalyticsm.utils.Constants;
import bulgakov.arthur.avitoanalyticsm.utils.Utils;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentSearch extends Fragment {
   public final static int FRAGMENT_NUM = 4;
   public final static String FRAGMENT_TAG = "4";
   private final String[] titleByTreeHeight = new String[]{"категорию", "подкатегорию", "вид товара"};
   private SharedPreferences prefs;
   private Button btnClearCategory;
   private Button btnChooseCategory;
   private Button btnChooseLocation;
   public TextView tvLocation;
   public EditText etPriceFrom;
   public EditText etPriceTo;
   public EditText etDescription;
   public CheckBox cbSearchInTitle;
   public CheckBox cbExchange;
   public RadioGroup rgSellerType;
   private LinearLayout.LayoutParams llParams;
   private LinearLayout llFragment;
   private LinearLayout llCategoryDialog;
   private AlertDialog dialog;
   private RadioGroup rgCategory;
   private RadioGroup rgLocation;
   private AlertDialog locationDialog;
   public int currentCategoryNum;
   public Search searchFrom;

   public static Fragment newInstance() {
      return new FragmentSearch();
   }

   public FragmentSearch() {
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      Log.d(Constants.APP_TAG, "Fragment0 onCreate");
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      Log.d(Constants.APP_TAG, "Fragment0 onCreatView");
      setHasOptionsMenu(true);
      prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
      View rootView = inflater.inflate(R.layout.fragment_search, container, false);
      ButterKnife.bind(this, rootView);
      llParams = new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      llParams.setMargins(10, 10, 10, 10);
      final View dialogLocationView = inflater.inflate(R.layout.dialog_pick_location, null);
      locationDialog = new AlertDialog.Builder(getActivity())
              .setTitle("Выберите местоположение")
              .setMessage(null)
              .setView(dialogLocationView)
              .create();
      rgLocation = (RadioGroup) dialogLocationView.findViewById(R.id.dialog_pick_location_rg);
      locationDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            int rbId = rgLocation.getCheckedRadioButtonId();
            if (rbId != -1) {
               RadioButton radioButton = (RadioButton) dialogLocationView.findViewById(rbId);
               tvLocation.setText(radioButton.getText());
            }
         }
      });

      final View dialogCategoryView = inflater.inflate(R.layout.dialog_pick_category, null);
      dialog = Utils.getAlertDialogBuilder(getActivity(), null, null, dialogCategoryView).create();
      llCategoryDialog = (LinearLayout) dialogCategoryView
              .findViewById(R.id.fragment0_dialog_pick_category_ll);

      llFragment = (LinearLayout) rootView.findViewById(R.id.fragment0_layout_ll);
      btnClearCategory = (Button) rootView.findViewById(R.id.fragment0_layout_btn_clear_category);
      btnChooseCategory = (Button) rootView.findViewById(R.id.fragment0_layout_btn_choose_category);
      btnChooseLocation = (Button) rootView.findViewById(R.id.fragment0_layout_btn_choose_location);
      etDescription = (EditText) rootView.findViewById(R.id.fragment0_layout_et_description);
      cbSearchInTitle = (CheckBox) rootView.findViewById(R.id.fragment0_layout_cb_title);
      cbExchange = (CheckBox) rootView.findViewById(R.id.fragment0_layout_cb_exchange);
      rgSellerType = (RadioGroup) rootView.findViewById(R.id.fragment0_layout_rg_seller_type);
      tvLocation = (TextView) rootView.findViewById(R.id.fragment0_layout_tv_location);
      etPriceFrom = (EditText) rootView.findViewById(R.id.fragment0_layout_et_price_from);
      etPriceTo = (EditText) rootView.findViewById(R.id.fragment0_layout_et_price_to);

      for (int i = 0; i < 3; i++) {
         RadioButton rbSellerType = new RadioButton(getActivity());
         rbSellerType.setId(i + 20000);
         rbSellerType.setText(Constants.sellerTypes[i]);
         rbSellerType.setTextSize(16);
         rgSellerType.addView(rbSellerType);
         if (searchFrom != null && ("" + i).equals(searchFrom.sellerTypeKey)
                 || searchFrom == null && i == Constants.DEFAULT_SELLER_TYPE) {
            rbSellerType.setChecked(true);
         }
      }
      btnChooseLocation.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (locationDialog != null) {
               locationDialog.show();
            }
         }
      });
      dialog.setButton(DialogInterface.BUTTON_POSITIVE, "ОК", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            int checkedRbId = rgCategory.getCheckedRadioButtonId();
            Log.d(Constants.APP_TAG, "Click on OK, id = " + checkedRbId);
            if (checkedRbId < 0)
               return;
            currentCategoryNum = checkedRbId - 10000;
            showBranchCategory();
         }
      });
      dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
         @Override
         public void onDismiss(DialogInterface dialog) {
            llCategoryDialog.removeAllViews();
         }
      });
      btnClearCategory.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            currentCategoryNum = Constants.treeParentNum[currentCategoryNum];
            showBranchCategory();
         }
      });
      return rootView;
   }

   @OnClick(R.id.fragment0_layout_fab_done)
   public void onFabClicked(View view) {
      String prefSearch = new Search(prefs).fromFragmentSearch(this).toJson().toString();
      if (searchFrom == null) {
         Utils.addArrayPref(prefs, Constants.SAVED_SEARCH_KEY, prefSearch);
         Utils.addArrayPref(prefs, Constants.SAVED_SEARCH_ADS_KEY, "");
         Utils.snack(view, "Поиск успешно сохранен");
      } else {
         prefs.edit().putString(Constants.SAVED_SEARCH_KEY + searchFrom.pos, prefSearch).commit();
         prefs.edit().putString(Constants.SAVED_SEARCH_ADS_KEY + searchFrom.pos, "").commit();
         Utils.snack(view, "Поиск успешно редактирован");
//         getActivity().getFragmentManager().popBackStack();
      }
   }


   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
      Log.d(Constants.APP_TAG, "Fragment0 onActivityCreated");
      super.onActivityCreated(savedInstanceState);
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      Log.d(Constants.APP_TAG, "Fragment0 onActivityResult");
      super.onActivityResult(requestCode, resultCode, data);
      if (resultCode == Activity.RESULT_OK) {
         switch (requestCode) {
            default:
         }
      }
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//      DrawerLayout drawerLayout = ((MainActivity) getActivity()).getDrawerLayout();
//      if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START))
//         return;
      super.onCreateOptionsMenu(menu, inflater);
//      inflater.inflate(R.menu.fragment0_menu, menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      return super.onOptionsItemSelected(item);
   }

   @Override
   public void onAttach(Activity activity) {
      Log.d(Constants.APP_TAG, "onAttach Fragment0");
      super.onAttach(activity);
   }

   @Override
   public void onResume() {
      Log.d(Constants.APP_TAG, "onResume Fragment0");
      super.onResume();
      currentCategoryNum = 0;
      if (searchFrom == null) {
         String location = Constants.DEFAULT_LOCATION;
         if (prefs.contains(Constants.LOCATION_KEY)) {
            location = Constants.locations[Utils.toInt(prefs.getString(Constants.LOCATION_KEY,
                    null)) - 1];
         }
         tvLocation.setText(location);
         etDescription.setText(null);
         etPriceFrom.setText(null);
         etPriceTo.setText(null);
         cbSearchInTitle.setChecked(false);
         cbExchange.setChecked(false);
         showBranchCategory();
      } else
         setFromSearch();
   }

   @Override
   public void onDestroyView() {
      super.onDestroyView();
      Log.d(Constants.APP_TAG, "onDestroyView FragmentNewSearch");
   }

   @Override
   public void onDestroy() {
      //      ServiceSearchTracking.isOnModifying = false;
      searchFrom = null;
      Log.d(Constants.APP_TAG, "onDestroy FragmentNewSearch");
      super.onDestroy();
   }

   private void showBranchCategory() {
      llFragment.removeAllViews();
      ArrayList<Integer> branch = new ArrayList<>();
      int tCategoryNum = currentCategoryNum;
      while (tCategoryNum != -1) {
         branch.add(0, tCategoryNum);
         tCategoryNum = Constants.treeParentNum[tCategoryNum];
      }
      for (int i = 0; i < branch.size(); i++) {
         TextView treeTv = new TextView(getActivity());
         treeTv.setClickable(false);
         treeTv.setLayoutParams(llParams);
         if (!Constants.categories[branch.get(i)].isEmpty())
            treeTv.setText(Constants.categories[branch.get(i)]);
         else
            treeTv.setText("Все категории");
         treeTv.setTextSize(20);
         llFragment.addView(treeTv);
      }
      final int currentTreeHeight = Constants.treeHeight[currentCategoryNum];
      if (currentTreeHeight == 0)
         btnClearCategory.setVisibility(View.GONE);
      else
         btnClearCategory.setVisibility(View.VISIBLE);
      if (!Utils.isCategoryLeaf(Constants.categories[currentCategoryNum])) {
         btnChooseCategory.setVisibility(View.VISIBLE);
         btnChooseCategory.setText("Выбрать " + titleByTreeHeight[currentTreeHeight]);
         btnChooseCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               rgCategory = new RadioGroup(getActivity());
               rgCategory.setLayoutParams(llParams);
               for (int num = 0; num < Constants.treeParentNum.length; num++) {
                  if (Constants.treeParentNum[num] == currentCategoryNum) {
                     RadioButton rbCategory = new RadioButton(getActivity());
                     rbCategory.setId(num + 10000);
                     rbCategory.setText(Constants.categories[num]);
                     rbCategory.setTextSize(18);
                     rgCategory.addView(rbCategory);
                  }
               }
               llCategoryDialog.addView(rgCategory);
               dialog.setTitle("Выберите " + titleByTreeHeight[currentTreeHeight]);
               dialog.show();
            }
         });
      } else
         btnChooseCategory.setVisibility(View.GONE);
   }

   public void setFromSearch() {
      Log.d(Constants.APP_TAG, "from edit fragment: " + searchFrom.query);
      tvLocation.setText(searchFrom.location);
      if (searchFrom.priceFrom != -1)
         etPriceFrom.setText("" + searchFrom.priceFrom);
      else
         etPriceFrom.setText("");
      if (searchFrom.priceTo != -1)
         etPriceTo.setText("" + searchFrom.priceTo);
      else
         etPriceTo.setText("");
      etDescription.setText(searchFrom.query);
      if (searchFrom.searchInTitleKey.equals("1")) {
         cbSearchInTitle.setChecked(true);
      } else {
         cbSearchInTitle.setChecked(false);
      }
      if (searchFrom.exchangeKey.equals("1")) {
         cbExchange.setChecked(true);
      } else {
         cbExchange.setChecked(false);
      }
      String vertexName = searchFrom.kind;
      if (vertexName.isEmpty())
         vertexName = searchFrom.category;
      while (!Constants.categories[currentCategoryNum].equals(vertexName))
         currentCategoryNum++;
      Log.d(Constants.APP_TAG, "currentCategoryNum = " + currentCategoryNum);
      showBranchCategory();
   }
}