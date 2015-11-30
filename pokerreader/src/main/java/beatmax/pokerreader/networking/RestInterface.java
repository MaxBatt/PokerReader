package beatmax.pokerreader.networking;

import java.util.ArrayList;

import beatmax.pokerreader.models.ArticleList;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Max Batt on 27.06.15.
 */
public interface RestInterface {

        public static final String BASE_URL = "http://www.pokercrawler.maxbatt.de";

        // GET Request articleIds
        @GET("/article_ids")
        Observable<ArrayList<Integer>> getArticleIds(@Query("sites") String sites, @Query("age") int age);

        // Get Request for Articles
        @GET("/articles")
        Observable<ArticleList> getArticles(@Query("sites") String sites, @Query("ids") String ids);



//        // GET Request for all rides. Returns a List of PedelecRide objects
//        @GET("/rides")
//        void getRides(Callback<List<PedelecRide>> cb);
//
//
//        // POST Request for uploading CSV file and saving ride into DB
//        // Returns the created ride as PedelecRide object
//        // Notice that multipart-formdata is set in the request header. This is necessary when uploading files
//        @Multipart
//        @POST("/ride/new")
//        void createRide(@Part("rideName") String rideName, @Part("userId") int UserId, @Part("csvFile") TypedFile file, Callback<PedelecRide> cb);
}
