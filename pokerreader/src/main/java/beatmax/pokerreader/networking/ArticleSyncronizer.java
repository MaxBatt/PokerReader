package beatmax.pokerreader.networking;

import android.content.Context;
import android.os.Environment;

import com.github.snowdream.android.util.Log;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import beatmax.pokerreader.models.Article;
import beatmax.pokerreader.models.ArticleList;
import beatmax.pokerreader.models.RealmArticle;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit.RestAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Beatmax on 08.08.15.
 */
public class ArticleSyncronizer {

    private final static String TAG = ArticleSyncronizer.class.getSimpleName();

    private Context mContext;

    // Retrofit REST interface
    private RestInterface mApi;

    // Retrofit RestAdapter. Notice LogLevel
    RestAdapter mRestAdapter;

    // Params for the REST call
    private RequestParams mRequestParams;

    private ArticleList mArticleList;



    // TODO: TEMPORARY, we should use an observable here?
    public interface SyncListener{
        void onFinished();
    }

    private ArrayList<SyncListener> mListener;


    public ArticleSyncronizer(Context context) {

        mContext = context;

        // Initialize the rest adapter
        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(RestInterface.BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        // Retrofit API Service for REST Webservice
        mApi = mRestAdapter.create(RestInterface.class);

        // Init articeList
        mArticleList = new ArticleList();

        mListener = new ArrayList<SyncListener>();



    }


    /**
     * Downloads multiple articles with given request params.
     * For every article, the corresponding image is downloaded asynchronously and saved locally on the device
     *
     * @param requestParams
     */
    public void synchronize(RequestParams requestParams){

        mRequestParams = requestParams;

        // Rest api call to get all article ids which fit the given requestParams
        // Because we combine retrofit with RX, the call happens in an own thread
        mApi.getArticleIds(mRequestParams.getSitesParam(), mRequestParams.getAge())
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())

                .subscribe(new Subscriber<ArrayList<Integer>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e);
                    }

