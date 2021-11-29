package com.example.drowsy_driver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    /*
    private final SharedPreferences.OnSharedPreferenceChangeListener sharedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            //Preference listPreference = findPreference("pref_driver_type");
            //listPreference.setSummary(listPreference.getSharedPreferences().toString());
            //driverType = sharedPreferences.getString(key, "default");
            //editor = sharedPreferences.edit();
            //if (driverType.equals("Commuter") || driverType.equals("Road Tripper") || driverType.equals("Truck Driver")) {
                //Toast.makeText(getActivity(), driverType, Toast.LENGTH_LONG).show();
                //listPreference.setSummary(driverType);
            //}
            if (key.equals("pref_driver_type")) {
                Preference lp = findPreference(key);
                SharedPreferences sh = lp.getSharedPreferences();
                lp.setSummary(sh.getString("pref_driver_type", ""));
            }

        }
    };

     */





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.OnSharedPreferenceChangeListener sp = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                Preference lp = findPreference("pref_driver_type");
                SharedPreferences sh = lp.getSharedPreferences();
                lp.setSummary(sh.getString("pref_driver_type", ""));
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(sp);

         */
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if (s.equals("pref_driver_type"))
                {
                    Preference driverType = findPreference(s);
                    //SharedPreferences sh = lp.getSharedPreferences();
                    driverType.setSummary(sharedPreferences.getString("pref_driver_type", ""));
                }
            }
        };

    }

    //Both resume and pause are required for sharedpreferencechangelistener
    //both these methods have register and unregister functions
    //notice the global variable preferenceChangeListener; it keeps track of all changes

    @Override
    public void onResume() {
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        Preference driverType = findPreference("pref_driver_type");
        //this saves and displays the current driver type in summary
        driverType.setSummary(getPreferenceScreen().getSharedPreferences().getString("pref_driver_type", ""));

        super.onResume();
    }

    @Override
    public void onPause() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        super.onPause();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (findPreference("pref_personal_information") != null)
        {
            Preference personalInformation = findPreference("pref_personal_information");
            personalInformation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    NavHostFragment.findNavController(SettingsFragment.this).navigate(R.id.action_settingsFragment_to_displayPersonalInfoFragment);
                    return false;
                }
            });
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //IMPORTANT: for toolbar to be added, must create an additional layout that overrides the preferences

        //Then you must add this line under the main style in themes.xml:
        //<item name="preferenceTheme">@style/MainStyleName.PreferenceThemeOverlay</item>

        //Finally create a separate style theme; it should link to the additional layout file added:
        //<style name="MainStyleName.PreferenceThemeOverlay" parent="@style/PreferenceThemeOverlay">
        //  <item name="android:layout">@layout/preference_override</item>
        //</style>

        //check preference_override.xml for more details--should look almost exactly like it

        Toolbar toolbar = view.findViewById(R.id.toolbarSettings); //declaring and specifying toolbar
        ((AppCompatActivity)(getActivity())).setSupportActionBar(toolbar); //setting tool bar

        ((AppCompatActivity)(getActivity())).getSupportActionBar().setTitle("Settings"); //title
        ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(true); //adding back button

       // Preference lp = findPreference("pref_driver_type");
       // SharedPreferences sh = lp.getSharedPreferences();
       // lp.setSummary(sh.getString("pref_driver_type", ""));

    }



}