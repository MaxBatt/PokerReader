package beatmax.pokerreader.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import beatmax.pokerreader.R;
import beatmax.pokerreader.models.SitesE;
import beatmax.pokerreader.prefs.PrefManager;

/**
 * Created by Max Batt on 09.09.2015.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter  {

    private PrefManager mPrefManager;

    private ArrayList<SitesE> activeSites;
    private int pageCount = 0;
    private String tabTitles[];
    private int tabIcons[] = {R.drawable.logo_pokerstrategy, R.drawable.logo_pokerfirma, R.drawable.logo_pokerstrategy};

    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        mPrefManager = new PrefManager(context);
        activeSites = mPrefManager.getActiveSites();
        pageCount = activeSites.size();

        tabTitles = new String[activeSites.size()];

        for(int i=0; i < activeSites.size(); i++){
            tabTitles[i] = activeSites.get(i).getValue();
        }


    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Override
    public Fragment getItem(int position) {
        return ArticleListFragment.newInstance(activeSites.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}