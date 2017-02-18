package bulgakov.arthur.avitoanalyticsm.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jjoe64.graphview.series.DataPoint;

import java.util.HashMap;

import bulgakov.arthur.avitoanalyticsm.R;
import bulgakov.arthur.avitoanalyticsm.utils.Utils;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentMakeMark extends Fragment {
   public final static int FRAGMENT_NUM = 2;
   public final static String FRAGMENT_TAG = "2";
   public static DataPoint[] points;
   private String stateMark;
   private String workMark;
   private EditText etAge;
//   private EditText etModel;

   public static Fragment newInstance() {
      return new FragmentMakeMark();
   }

   public FragmentMakeMark() {
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      setHasOptionsMenu(true);
      View rootView = inflater.inflate(R.layout.fragment_make_mark, container, false);
      ButterKnife.bind(this, rootView);
      final TextView tvState = (TextView) rootView.findViewById(R.id.fragment3_layout_tv_state);
      final TextView tvWork = (TextView) rootView.findViewById(R.id.fragment3_layout_tv_work);
      final View dialogStateView = inflater.inflate(R.layout.dialog_pick_mark_state, null);
      final AlertDialog dialogState = Utils.getAlertDialogBuilder(getActivity(),
              null, null, dialogStateView).create();
      final RadioGroup rgDialogState = (RadioGroup) dialogStateView.findViewById(R.id.fragment3_state_dialog_pick_mark_rg);
      dialogState.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            dialogState.dismiss();
         }
      });
      dialogState.setOnDismissListener(new DialogInterface.OnDismissListener() {
         @Override
         public void onDismiss(DialogInterface dialog) {
            int rgId = rgDialogState.getCheckedRadioButtonId();
            if (rgId != -1) {
               RadioButton rb = (RadioButton) dialogStateView.findViewById(rgId);
               stateMark = rb.getText().toString();
               tvState.setText("Состояние: " + stateMark);
            }
         }
      });

      final View dialogWorkView = inflater.inflate(R.layout.dialog_pick_mark_work, null);
      final AlertDialog dialogWork = Utils.getAlertDialogBuilder(getActivity(), null, null, dialogWorkView).create();
      final RadioGroup rgDialogWork = (RadioGroup) dialogWorkView.findViewById(R.id.fragment3_work_dialog_pick_mark_rg);
      dialogWork.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            dialogWork.dismiss();
         }
      });
      dialogWork.setOnDismissListener(new DialogInterface.OnDismissListener() {
         @Override
         public void onDismiss(DialogInterface dialog) {
            int rgId = rgDialogWork.getCheckedRadioButtonId();
            if (rgId != -1) {
               RadioButton rb = (RadioButton) dialogWorkView.findViewById(rgId);
               workMark = rb.getText().toString();
               tvWork.setText("Работоспособность: " + workMark);
            }
         }
      });

//      etModel = (EditText) rootView.findViewById(R.id.fragment3_layout_et_model);
      etAge = (EditText) rootView.findViewById(R.id.fragment3_layout_et_age);
      Button btnState = (Button) rootView.findViewById(R.id.fragment3_layout_btn_state);
      btnState.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            dialogState.show();
         }
      });
      Button btnWork = (Button) rootView.findViewById(R.id.fragment3_layout_btn_work);
      btnWork.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            dialogWork.show();
         }
      });

      return rootView;
   }

   @Override
   public void onResume() {
      super.onResume();
   }

   @OnClick(R.id.fragment_make_mark_fab_info)
   public void onFabClicked(View view) {
      String age = etAge.getText().toString();
//      String model = etModel.getText().toString();
      if (!age.isEmpty() /*&& !model.isEmpty()*/ && stateMark != null && workMark != null) {
         HashMap<String, Integer> markMap = new HashMap<>();
         markMap.put("идеально", 100);
         markMap.put("отлично", 90);
         markMap.put("хорошо", 80);
         markMap.put("удовлетворительно", 70);
         markMap.put("ужасно", 60);
         for (int i = 0; i <= 24; i++) {
            markMap.put("" + i, 90 - i);
         }
         double k = markMap.get(age) * markMap.get(stateMark) * markMap.get(workMark) / 10000.0;
         Utils.snack(view, "Цена по отношению к средней на авито ~" + (int) k + "%");
      } else {
         Utils.snack(view, "Неверный запрос");
      }
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//      DrawerLayout drawerLayout = ((MainActivity) getActivity()).getDrawerLayout();
//      if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START))
//         return;
      super.onCreateOptionsMenu(menu, inflater);
//      inflater.inflate(R.menu.fragment3_menu, menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      return super.onOptionsItemSelected(item);
   }

   @Override
   public void onAttach(Activity activity) {
      super.onAttach(activity);
//      ((ActivityMain) activity).onSectionAttached(FRAGMENT_NUM);
   }
}