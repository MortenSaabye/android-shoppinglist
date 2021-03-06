package org.projects.shoppinglist;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by MortenSaabye on 03/11/2017.
 */

public class MyPreferenceFragment extends PreferenceFragment {

    private static String SETTINGS_NAMEKEY = "name";


    public static String getName(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SETTINGS_NAMEKEY, "");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //adding the preferences from the xml
        //so this will in fact be the whole view.
        addPreferencesFromResource(R.xml.prefs);
    }
}
