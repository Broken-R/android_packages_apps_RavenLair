/*
 * Copyright (C) 2018-2019 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.raven.lair.fragments;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.app.Activity;
import android.content.res.Resources;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.Handler;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.widget.Toast;
import androidx.preference.PreferenceCategory;
import androidx.preference.ListPreference;

import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.*;
import com.android.settingslib.search.SearchIndexable;


import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;


import com.corvus.support.preferences.SystemSettingSwitchPreference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class NavigationOptions extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String PIXEL_NAV_ANIMATION = "pixel_nav_animation";
    private static final String LAYOUT_SETTINGS = "navbar_layout_views";
    private static final String NAVIGATION_BAR_INVERSE = "navbar_inverse_layout";
    private static final String NAVBAR_VISIBILITY = "navbar_visibility";



    private SwitchPreference mPixelNavAnimation;
    private Preference mLayoutSettings;
    private SwitchPreference mSwapNavButtons;
    private SwitchPreference mNavbarVisibility;

    private boolean mIsNavSwitchingMode = false;
    private Handler mHandler;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.navigation_options);

        final PreferenceScreen prefScreen = getPreferenceScreen();

                mPixelNavAnimation = (SwitchPreference) findPreference(PIXEL_NAV_ANIMATION);
                mLayoutSettings = (Preference) findPreference(LAYOUT_SETTINGS);
                mSwapNavButtons = (SwitchPreference) findPreference(NAVIGATION_BAR_INVERSE);
                mNavbarVisibility = (SwitchPreference) findPreference(NAVBAR_VISIBILITY);



        if (!Utils.isThemeEnabled("com.android.internal.systemui.navbar.threebutton")) {
            prefScreen.removePreference(mLayoutSettings);
        }

      boolean defaultToNavigationBar = Utils.deviceSupportNavigationBar(getActivity());
        boolean showing = Settings.System.getInt(getContentResolver(),
                Settings.System.FORCE_SHOW_NAVBAR,
                defaultToNavigationBar ? 1 : 0) != 0;
        updateBarVisibleAndUpdatePrefs(showing);

        mNavbarVisibility.setOnPreferenceChangeListener(this);

        mHandler = new Handler();

    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
    if (preference.equals(mNavbarVisibility)) {
            if (mIsNavSwitchingMode) {
                return false;
            }
            mIsNavSwitchingMode = true;
            boolean showing = ((Boolean)newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.FORCE_SHOW_NAVBAR,
                    showing ? 1 : 0);
            updateBarVisibleAndUpdatePrefs(showing);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsNavSwitchingMode = false;
                }
            }, 1500);
            return true;
        }
        return false;
    }

    private void updateBarVisibleAndUpdatePrefs(boolean showing) {
        mNavbarVisibility.setChecked(showing);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CORVUS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

   public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(
                        Context context, boolean enabled) {
                    final SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.navigation_options;
                    return Arrays.asList(sir);
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    return result;
                }
            };

}
