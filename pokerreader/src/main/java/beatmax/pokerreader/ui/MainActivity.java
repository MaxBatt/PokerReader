package beatmax.pokerreader.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.github.pwittchen.prefser.library.Prefser;

import java.util.Calendar;
import java.util.Date;

import beatmax.pokerreader.BackgroundService;
import beatmax.pokerreader.R;
import beatmax.pokerreader.helper.Tools;
import beatmax.pokerreader.models.RealmArticle;
import beatmax.pokerreader.networking.ArticleSyncronizer;
import beatmax.pokerreader.networking.RequestParams;
import beatmax.pokerreader.prefs.PrefManager;
import beatmax.pokerreader.prefs.PreferencesActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tabs)
    PagerSlidingTabStrip mTabStrip;
    @Bind(R.id.viewpager)
    ViewPager mViewpager;


    private Tools mTools;
    private Prefser mPrefser;
    private PrefManager mPrefManager;
    private BackgroundServiceConnection mServiceConnection;
    private BackgroundService mService;
    private ProgressDialog mDialog;
    private ArticleSyncronizer mSyncronizer;
    private boolean mShouldSync = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        ButterKnife.bind(this);

        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);

        mPrefser = new Prefser(this);

        mPrefManager = new PrefManager(this);

        mTools = new Tools(this);

        bindBackgroundService();

        // Toolbar
        setSupportActionBar(mToolbar);





        // Dialog
        mDialog = new ProgressDialog(this);
        mDialog.setTitle(getString(R.string.sync_dialog_title));
        mDialog.setMessage(getString(R.string.sync_dialog_message));

        // Check Network Connection frequently
//        new ReactiveNetwork().observeConnectivity(this)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//
//                .subscribe(new Action1<ConnectivityStatus>() {
//                    @Override
//                    public void call(ConnectivityStatus connectivityStatus) {
//                        switch (connectivityStatus) {
//                            case OFFLINE:
//                                Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set app color theme
        mTools.setTheme(mToolbar, mTabStrip);

        // if we resume from settings and the article age has changed, refresh list
        if(mSyncronizer != null && mPrefManager.getShouldReload()){
            synchronizeArticles();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSyncronizer.close();
        unbindService(mServiceConnection);
    }

    /**
     * Binding the BackgroundService
     */
    private void bindBackgroundService()
    {
        mServiceConnection = new BackgroundServiceConnection();
        Intent i = new Intent(this, BackgroundService.class);
        startService(i);
        // bind to the service.
        bindService(new Intent(this,
                BackgroundService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    protected class BackgroundServiceConnection implements android.content.ServiceConnection {

        @SuppressLint("NewApi")
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder)
        {
            mService = ((BackgroundService.Binder) binder).getService();
            mSyncronizer = mService.getSyncronizer();
            synchronizeArticles();


            mSyncronizer.addListener(new ArticleSyncronizer.SyncListener() {
                @Override
                public void onFinished() {
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {

                           clearOldArticles();
                           mPrefManager.setShouldReload(false);


                           mDialog.dismiss();


                           // Get the ViewPager and set it's PagerAdapter so that it can display items
                           mViewpager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));

                           // Give the PagerSlidingTabStrip the ViewPager
                           // Attach the view pager to the tab strip
                           mTabStrip.setViewPager(mViewpager);


                           // Attach the page change listener to tab strip and **not** the view pager inside the activity
                           mTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                               // This method will be invoked when a new page becomes selected.
                               @Override
                               public void onPageSelected(int position) {

                               }

                               // This method will be invoked when the current page is scrolled
                               @Override
                               public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                   // Code goes here
                               }

                               // Called when the scroll state changes:
                               // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
                               @Override
                               public void onPageScrollStateChanged(int state) {
                                   // Code goes here
                               }
                           });

                       }
                   });
                }
            });

        }


        // called when the service crashed or is killed
        @Override
        public void onServiceDisconnected(ComponentName name)
        {

        }
    };


    protected void synchronizeArticles()
    {
        mDialog.show();

        // Params for the Sync
        RequestParams params = new RequestParams();
        // Get sites to sync from Shared Prefs
        params.setSytesToSync(mPrefManager.getActiveSites());
        params.setAge(mPrefManager.getArticleAge());

        mSyncronizer.synchronize(params);
    }

    /**
     * Deletes all articles from Relam that are older than set in preferences
     */
    private void clearOldArticles(){

        // get oldest valid date for articles
        Date now = new Date();
        int articleAge = mPrefManager.getArticleAge();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DATE, -articleAge);
        Date oldestDate = cal.getTime();

        // delete all older articles from Realm
        final Realm realm = Realm.getInstance(this);
        RealmQuery<RealmArticle> query = realm.where(RealmArticle.class);
        query.lessThan("createdAt", oldestDate);
        final RealmResults<RealmArticle> res = query.findAll();
        realm.beginTransaction();
        res.clear();
        realm.commitTransaction();
        realm.close();
    }


    public BackgroundService getService() {
        return mService;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, PreferencesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
