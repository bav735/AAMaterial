package bulgakov.arthur.avitoanalyticsm.content;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import bulgakov.arthur.avitoanalyticsm.utils.Constants;

/**
 * Created by A on 30.11.2015.
 */
public class MPNParsed {
   public String description;
   public HashSet<String> mpns;

   public MPNParsed() {
   }

   public MPNParsed(String description) {
      this.description = description;
      mpns = new HashSet<>();
   }

   public MPNParsed fromJson(String jsonString) {
      try {
         JSONObject jsonObject = new JSONObject(jsonString);
         mpns = getHashSet(jsonObject.getJSONArray("mpns"));
         description = jsonObject.getString("description");
      } catch (JSONException e) {
         Log.d(Constants.APP_TAG, "phonenumbers fromjson exception");
      }
      return this;
   }


   public JSONObject toJson() {
      JSONObject jsonObject = new JSONObject();
      try {
         jsonObject.put("description", description);
         jsonObject.put("mpns", getJsonArray(mpns));
      } catch (JSONException e) {
         Log.d(Constants.APP_TAG, "phonenumbers tojson exception");
      }
      return jsonObject;
   }

   private JSONArray getJsonArray(HashSet<String> hs) {
      JSONArray jsonArray = new JSONArray();
      for (String a : hs)
         jsonArray.put(a);
      return jsonArray;
   }

   private JSONArray getJsonArray(ArrayList<Search> ss) {
      JSONArray jsonArray = new JSONArray();
      for (Search s : ss)
         jsonArray.put(s.toJson().toString());
      return jsonArray;
   }

   private JSONArray getJsonArray(String[] ss) {
      JSONArray jsonArray = new JSONArray();
      for (String s : ss) jsonArray.put(s);
      return jsonArray;
   }

   private HashSet<String> getHashSet(JSONArray jsonArray) {
      HashSet<String> hs = new HashSet<>();
      for (int i = 0; i < jsonArray.length(); i++)
         try {
            hs.add(jsonArray.getString(i));
         } catch (JSONException e) {
            Log.d(Constants.APP_TAG, e.toString());
         }
      return hs;
   }
}
