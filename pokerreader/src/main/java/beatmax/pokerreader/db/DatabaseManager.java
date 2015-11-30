package beatmax.pokerreader.db;

import android.content.Context;

import com.github.snowdream.android.util.Log;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import beatmax.pokerreader.helper.Tools;
import beatmax.pokerreader.models.Article;
import beatmax.pokerreader.models.ArticleList;
import beatmax.pokerreader.models.RealmArticle;
import beatmax.pokerreader.models.SitesE;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Max Batt on 02.09.2015.
 */
public class DatabaseManager {

    private final static String TAG = DatabaseManager.class.getSimpleName();

    private Context mContext;
    private Realm mRealm;
    private Tools mTools;

    public DatabaseManager(Context context) {
        mContext = context;
        mRealm = Realm.getInstance(mContext);
        mTools = new Tools(mContext);
    }

    /**
     * Gets all locally persisted articles from the given sites
     * @param sites
     * @return a list of RealmArticles
     */
    public RealmResults getPersistedArticles(ArrayList<SitesE> sites){

        RealmQuery<RealmArticle> query = mRealm.where(RealmArticle.class);

        if(sites.size() > 0)
        {
            query.equalTo("siteName", sites.get(0).getValue());

            if(sites.size() > 1)
            {
                for(int i = 1; i < sites.size(); i++)
                {
                    query.or();
                    query.equalTo("siteName", sites.get(i).getValue());
                }
            }
        }

        RealmResults<RealmArticle> allArticles = query.findAll();
        allArticles.sort("createdAt", false);
        return allArticles;
    }

    /**
     *
     * Returns all locally persisted articles
     * @return
     */
    public RealmResults getPersistedArticles()
    {
        RealmQuery<RealmArticle> query = mRealm.where(RealmArticle.class);
        RealmResults<RealmArticle> allArticles = query.findAll();
        allArticles.sort("createdAt", false);
        return allArticles;
    }

    /**
     *
     * Takes a list of articlesIDs, checks which of them are already persisted
     * and returns a list of the articleIds to fetch and persist
     * @param articleIds
     * @return a list of articleIds to fetch
     */
    private ArrayList<Integer> getArticlesToFetch(List<Integer> articleIds){
        ArrayList<Integer> articlesToFetch = new ArrayList<Integer>();

        Realm realm = Realm.getInstance(mContext);
        for(int articleId : articleIds){
            RealmQuery<RealmArticle> query = realm.where(RealmArticle.class);
            query.equalTo("id", articleId);
            RealmResults<RealmArticle> res = query.findAll();
            realm.close();

            if(res.size() == 0){
                articlesToFetch.add(articleId);
            }
        }
        return articlesToFetch;
    }

    /**
     * Persists all downloaded articles to REALM
     * @param articleList
     */
    public void persistArticles(ArticleList articleList){

        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        for (Article article: articleList.get()) {
            mRealm.beginTransaction();
            RealmArticle rArticle = mRealm.createObject(RealmArticle.class);

            rArticle.setId(Integer.valueOf(article.getId()));

            Date date;
            try {
                date = (Date) mDateFormat.parse(article.getCreatedAt());
            } catch (ParseException e) {
                e.printStackTrace();
                date = new Date(System.currentTimeMillis());
            }
            rArticle.setCreatedAt(date);

            rArticle.setUrl(article.getUrl());
            rArticle.setSiteName(article.getSiteName());
            rArticle.setTitle(article.getTitle());
            rArticle.setPrevText(article.getPrevText());
            rArticle.setThumbUrl(article.getThumbUrl());
            rArticle.setArticleHTML(article.getArticleHTML());

            String fileName = FilenameUtils.getBaseName(article.getThumbUrl()) + "." + FilenameUtils.getExtension(article.getThumbUrl());
            File localImgFile = new  File(mTools.getLocalImgFolder().getAbsolutePath() + "/" + fileName);

            if (localImgFile != null){
                rArticle.setThumbPath(localImgFile.getAbsolutePath());
            }

            mRealm.commitTransaction();
            Log.d(TAG, "Saved article " + article.getId() + " into database");
        }
    }



    public void close(){
        mRealm.close();
    }
}