                    @Override
                    public void onNext(ArrayList<Integer> articleIds) {

                        Log.d(TAG, "ArticleIds: " + articleIds.toString());

                        // set IDs in requestParams
                        mRequestParams.setIds(articleIds);

                        Realm realm = Realm.getInstance(mContext);
                        // check which articles are already persisted in realm and only fetch unpersisted articles from webservice
                        for(int articleId : articleIds){
                            RealmQuery<RealmArticle> query = realm.where(RealmArticle.class);

                            query.equalTo("id", articleId);
                            RealmResults<RealmArticle> res = query.findAll();

                            android.util.Log.d(TAG, "Persisted Articles:");
                            android.util.Log.d(TAG, res.toString());

                            if(res.size() == 0){
                                mRequestParams.addArticleToFetch(articleId);
                            }

                        }

                        realm.close();


                        if(mRequestParams.getArticlesToFetch().size() == 0){
                            Log.d(TAG, "No articles to fetch. Everything is persisted");
                            for (SyncListener listener: mListener) {
                                listener.onFinished();
                            }
                            return;
                        }

                        Log.d(TAG, "Fetching IDs: " + mRequestParams.getIdsParam());
                        Log.d(TAG, "From Sites: " + mRequestParams.getSitesParam());
                        Log.d(TAG, "Fetching " + articleIds.size() +  " articles...");


                        // Rest api call to Get the list of all articles.
                        // This happens in an own thread
                        mApi.getArticles(mRequestParams.getSitesParam(), mRequestParams.getArticlesToFetchParam())
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(Schedulers.newThread())
                                .subscribe(new Subscriber<ArticleList>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e(TAG, e);
                                    }

                                    @Override
                                    public void onNext(ArticleList articleList) {

                                        Log.d(TAG, "Done!");
                                        mArticleList = articleList;

                                        // Downloading images for all articles
                                        // We transform the articleList into an observable
                                        Observable
                                                .from(articleList.get())
                                                        // Now we transform every article into an observable,
                                                        // so that we can download the image for every article in an own thread
                                                        // All observabes will be flatmapped, so that we can be noticed if all images are downloaded
                                                .flatMap(article ->
                                                                Observable
                                                                        .just(article)
                                                                        .doOnNext(new Action1<Article>() {
                                                                            @Override
                                                                            public void call(Article article) {
                                                                                Log.d(TAG, "Downloading image: " + article.getThumbUrl());

                                                                                // Download image from given url and save it locally on the device
                                                                                File imgFile = downloadImage(article.getThumbUrl());

                                                                                // if the image is available save local image path to the realm article model
                                                                                if (imgFile != null) {
                                                                                    Log.d(TAG, "Saved image to " + imgFile.getAbsolutePath());

                                                                                } else {
                                                                                    Log.d(TAG, "No image file  available");
                                                                                }
                                                                            }

                                                                        })
                                                                        .subscribeOn(Schedulers.io())
                                                )
                                                .doOnError(t -> Log.d(TAG, t))
                                                .subscribe(new Subscriber<Article>() {

                                                    // All articles including images are downloaded and the merged observable fires
                                                    @Override
                                                    public void onCompleted() {
                                                        Log.d(TAG, "FINISHED SYNCHRONIZING");

                                                        persistArticles();
                                                        for (SyncListener listener: mListener) {
                                                            listener.onFinished();
                                                        }

                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {
                                                        Log.e(TAG, e);
                                                    }

                                                    @Override
                                                    public void onNext(Article article) {

                                                    }
                                                });
                                    }
                                });

                    }
                });

    }


    /**
     * Downloads an image from a given URL and saves it to the local storage
     * @param sUrl
     * @return a File object of the locally saved image
     */
    private File downloadImage(String sUrl){

        // Get filename out of image url
        String fileName = FilenameUtils.getBaseName(sUrl) + "." + FilenameUtils.getExtension(sUrl);

        URL url = null;
        try {
            url = new URL(sUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        try {
            URLConnection connection = url.openConnection();
            InputStream inputStream = new BufferedInputStream(url.openStream(), 10240);
            File localImgFolder = getLocalImgFolder();
            File imgFile = new File(localImgFolder, fileName);
            FileOutputStream outputStream = new FileOutputStream(imgFile);

            byte buffer[] = new byte[1024];
            int dataSize;
            int loadedSize = 0;
            while ((dataSize = inputStream.read(buffer)) != -1) {
                loadedSize += dataSize;
                outputStream.write(buffer, 0, dataSize);
            }

            outputStream.close();

            return imgFile;


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }



    public RealmResults getPersistedArticles(){
        Realm realm = Realm.getInstance(mContext);
        RealmQuery<RealmArticle> findAllQuery = realm.where(RealmArticle.class);
        RealmResults<RealmArticle> allArticles = findAllQuery.findAll();
        return allArticles;
    }


    /**
     * Persists all downloaded articles to REALM
     */
    private void persistArticles(){

        Realm realm = Realm.getInstance(mContext);
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        for (Article article: mArticleList.get()) {
            realm.beginTransaction();
            RealmArticle rArticle = realm.createObject(RealmArticle.class);

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
            File localImgFile = new  File(getLocalImgFolder().getAbsolutePath() + "/" + fileName);

            if (localImgFile != null){
                rArticle.setThumbPath(localImgFile.getAbsolutePath());
            }

            realm.commitTransaction();
            Log.d(TAG, "Saved article " + article.getId() + " into database");
        }

        realm.close();
    }


    /**
     * Get the local Folder for saving images
     * @return daraDir
     */
    private File getLocalImgFolder() {
        File dataDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dataDir = new File(Environment.getExternalStorageDirectory(), "myappdata");
            if(!dataDir.isDirectory()) {
                dataDir.mkdirs();
            }
        }

        if(!dataDir.isDirectory()) {
            dataDir = mContext.getFilesDir();
        }

        return dataDir;
    }

    private void fetchArticles(){

    }

    public void setRequestParams(RequestParams requestParams) {
        mRequestParams = requestParams;
    }

    public void addListener(SyncListener listener){
        if(!mListener.contains(listener)){
            mListener.add(listener);
        }
    }

    public void removeListener(SyncListener listener){
        if(mListener.contains(listener)){
            mListener.remove(listener);
        }
    }

    public void close(){

    }


}
