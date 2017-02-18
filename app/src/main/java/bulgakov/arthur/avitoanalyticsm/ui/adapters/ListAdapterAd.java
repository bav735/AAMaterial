package bulgakov.arthur.avitoanalyticsm.ui.adapters;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import bulgakov.arthur.avitoanalyticsm.GetAdTask;
import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.content.Ad;
import bulgakov.arthur.avitoanalyticsm.ui.MainActivity;
import bulgakov.arthur.avitoanalyticsm.ui.fragments.FragmentFavouriteAds;
import bulgakov.arthur.avitoanalyticsm.ui.fragments.FragmentShowAd;
import bulgakov.arthur.avitoanalyticsm.utils.Constants;
import bulgakov.arthur.avitoanalyticsm.utils.FragmentProcessor;
import bulgakov.arthur.avitoanalyticsm.utils.Utils;

public class ListAdapterAd extends RecyclerView.Adapter<ListAdapterAd.HolderAd> {
   private ArrayList<Ad> adsList;
   private MainActivity activity;
   private Fragment fragment;
//   private Button btnLoad;

   public ListAdapterAd(MainActivity activity, ArrayList<Ad> adsList, Fragment fragment) {
      this.activity = activity;
      this.adsList = adsList;
      this.fragment = fragment;
   }

   @Override
   public HolderAd onCreateViewHolder(ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext())
              .inflate(R.layout.holder_list_ads, parent, false);
      // set the view's size, margins, paddings and layout parameters
      return new HolderAd(v);
   }

   @Override
   public void onBindViewHolder(HolderAd holder, final int pos) {
      holder.cv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (adsList.get(pos).description.isEmpty()) {
               Utils.showProgress(activity);
               new GetAdTask(new GetAdTask.OnAdReceivedListener() {
                  @Override
                  public void onAdReceived(Ad adReceived) {
                     FragmentProcessor fp = new FragmentProcessor(activity, FragmentShowAd.FRAGMENT_NUM);
                     FragmentShowAd fragment = (FragmentShowAd) fp.get();
                     fragment.ad = adReceived;
                     fp.replace(fragment);
                     Utils.hideProgress(activity);
                  }
               }, adsList.get(pos)).execute((Void) null);
            } else {
               FragmentProcessor fp = new FragmentProcessor(activity, FragmentShowAd.FRAGMENT_NUM);
               FragmentShowAd fragment = (FragmentShowAd) fp.get();
               fragment.ad = adsList.get(pos);
               fp.replace(fragment);
            }
         }
      });
      final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
      final Ad ad = adsList.get(pos);
      holder.tvDescription.setVisibility(View.GONE);
      holder.btnLink.setText(ad.linkText);
      holder.btnLink.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ad.url));
            activity.startActivity(browserIntent);
         }
      });
      holder.btnFavourite.setText("Удалить из избранного");
      if (!(fragment instanceof FragmentFavouriteAds)) {
         int posFavourite = -1;
         for (int i = 0; i < prefs.getInt(Constants.FAVOURITE_AD_SIZE_KEY, 0); i++) {
            Ad adFavourite = new Ad().fromJson(prefs.getString(Constants.FAVOURITE_AD_KEY + i, null));
            if (adFavourite.url.equals(ad.url)) {
               posFavourite = i;
               break;
            }
         }
         if (posFavourite == -1) {
            holder.btnFavourite.setText("Добавить в избранное");
         }
         final int finalPosFavourite = posFavourite;
         holder.btnFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (finalPosFavourite == -1) {
                  Utils.addArrayPref(prefs, Constants.FAVOURITE_AD_KEY, ad.toJson().toString());
                  Utils.snack(v, "Объявление добавлено в избранные");
               } else {
                  Utils.removeArrayPrefByPos(prefs, Constants.FAVOURITE_AD_KEY, finalPosFavourite);
                  Utils.snack(v, "Объявление удалено из избранного");
               }
               notifyDataSetChanged();
            }
         });
      } else {
         holder.btnFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Log.d(Constants.APP_TAG, "removed from pos=" + pos);
               Utils.removeArrayPrefByPos(prefs, Constants.FAVOURITE_AD_KEY, pos);
               adsList.remove(pos);
               notifyDataSetChanged();
            }
         });
      }
      holder.tvDate.setText(ad.date);
      holder.tvPrice.setText(ad.price);
      holder.tvTitle.setText(ad.title);
   }

   @Override
   public int getItemCount() {
      return adsList.size();
   }

   public class HolderAd extends RecyclerView.ViewHolder {
      public CardView cv;
      public TextView tvTitle;
      public TextView tvDescription;
      public TextView tvPrice;
      public Button btnLink;
      public Button btnFavourite;
      public TextView tvDate;

      public HolderAd(View itemView) {
         super(itemView);
         cv = (CardView) itemView.findViewById(R.id.list_ads_holder_cv);
         tvDate = (TextView) itemView.findViewById(R.id.list_ads_holder_tv_date);
         tvDescription = (TextView) itemView.findViewById(R.id.list_ads_holder_tv_description);
         btnLink = (Button) itemView.findViewById(R.id.list_ads_holder_btn_link);
         btnFavourite = (Button) itemView.findViewById(R.id.list_ads_holder_btn_favourite);
         tvPrice = (TextView) itemView.findViewById(R.id.list_ads_holder_tv_price);
         tvTitle = (TextView) itemView.findViewById(R.id.list_ads_holder_tv_title);
      }
   }
}
