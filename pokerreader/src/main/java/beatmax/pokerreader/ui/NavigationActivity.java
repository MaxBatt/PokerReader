package beatmax.pokerreader.ui;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.github.pwittchen.prefser.library.Prefser;

import beatmax.pokerreader.BackgroundService;
import beatmax.pokerreader.R;
import beatmax.pokerreader.models.SitesE;
import beatmax.pokerreader.prefs.PrefManager;
import beatmax.pokerreader.prefs.PreferencesActivity;
import de.madcyph3r.materialnavigationdrawer.MaterialNavigationDrawer;
import de.madcyph3r.materialnavigationdrawer.activity.MaterialNavHeadItemActivity;
import de.madcyph3r.materialnavigationdrawer.head.MaterialHeadItem;
import de.madcyph3r.materialnavigationdrawer.menu.MaterialMenu;
import de.madcyph3r.materialnavigationdrawer.menu.item.custom.MaterialItemCustom;
import de.madcyph3r.materialnavigationdrawer.menu.item.section.MaterialItemSectionActivity;
import de.madcyph3r.materialnavigationdrawer.menu.item.section.MaterialItemSectionFragment;
import de.madcyph3r.materialnavigationdrawer.menu.item.style.MaterialItemDevisor;
import de.madcyph3r.materialnavigationdrawer.menu.item.style.MaterialItemLabel;
import de.madcyph3r.materialnavigationdrawer.tools.RoundedCornersDrawable;

public class NavigationActivity extends MaterialNavHeadItemActivity {

    private MaterialNavigationDrawer mDrawer = null;
    private BackgroundServiceConnection mServiceConnection;
    private BackgroundService mService;

    private MaterialMenu mMaterialMenu;
    private Prefser mPrefser;
    private PrefManager mPrefManager;

    @Override
    protected boolean finishActivityOnNewIntent() {
        return false;
    }

    @Override
    protected int getNewIntentRequestCode(Class clazz) {
        return 0;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        mPrefser = new Prefser(this);

        mPrefManager = new PrefManager(this);

        mDrawer = this;

        // create menu
        mMaterialMenu = new MaterialMenu();


        mMaterialMenu.add(new MaterialItemSectionFragment(NavigationActivity.this, getString(R.string.menu_item_articles),new ArticleListFragmentDeprecated(), getString(R.string.menu_item_articles))
                .setSectionColor(Color.parseColor("#ff0000")));

        mMaterialMenu.add(new MaterialItemDevisor());

        mMaterialMenu.add(new MaterialItemSectionActivity(NavigationActivity.this, "Settings", new Intent(NavigationActivity.this, PreferencesActivity.class))
                .setSectionColor(Color.parseColor("#ff0000")));

        mMaterialMenu.add(new MaterialItemDevisor());

        mMaterialMenu.add(new MaterialItemLabel(NavigationActivity.this, getString(R.string.title_sites)));


        // Checkbox Section for Pokersites
        mMaterialMenu.add(new MaterialItemCustom(NavigationActivity.this, R.layout.menu_section_pokersites));



        // create Head Item
        // use bitmap and make a circle photo
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.poker_icon);
        final RoundedCornersDrawable drawableAppIcon = new RoundedCornersDrawable(getResources(), bitmap);
        MaterialHeadItem headItem = new MaterialHeadItem(this, getString(R.string.menu_title), getString(R.string.menu_subtitle), drawableAppIcon, R.drawable.mat1, mMaterialMenu);
        this.addHeadItem(headItem);

        // load menu
        this.loadMenu(getCurrentHeadItem().getMenu());

        this.setLoadFragmentOnStartFromMenu(false);

        bindBackgroundService();
    }

    @Override
    public void afterInit(Bundle savedInstanceState) {

        // Checkboxes for Pokersites
        CheckBox cbPokerstrategy = (CheckBox)findViewById(R.id.cb_pokerstrategy);
        CheckBox cbPokerfirma = (CheckBox)findViewById(R.id.cb_pokerfirma);

        // Check from Sharedprefs which Checkboxes should be checked
        // PS.de
        cbPokerstrategy.setChecked(mPrefManager.isSynced(SitesE.POKERSTRATEGY));
        // Pokerfirma
        cbPokerfirma.setChecked(mPrefManager.isSynced(SitesE.POKERFIRMA));


        // Checkbox Listeners
        // PS.de
        cbPokerstrategy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPrefManager.setSynced(SitesE.POKERSTRATEGY, isChecked);
            }
        });

        // PS.de
        cbPokerfirma.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPrefManager.setSynced(SitesE.POKERFIRMA, isChecked);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        stopService(new Intent(this, BackgroundService.class));
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

            // load the first MaterialItemSectionFragment from the menu
            NavigationActivity.this.loadStartFragmentFromMenu(getCurrentHeadItem().getMenu());


        }


        // called when the service crashed or is killed
        @Override
        public void onServiceDisconnected(ComponentName name)
        {

        }
    };

    public BackgroundService getService() {
        return mService;
    }


}
