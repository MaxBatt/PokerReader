package beatmax.pokerreader.prefs;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.snowdream.android.util.Log;
import com.orhanobut.bee.Bee;

import java.io.File;
import java.util.Set;

import beatmax.pokerreader.BuildConfig;
import beatmax.pokerreader.R;
import beatmax.pokerreader.debug.MainBeeConfig;
import beatmax.pokerreader.helper.Tools;
import beatmax.pokerreader.models.RealmArticle;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by Max Batt on 03.09.2015.
 */
public class PreferencesActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private Tools mTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        ButterKnife.bind(this);

        mTools = new Tools(this);

        // Toolbar
        setSupportActionBar(mToolbar);

        // Up Navigation
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTools.setTheme(mToolbar);


        if (BuildConfig.DEBUG){
            Bee.init(this)
                    .inject(MainBeeConfig.class);
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new SettingsFragment())
                    .commit();
        }





    }

    protected Toolbar getToolbar(){
        return mToolbar;
    }

    public static class SettingsFragment extends PreferenceFragment{

        PrefManager mPrefManager;
        private Preference mAgePreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Standard Prefs
            addPreferencesFromResource(R.xml.prefs);

            // Debug Prefs
            if (BuildConfig.DEBUG) {
                addPreferencesFromResource(R.xml.debug_prefs);
            }

            mPrefManager = new PrefManager(getActivity());




            // If article age has changed, set a flag preference age_has_changed, so that we can check that flag, when coming back to main activity
            // if the flag has been set, MainActivity will refresh the list
            mAgePreference = findPreference(getString(R.string.pref_article_age));
            mAgePreference.setSummary(getString(R.string.article_age_summary, String.valueOf(mPrefManager.getArticleAge())));

            mAgePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object age) {
                    mPrefManager.setShouldReload(true);
                    mAgePreference.setSummary(getString(R.string.article_age_summary, age));
                    return true;
                }
            });

            // App Theme Listener
            findPreference(getString(R.string.pref_key_app_theme)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    getActivity().finish();
                    getActivity().startActivity(getActivity().getIntent());
                    return true;
                }
            });

            // Active Sites Listener
            findPreference(getString(R.string.pref_key_active_sites)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {

                    // Check if at least one site is checked
                    Set<String> values = (Set<String>) o;
                    if(values.size() == 0){
                        Toast.makeText(getActivity(), R.string.at_least_one_site, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    mPrefManager.setShouldReload(true);
                    return true;
                }
            });

            if (BuildConfig.DEBUG){
                Preference clearButton = findPreference("btn_clear_app_data");

                // TODO DELETE ALL APP DATA
                clearButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        Realm realm = Realm.getInstance(getActivity());
                        realm.beginTransaction();
                        realm.clear(RealmArticle.class);
                        realm.commitTransaction();
                        realm.close();

                        clearApplicationData();

                        return false;
                    }
                });
            }
        }

        private void clearApplicationData() {
            File cache = getActivity().getCacheDir();
            File appDir = new File(cache.getParent());
            if (appDir.exists()) {
                String[] children = appDir.list();
                for (String s : children) {
                    if (!s.equals("lib")) {
                        deleteDir(new File(appDir, s));
                        Log.i("**************** File /data/data/de.beatmax.pokerreader/" + s + " DELETED *******************");
                    }
                }
            }
        }

        private static boolean deleteDir(File dir) {
            if (dir != null && dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }

            return dir.delete();
        }

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}