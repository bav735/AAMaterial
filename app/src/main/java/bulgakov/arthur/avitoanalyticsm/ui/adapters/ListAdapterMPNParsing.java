package bulgakov.arthur.avitoanalyticsm.ui.adapters;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.content.MPNParsing;
import bulgakov.arthur.avitoanalyticsm.ui.MainActivity;

public class ListAdapterMPNParsing extends RecyclerView.Adapter<ListAdapterMPNParsing.HolderMPNParsing> {
   private ArrayList<MPNParsing> mpnParsings;
   private MainActivity activityMain;
   private SharedPreferences prefs;

   public ListAdapterMPNParsing(MainActivity activity, ArrayList<MPNParsing> list) {
      prefs = PreferenceManager.getDefaultSharedPreferences(activity);
      activityMain = activity;
      this.mpnParsings = list;
   }

   @Override
   public HolderMPNParsing onCreateViewHolder(ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext())
              .inflate(R.layout.holder_list_mpn_parsings, parent, false);
      // set the view's size, margins, paddings and layout parameters
      return new HolderMPNParsing(v);
   }

   @Override
   public void onBindViewHolder(HolderMPNParsing holder, final int pos) {
      holder.cv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
//            FragmentProcessor fp = new FragmentProcessor(activityMain, FragmentSearch.FRAGMENT_NUM);
//            FragmentSearch fragment = (FragmentSearch) fp.get();
//            fragment.searchFrom = mpnParsings.get(pos);
//            fp.replace(fragment);
         }
      });
      holder.tvDescription.setText("Описание: " + mpnParsings.get(pos).description);
      holder.tvMpnsCount.setText("Кол-во полученных номеров: " + mpnParsings.get(pos).mpns.size());
      holder.tvMessage.setText("Сообщение: " + mpnParsings.get(pos).message);
   }

   @Override
   public int getItemCount() {
      return mpnParsings.size();
   }

   public class HolderMPNParsing extends RecyclerView.ViewHolder {
      public CardView cv;
      public TextView tvDescription;
      public TextView tvMpnsCount;
      public TextView tvMessage;

      public HolderMPNParsing(View itemView) {
         super(itemView);
         cv = (CardView) itemView.findViewById(R.id.holder_list_mpn_parsings_cv);
         tvDescription = (TextView) itemView.findViewById(R.id.holder_list_mpn_parsings_tv_description);
         tvMpnsCount = (TextView) itemView.findViewById(R.id.holder_list_mpn_parsings_tv_mpns_count);
         tvMessage = (TextView) itemView.findViewById(R.id.holder_list_mpn_parsings_tv_message);
      }
   }
}
