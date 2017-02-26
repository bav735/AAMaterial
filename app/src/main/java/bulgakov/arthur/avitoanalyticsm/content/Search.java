package bulgakov.arthur.avitoanalyticsm.content;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import bulgakov.arthur.avitoanalyticsm.ui.fragments.FragmentSearch;
import bulgakov.arthur.avitoanalyticsm.utils.Constants;
import bulgakov.arthur.avitoanalyticsm.utils.Utils;

public class Search {
   public String location;
   public String category;
   public String kind;
   public String query;
   public String searchInTitleKey;
   public String exchangeKey;
   public String sellerTypeKey;
   public int priceFrom;
   public int priceTo;
   //   public String id;
//   public boolean isNew;
   public int pos;

   public Search() {
   }

//   public Search newSearch() {
//      isNew = true;
//      location = Constants.DEFAULT_LOCATION;
//      kind = query = category = "";
//      searchInTitleKey ="";
//              sellerTypeKey = "0";
//      priceFrom = priceTo = -1;
//      id = Utils.getCurrentDate();
//      return this;
//   }


   public Search fromFragmentSearch(FragmentSearch fragment) {
      location = fragment.tvLocation.getText().toString();
      kind = "";
      int parentCategoryNum = Constants.treeParentNum[fragment.currentCategoryNum];
      category = Constants.categories[fragment.currentCategoryNum];
      if (Utils.isCategoryLeaf(category) && !category.equals("Животные") &&
              !category.equals("Ноутбуки")) {
         category = Constants.categories[parentCategoryNum];
         kind = Constants.categories[fragment.currentCategoryNum];
      }
      priceFrom = toInt(fragment.etPriceFrom.getText().toString());
      priceTo = toInt(fragment.etPriceTo.getText().toString());
      if (fragment.cbSearchInTitle.isChecked()) {
         searchInTitleKey = "1";
      } else {
         searchInTitleKey = "0";
      }
      if (fragment.cbExchange.isChecked()) {
         exchangeKey = "1";
      } else {
         exchangeKey = "0";
      }
      sellerTypeKey = "" + (fragment.rgSellerType.getCheckedRadioButtonId() - 20000);
      query = fragment.etDescription.getText().toString();
      return this;
   }

   public JSONObject toJson() {
      JSONObject jsonObject = new JSONObject();
      try {
//         jsonObject.put("isNew", isNew);
//         jsonObject.put("id", id);
         jsonObject.put("location", location);
         jsonObject.put("category", category);
         jsonObject.put("kind", kind);
         jsonObject.put("priceFrom", priceFrom);
         jsonObject.put("priceTo", priceTo);
         jsonObject.put("query", query);
         jsonObject.put("searchInTitleKey", searchInTitleKey);
         jsonObject.put("exchangeKey", exchangeKey);
         jsonObject.put("sellerTypeKey", sellerTypeKey);
         return jsonObject;
      } catch (JSONException e) {
         Log.d(Constants.APP_TAG, e.toString());
         return null;
      }
   }

   public Search fromJson(String jsonString) {
      try {
         JSONObject jsonObject = new JSONObject(jsonString);
//         id = jsonObject.getString("id");
         location = jsonObject.getString("location");
         category = jsonObject.getString("category");
         kind = jsonObject.getString("kind");
         priceFrom = jsonObject.getInt("priceFrom");
//         isNew = jsonObject.getBoolean("isNew");
         priceTo = jsonObject.getInt("priceTo");
         query = jsonObject.getString("query");
         searchInTitleKey = jsonObject.getString("searchInTitleKey");
         exchangeKey = jsonObject.getString("exchangeKey");
         sellerTypeKey = jsonObject.getString("sellerTypeKey");
         return this;
      } catch (JSONException e) {
         Log.d(Constants.APP_TAG, "search from exception");
         return null;
      }
   }

//   public int getPosById() {
//      for (int i = 0; i < prefs.getInt(Constants.SAVED_SEARCH_SIZE_KEY, 0); i++) {
//         Search search = new Search(prefs).fromJson(prefs.getString(Constants.SAVED_SEARCH_KEY
//                 + i, null));
//         if (search.id.equals(id)) {
//            return i;
//         }
//      }
//      return -1;
//   }

   private int toInt(String s) {
      return Utils.toInt(s.replace(" ", ""));
   }
}