/*
package bulgakov.arthur.avitoanalyticsm.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;

import java.util.ArrayList;

import bulgakov.arthur.avitoanalyticsm.utils.Constants;
import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.utils.Utils;
import bulgakov.arthur.avitoanalyticsm.content.Search;
import bulgakov.arthur.avitoanalyticsm.ui.base.BaseActivity;
import butterknife.Bind;
import butterknife.OnClick;

*/
/**
 * Shows the quote detail page.
 * <p/>
 * Created by Andreas Schrade on 14.12.2015.
 *//*

public class SearchDetailFragment extends BaseFragment {
   private final String[] titleByTreeHeight = new String[]{"категорию", "подкатегорию", "вид товара"};
   private SharedPreferences prefs;
   private boolean fabClicked;
   private Button btnClearCategory;
   private Button btnChooseCategory;
   private Button btnChooseLocation;
   private LinearLayout.LayoutParams llParams;
   private LinearLayout llFragment;
   private LinearLayout llDialog;
   private AlertDialog categoryDialog;
   private RadioGroup rgCategory;
   private RadioGroup rgLocation;
   private Search searchItem;
   private AlertDialog locationDialog;
   public int currentCategoryNum;

   @Bind(R.id.fragment_search_detail_tv_location)
   public TextView tvLocation;

   @Bind(R.id.fragment_search_detail_et_price_from)
   public EditText etPriceFrom;

   @Bind(R.id.fragment_search_detail_et_price_to)
   public EditText etPriceTo;

   @Bind(R.id.fragment_search_detail_cb_title)
   public CheckBox cbSearchInTitle;

   @Bind(R.id.fragment_search_detail_et_description)
   public EditText etDescription;

   @OnClick(R.id.fragment_search_detail_fab_done)
   public void onFabClicked(View view) {
      fabClicked = true;
      searchItem.isNew = false;
      int pos = searchItem.getPosById();
      if (pos != -1) {
         Log.d(Constants.APP_TAG, "num=" + currentCategoryNum);
         prefs.edit().putString(Constants.SAVED_SEARCH_KEY + pos,
                 new Search(prefs).fromSDFragment(this).toJson().toString()).commit();
         prefs.edit().putString(Constants.SAVED_SEARCH_ADS_KEY + pos, "").commit();
         Toast.makeText(getActivity(), "Успешно", Toast.LENGTH_SHORT).show();
      }
   }

   @Override
   public void onDestroyView() {
      if (!fabClicked) {
         int pos = searchItem.getPosById();
         if (pos != -1 && searchItem.isNew) {
            Utils.removeArrayPrefByPos(prefs, Constants.SAVED_SEARCH_KEY, pos);
         }
      }
      Log.d(Constants.APP_TAG, "ondestroyViewSDF");
      super.onDestroyView();
   }

   @Override
   public void onDestroy() {
      Log.d(Constants.APP_TAG, "ondestroySDF");
//      SearchListFragment.adapter.notifyDataSetChanged();
      super.onDestroy();
   }


   public SearchDetailFragment() {
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
      if (getArguments().containsKey(Constants.SAVED_SEARCH_KEY)) {
         Log.d(Constants.APP_TAG, "contains ssk");
         searchItem = new Search(prefs).fromJson(getArguments().getString(Constants.SAVED_SEARCH_KEY));
      }
      setHasOptionsMenu(true);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View rootView = inflateAndBind(inflater, container, R.layout.fragment_search_detail);

      if (!((BaseActivity) getActivity()).providesActivityToolbar()) {
         // No Toolbar present. Set include_toolbar:
         ((BaseActivity) getActivity()).setToolbar((Toolbar) rootView.findViewById(R.id.toolbar));
      }

      if (searchItem != null) {
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
         categoryDialog = new AlertDialog.Builder(getActivity())
                 .setTitle("Выберите категорию")
                 .setMessage(null)
                 .setView(dialogCategoryView)
                 .create();
         llParams = new LinearLayout.LayoutParams(
                 LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
         llParams.setMargins(10, 10, 10, 10);
         llDialog = (LinearLayout) dialogCategoryView.findViewById(R.id.fragment0_dialog_pick_category_ll);
         llFragment = (LinearLayout) rootView.findViewById(R.id.fragment_search_detail_ll);
         btnClearCategory = (Button) rootView.findViewById(R.id.fragment_search_detail_btn_clear_category);
         btnChooseCategory = (Button) rootView.findViewById(R.id.fragment_search_detail_btn_choose_category);
         btnChooseLocation = (Button) rootView.findViewById(R.id.fragment_search_detail_btn_choose_location);
         etDescription = (EditText) rootView.findViewById(R.id.fragment_search_detail_et_description);
         cbSearchInTitle = (CheckBox) rootView.findViewById(R.id.fragment_search_detail_cb_title);
         tvLocation = (TextView) rootView.findViewById(R.id.fragment_search_detail_tv_location);
         etPriceFrom = (EditText) rootView.findViewById(R.id.fragment_search_detail_et_price_from);
         etPriceTo = (EditText) rootView.findViewById(R.id.fragment_search_detail_et_price_to);
         tvLocation.setText(searchItem.location);
         btnChooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (locationDialog != null) {
                  locationDialog.show();
               }
            }
         });
         categoryDialog.setButton(DialogInterface.BUTTON_POSITIVE, "ОК", new DialogInterface.OnClickListener() {
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
         categoryDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
               llDialog.removeAllViews();
            }
         });
         btnClearCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               currentCategoryNum = Constants.treeParentNum[currentCategoryNum];
               showBranchCategory();
            }
         });

         currentCategoryNum = 0;
         String vertexName = searchItem.kind;
         if (vertexName.isEmpty())
            vertexName = searchItem.category;
         while (!Constants.categories[currentCategoryNum].equals(vertexName))
            currentCategoryNum++;
         showBranchCategory();
      }

      return rootView;
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      inflater.inflate(R.menu.menu_activity_main, menu);
      super.onCreateOptionsMenu(menu, inflater);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_settings:
            // your logic
            return true;
      }
      return super.onOptionsItemSelected(item);
   }

   public static SearchDetailFragment newInstance(Search search) {
      SearchDetailFragment fragment = new SearchDetailFragment();
      if (search != null) {
         Bundle args = new Bundle();
         args.putString(Constants.SAVED_SEARCH_KEY, search.toJson().toString());
         fragment.setArguments(args);
      }
      return fragment;
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
            treeTv.setText("-> " + Constants.categories[branch.get(i)]);
         else
            treeTv.setText("-> Все категории");
         treeTv.setTextSize(20);
         llFragment.addView(treeTv);
      }
      final int currentTreeHeight = Constants.treeHeight[currentCategoryNum];
      if (currentTreeHeight == 0)
         btnClearCategory.setVisibility(View.GONE);
      else
         btnClearCategory.setVisibility(View.VISIBLE);
      if (!Utils.isCategoryLeaf(currentCategoryNum)) {
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
               llDialog.addView(rgCategory);
               categoryDialog.setTitle("Выберите " + titleByTreeHeight[currentTreeHeight]);
               categoryDialog.show();
            }
         });
      } else
         btnChooseCategory.setVisibility(View.GONE);
   }
}
*/
