package bulgakov.arthur.avitoanalyticsm.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import bulgakov.arthur.avitoanalyticsm.R;

/**
 * Created by A on 09.06.2016.
 */
public class FragmentProcessor {
   private int fragmentPos;
   private FragmentManager fragmentManager;

   public FragmentProcessor(Activity activity, int fragmentPos) {
      fragmentManager = activity.getFragmentManager();
      this.fragmentPos = fragmentPos;
   }

   public Fragment get() {
      Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
      if (fragment != null && Utils.toInt(fragment.getTag()) == fragmentPos) {
         return fragment;
      }
      fragment = fragmentManager.findFragmentByTag(Constants.fragmentTags[fragmentPos]);
      if (fragment == null) {
         fragment = Constants.fragments[fragmentPos];
      }
      return fragment;
   }

   public void replace(Fragment fragment) {
      fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment,
              Constants.fragmentTags[fragmentPos]).addToBackStack(null).commit();
   }
}
