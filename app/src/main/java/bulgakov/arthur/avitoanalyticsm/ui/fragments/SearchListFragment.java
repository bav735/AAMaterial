//package bulgakov.arthur.avitoanalyticsm.ui.fragments;
//
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.app.ListFragment;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Build;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.support.v7.widget.PopupMenu;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import bulgakov.arthur.avitoanalyticsm.utils.Constants;
//import bulgakov.arthur.avitoanalyticsm.R;
//import bulgakov.arthur.avitoanalyticsm.content.Search;
//
///**
// * Shows a list of all available quotes.
// * <p/>
// * Created by Andreas Schrade on 14.12.2015.
// */
//public class SearchListFragment extends ListFragment {
//   public static MyListAdapter adapter;
//   private Callback callback = searchCallback;
//
//   /**
//    * A callback interface. Called whenever a item has been selected.
//    */
//   public interface Callback {
//      void onItemSelected(Search search);
//   }
//
//   /**
//    * A dummy no-op implementation of the Callback interface. Only used when no active Activity is present.
//    */
//   private static final Callback searchCallback = new Callback() {
//      @Override
//      public void onItemSelected(Search search) {
//      }
//   };
//
//   @Override
//   public void onCreate(Bundle savedInstanceState) {
//      super.onCreate(savedInstanceState);
//      adapter = new MyListAdapter();
//      setListAdapter(adapter);
//      setHasOptionsMenu(true);
//   }
//
//   /**
//    * onAttach(Context) is not called on pre API 23 versions of Android.
//    * onAttach(Activity) is deprecated but still necessary on older devices.
//    */
//   @TargetApi(23)
//   @Override
//   public void onAttach(Context context) {
//      super.onAttach(context);
//      onAttachToContext(context);
//   }
//
//   /**
//    * Deprecated on API 23 but still necessary for pre API 23 devices.
//    */
//   @SuppressWarnings("deprecation")
//   @Override
//   public void onAttach(Activity activity) {
//      super.onAttach(activity);
//      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//         onAttachToContext(activity);
//      }
//   }
//
//   /**
//    * Called when the fragment attaches to the context
//    */
//   protected void onAttachToContext(Context context) {
//      if (!(context instanceof Callback)) {
//         throw new IllegalStateException("Activity must implement callback interface.");
//      }
//
//      callback = (Callback) context;
//   }
//
//   public class MyListAdapter extends BaseAdapter {
//
//      @Override
//      public int getCount() {
//         SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//         return prefs.getInt(Constants.SAVED_SEARCH_SIZE_KEY, 0);
//      }
//
//      @Override
//      public Object getItem(int position) {
//         return getSearchByPos(position);
//      }
//
//      @Override
//      public long getItemId(int position) {
//         return getSearchByPos(position).id.hashCode();
//      }
//
//      @Override
//      public View getView(int position, View convertView, ViewGroup container) {
//         if (convertView == null) {
//            convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_search, container, false);
//         }
//
//         final Search item = (Search) getItem(position);
//
//         TextView tvCategory = ((TextView) convertView.findViewById(R.id.list_item_search_tv_category));
//         TextView tvPrice = ((TextView) convertView.findViewById(R.id.list_item_search_tv_price));
//         TextView tvDescription = ((TextView) convertView.findViewById(R.id.list_item_search_tv_description));
//         TextView tvLocation = ((TextView) convertView.findViewById(R.id.list_item_search_tv_location));
//
//         tvLocation.setText("Местоположение: " + item.location);
//         String price = "Цена: ";
//         if (item.priceFrom == -1 && item.priceTo == -1)
//            price += "не указана";
//         if (item.priceFrom != -1)
//            price += "от " + item.priceFrom;
//         if (item.priceTo != -1)
//            price += " до " + item.priceTo;
//         tvPrice.setText(price);
//         String category = "Категория: " + item.category;
//         Log.d(Constants.APP_TAG, item.toJson().toString());
//         if (!item.kind.isEmpty())
//            category += ", вид товара: " + item.kind;
//         tvCategory.setText(category);
//         if (!item.query.isEmpty()) {
//            String description = "Описание";
//            if (item.searchInTitleKey.equals("1"))
//               description += " в заголовке";
//            tvDescription.setText(description + ": " + item.query);
//         } else
//            tvDescription.setText("Описание: не указано");
//
//         final ImageView img = (ImageView) convertView.findViewById(R.id.thumbnail);
//         img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               showPopupMenu(v);
//            }
//         });
//
//         return convertView;
//      }
//   }
//
//   public SearchListFragment() {
//   }
//
//   private void showPopupMenu(View v) {
//      PopupMenu popupMenu = new PopupMenu(getActivity(), v);
//      popupMenu.inflate(R.menu.popup_search_list);
//
//      popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//         @Override
//         public boolean onMenuItemClick(MenuItem item) {
//            switch (item.getItemId()) {
//               case R.id.menu_show_ads:
//                  return true;
//               case R.id.menu_show_graph:
//                  return true;
//               case R.id.menu_delete:
//                  return true;
//               default:
//                  return false;
//            }
//         }
//      });
//
//      popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
//         @Override
//         public void onDismiss(PopupMenu menu) {
//         }
//      });
//      popupMenu.show();
//   }
//
//   private Search getSearchByPos(int pos) {
//      Log.d(Constants.APP_TAG, "" + pos);
//      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//      return new Search(prefs).fromJson(prefs.getString(Constants.SAVED_SEARCH_KEY + pos, null));
//   }
//}
