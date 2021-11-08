package com.example.drowsydriversettings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;
import java.util.zip.Inflater;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    public String driverType;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener sharedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            driverType = sharedPreferences.getString(key, "default");
            //editor = sharedPreferences.edit();
            if (driverType.equals("Commuter") || driverType.equals("Road Tripper") || driverType.equals("Truck Driver")) {
                Toast.makeText(getActivity(), driverType, Toast.LENGTH_LONG).show();
            }
        }
    };

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
    }



}