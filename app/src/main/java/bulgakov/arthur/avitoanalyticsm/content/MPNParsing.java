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
public class MPNParsing {
   public String description;
   public String message;
   public HashSet<String> mpns;
   public int pos;
   public ArrayList<Search> searches;

   public MPNParsing() {
   }

   public MPNParsing(String description, String message, ArrayList<Search> searches) {
      this.description = description;
      this.message = message;
      this.searches = searches;
      mpns = new HashSet<>();
   }

   public MPNParsing fromJson(String jsonString) {
      try {
         JSONObject jsonObject = new JSONObject(jsonString);
         mpns = getHashSet(jsonObject.getJSONArray("mpns"));
         searches = getSearches(jsonObject.getJSONArray("searches"));
         description = jsonObject.getString("description");
         message = jsonObject.getString("message");
      } catch (JSONException e) {
         Log.d(Constants.APP_TAG, "phonenumbers fromjson exception");
      }
      return this;
   }


   public JSONObject toJson() {
      JSONObject jsonObject = new JSONObject();
      try {
         jsonObject.put("description", description);
         jsonObject.put("message", message);
         jsonObject.put("mpns", getJsonArray(mpns));
         jsonObject.put("searches", getJsonArray(searches));
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

   private String[] getArray(JSONArray jsonArray) {
      String[] arr = new String[jsonArray.length()];
      for (int i = 0; i < jsonArray.length(); i++) {
         try {
            arr[i] = jsonArray.getString(i);
         } catch (JSONException e) {
            Log.d(Constants.APP_TAG, "" + e);
         }
      }
      return arr;
   }

   private ArrayList<Search> getSearches(JSONArray jsonArray) {
      ArrayList<Search> arrayList = new ArrayList<>();
      for (int i = 0; i < jsonArray.length(); i++)
         try {
            arrayList.add(new Search().fromJson(jsonArray.getString(i)));
         } catch (JSONException e) {
            Log.d(Constants.APP_TAG, e.toString());
         }
      return arrayList;
   }
}
