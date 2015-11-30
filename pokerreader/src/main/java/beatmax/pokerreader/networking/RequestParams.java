package beatmax.pokerreader.networking;

import com.google.gson.Gson;

import java.util.ArrayList;

import beatmax.pokerreader.models.SitesE;

/**
 * Created by Beatmax on 08.08.15.
 */
public class RequestParams {

    private ArrayList<SitesE> mSites;
    private ArrayList<Integer> mIds;
    private ArrayList<Integer> mArticlesToFetch;

    private Gson mGson;
    private int mAge;

    public RequestParams() {
        mSites = new ArrayList<SitesE>();
        mIds = new ArrayList<Integer>();
        mArticlesToFetch = new ArrayList<Integer>();

        mGson = new Gson();
    }

    public void setSytesToSync(ArrayList<SitesE> sites){
        mSites = sites;
    }

    public void setIds(ArrayList<Integer> ids) {
        mIds = ids;
    }

    public String getSitesParam(){
        ArrayList<String> sites = new ArrayList<String>();
        for (SitesE site: mSites) {
            sites.add(site.getValue());
        }
        String sitesParam = mGson.toJson(sites);
        return sitesParam;
    }

    public String getIdsParam(){
        return mGson.toJson(mIds);
    }

    public ArrayList<SitesE> getSites() {
        return mSites;
    }

    public void addArticleToFetch(int id){
        mArticlesToFetch.add(id);
    }

    public ArrayList<Integer> getArticlesToFetch() {
        return mArticlesToFetch;
    }

    public String getArticlesToFetchParam() {
        return mGson.toJson(mArticlesToFetch);
    }


    public void setAge(int age) {
        mAge = age;
    }

    public int getAge() {
        return mAge;
    }
}
