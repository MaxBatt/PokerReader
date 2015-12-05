package beatmax.pokerreader.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.pwittchen.prefser.library.Prefser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import beatmax.pokerreader.R;
import beatmax.pokerreader.models.SitesE;

/**
 * Created by Beatmax on 08.09.15.
 */
public class PrefManager {

    private final Context mContext;
    private Prefser mPrefser;


    /**
     * Manages the preferences of our App
     * @param context
     */
    public PrefManager(Context context)
    {
        mContext = context;
        mPrefser = new Prefser(mContext);
    }

    /**
     * Sets a given site to be synced or not
     * @param site
     * @param synced
     */
    public void setSynced(SitesE site, boolean synced)
    {
        mPrefser.put(site.getValue(), synced);
    }


    /**
     * Returns the "to be synced" status
     * @param site
     * @return the to be synced status
     */
    public boolean isSynced(SitesE site)
    {
        return mPrefser.get(site.getValue(), Boolean.class, false);
    }

    /**
     * Persist in prefs if appData has just been cleared
     * @param cleared
     */
    public void setAppCleard(boolean cleared){
        mPrefser.put(mContext.getString(R.string.pref_key_app_cleared), cleared);
    }

    /**
     * Returns the appCleared status
     * @return
     */
    public boolean isAppCleared(){
        return mPrefser.get(mContext.getString(R.string.pref_key_app_cleared), Boolean.class, false);
    }

    /**
     * Returns the maximum article age to download
     * @return the to be synced status
     */
    public int getArticleAge()
    {
        String age =  mPrefser.get(mContext.getString(R.string.pref_article_age), String.class, "3");
        return Integer.parseInt(age);
    }

    /**
     * Checks which Sites are enabled in SharedPrefs
     * @return a list of the active sites
     */
    public ArrayList<SitesE> getSitesToSync(){
        ArrayList<SitesE> activeSites = new ArrayList<SitesE>();

        if(isSynced(SitesE.HIGHSTAKESDB))
            activeSites.add(SitesE.HIGHSTAKESDB);

        if(isSynced(SitesE.POKERNEWS))
            activeSites.add(SitesE.POKERNEWS);

        if(isSynced(SitesE.POKERFIRMA)) {
            activeSites.add(SitesE.POKERFIRMA);
        }

        if(isSynced(SitesE.POKEROLYMP)) {
            activeSites.add(SitesE.POKEROLYMP);
        }

        if(isSynced(SitesE.HOCHGEPOKERT)) {
            activeSites.add(SitesE.HOCHGEPOKERT);
        }

        if(isSynced(SitesE.POKERSTRATEGY)) {
            activeSites.add(SitesE.POKERSTRATEGY);
        }


        return activeSites;

    }

    /**
     * sets if articleList should be reloaded
     * @param shouldReload
     */
    public void setShouldReload(boolean shouldReload) {
        mPrefser.put(mContext.getString(R.string.pref_shouldReload), shouldReload);
    }

    /**
     * gets if articleList should be reloaded
     * @return shouldReload
     */
    public boolean getShouldReload(){
        return mPrefser.get(mContext.getString(R.string.pref_shouldReload), Boolean.class, false);
    }

    /**
     * @return all checked (active) sites
     */
    public ArrayList<SitesE> getActiveSites(){
//        ArrayList<SitesE> sites = new ArrayList<SitesE>();
//
//        if(mPrefser.get(mContext.getString(R.string.pref_key_site_pokerstrategy), Boolean.class, false)){
//            sites.add(SitesE.POKERSTRATEGY);
//        }
//
//        if(mPrefser.get(mContext.getString(R.string.pref_key_site_pokerfirma), Boolean.class, false)){
//            sites.add(SitesE.POKERFIRMA);
//        }
//
//        if(mPrefser.get(mContext.getString(R.string.pref_key_site_pokerolymp), Boolean.class, false)){
//            sites.add(SitesE.POKEROLYMP);
//        }
//
//        if(mPrefser.get(mContext.getString(R.string.pref_key_site_pokernews), Boolean.class, false)){
//            sites.add(SitesE.POKERNEWS);
//        }
//        return sites;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);



        Set<String> activeSites = prefs.getStringSet(mContext.getString(R.string.pref_key_active_sites), new HashSet<String>());

        ArrayList<SitesE> activeSitesList = new ArrayList<SitesE>();

        if(activeSites.contains(SitesE.HIGHSTAKESDB.getValue())){
            activeSitesList.add(SitesE.HIGHSTAKESDB);
        }

        if(activeSites.contains(SitesE.POKERNEWS.getValue())){
            activeSitesList.add(SitesE.POKERNEWS);
        }

        if(activeSites.contains(SitesE.POKERFIRMA.getValue())){
            activeSitesList.add(SitesE.POKERFIRMA);
        }

        if(activeSites.contains(SitesE.POKEROLYMP.getValue())){
            activeSitesList.add(SitesE.POKEROLYMP);
        }

        if(activeSites.contains(SitesE.HOCHGEPOKERT.getValue())){
            activeSitesList.add(SitesE.HOCHGEPOKERT);
        }

        if(activeSites.contains(SitesE.POKERSTRATEGY.getValue())){
            activeSitesList.add(SitesE.POKERSTRATEGY);
        }

        return activeSitesList;

        //mPrefser.get(mContext.getString(R.string.pref_key_active_sites), Set.class, )

    }


}
