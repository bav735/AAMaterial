package bulgakov.arthur.avitoanalyticsm.ui.adapters;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.content.Search;
import bulgakov.arthur.avitoanalyticsm.ui.MainActivity;
import bulgakov.arthur.avitoanalyticsm.ui.fragments.FragmentSearch;
import bulgakov.arthur.avitoanalyticsm.ui.fragments.FragmentShowAds;
import bulgakov.arthur.avitoanalyticsm.ui.fragments.FragmentShowGraph;
import bulgakov.arthur.avitoanalyticsm.utils.Constants;
import bulgakov.arthur.avitoanalyticsm.utils.FragmentProcessor;
import bulgakov.arthur.avitoanalyticsm.utils.Utils;

public class ListAdapterSearch extends RecyclerView.Adapter<ListAdapterSearch.HolderSearch> {
   private ArrayList<Search> searchList;
   private MainActivity activityMain;
   private SharedPreferences prefs;

   public ListAdapterSearch(MainActivity activity, ArrayList<Search> list) {
      prefs = PreferenceManager.getDefaultSharedPreferences(activity);
      activityMain = activity;
      this.searchList = list;
   }

   @Override
   public HolderSearch onCreateViewHolder(ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext())
              .inflate(R.layout.holder_list_searches, parent, false);
      // set the view's size, margins, paddings and layout parameters
      return new HolderSearch(v);
   }

   @Override
   public void onBindViewHolder(HolderSearch holder, final int pos) {
      holder.cv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            FragmentProcessor fp = new FragmentProcessor(activityMain, FragmentSearch.FRAGMENT_NUM);
            FragmentSearch fragment = (FragmentSearch) fp.get();
            fragment.searchFrom = searchList.get(pos);
            fp.replace(fragment);
         }
      });
      holder.tvLocation.setText("Местоположение: " + searchList.get(pos).location);
      String price = "Цена: ";
      if (searchList.get(pos).priceFrom == -1 && searchList.get(pos).priceTo == -1)
         price += "не указана";
      if (searchList.get(pos).priceFrom != -1)
         price += "от " + searchList.get(pos).priceFrom;
      if (searchList.get(pos).priceTo != -1)
         price += " до " + searchList.get(pos).priceTo;
      holder.tvPrice.setText(price);
      String category = "Категория: " + searchList.get(pos).category;
      if (searchList.get(pos).category.isEmpty())
         category += "Все категории";
      if (!searchList.get(pos).kind.isEmpty())
         category += ", вид товара: " + searchList.get(pos).kind;
      holder.tvCategory.setText(category);
      if (!searchList.get(pos).query.isEmpty()) {
         String description = "Описание";
         if (searchList.get(pos).searchInTitleKey.equals("1")) {
            description += " в заголовке";
         }
         if (searchList.get(pos).exchangeKey.equals("1")) {
            description += " только обмен";
         }
         holder.tvDescription.setText(description + ": " + searchList.get(pos).query);
      } else
         holder.tvDescription.setText("Описание: не указано");

      holder.btnShow.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
//            Intent intent = new Intent(activityMain, ActivityShowAds.class);
//            intent.putExtra("search_ads", prefs.getString(Constants.SAVED_SEARCH_KEY + pos, null));
//            activityMain.startActivity(intent);
            FragmentProcessor fp = new FragmentProcessor(activityMain, FragmentShowAds.FRAGMENT_NUM);
            FragmentShowAds fragment = (FragmentShowAds) fp.get();
            fragment.adsList = null;
            fragment.search = searchList.get(pos);
            fp.replace(fragment);
         }
      });
      holder.btnDelete.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Utils.removeArrayPrefByPos(prefs, Constants.SAVED_SEARCH_KEY, pos);
            Utils.removeArrayPrefByPos(prefs, Constants.SAVED_SEARCH_ADS_KEY, pos);
            searchList.remove(pos);
            notifyDataSetChanged();
         }
      });
      holder.btnGraph.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
//            Intent intent = new Intent(activityMain, ActivityShowGraph.class);
//            intent.putExtra("search_graph", searchList.get(pos).toJson().toString());
//            activityMain.startActivity(intent);
            FragmentProcessor fp = new FragmentProcessor(activityMain, FragmentShowGraph.FRAGMENT_NUM);
            FragmentShowGraph fragment = (FragmentShowGraph) fp.get();
            fragment.search = searchList.get(pos);
            fragment.mapAvitoPoints = null;
            fp.replace(fragment);
         }
      });
   }

   @Override
   public int getItemCount() {
      return searchList.size();
   }

   public class HolderSearch extends RecyclerView.ViewHolder {
      public CardView cv;
      public TextView tvLocation;
      public TextView tvCategory;
      public TextView tvPrice;
      public TextView tvDescription;
      public Button btnDelete;
      //   public Button btnEdit;
      public Button btnShow;
      public Button btnGraph;

      public HolderSearch(View itemView) {
         super(itemView);
         cv = (CardView) itemView.findViewById(R.id.holder_list_searches_cv);
         tvCategory = (TextView) itemView.findViewById(R.id.list_searches_holder_tv_category);
         tvDescription = (TextView) itemView.findViewById(R.id.list_searches_holder_tv_description);
         tvLocation = (TextView) itemView.findViewById(R.id.list_searches_holder_tv_location);
         tvPrice = (TextView) itemView.findViewById(R.id.list_searches_holder_tv_price);
         btnDelete = (Button) itemView.findViewById(R.id.list_searches_holder_btn_delete);
         btnShow = (Button) itemView.findViewById(R.id.list_searches_holder_btn_show);
         btnGraph = (Button) itemView.findViewById(R.id.list_searches_holder_btn_graph);
      }
   }
}
