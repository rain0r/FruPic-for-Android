package net.frubar.frupic;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Prefs extends PreferenceActivity {
   // Option names and default values
   private static final String OPT_USERNAME = "username";
   private static final String OPT_USERNAME_DEF = "";
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.settings);
   }

   /** Get the current value of the username option */
   
   public static String getUsername(Context context) {
      return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(OPT_USERNAME, OPT_USERNAME_DEF);
   }
}