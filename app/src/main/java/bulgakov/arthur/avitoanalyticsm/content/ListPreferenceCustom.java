package bulgakov.arthur.avitoanalyticsm.content;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class ListPreferenceCustom extends ListPreference {
   public ListPreferenceCustom(Context context) {
      super(context);
   }

   public ListPreferenceCustom(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public void setValue(final String value) {
      super.setValue(value);
      notifyChanged();
   }

   public void show() {
      showDialog(null);
   }
}
