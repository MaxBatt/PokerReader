package beatmax.pokerreader.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import beatmax.pokerreader.R;
import beatmax.pokerreader.helper.Tools;
import beatmax.pokerreader.models.RealmArticle;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;

public class ArticleActivity extends AppCompatActivity {


    @Bind(R.id.webView)
    WebView mWebView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.progressBar)
    RoundCornerProgressBar mProgressBar;
    private Tools mTools;
    private RealmArticle mArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        ButterKnife.bind(this);

        mTools = new Tools(this);

        setSupportActionBar(mToolbar);

        // Up Navigation
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTools.setTheme(mToolbar, mProgressBar);

        mProgressBar.setRadius(0);





        Intent i = getIntent();

        Bundle bundle = i.getExtras();

        if (bundle == null) {
            return;
        }

        int articleId = bundle.getInt("articleId");

        Realm realm = Realm.getInstance(this);

        RealmQuery<RealmArticle> query = realm.where(RealmArticle.class);
        query.equalTo("id", articleId);

        mArticle = query.findFirst();

        getSupportActionBar().setTitle(mArticle.getTitle());
        getSupportActionBar().setSubtitle(mArticle.getSiteName());


        mWebView.setWebChromeClient(new MyWebChromeClient());

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);

        mWebView.loadDataWithBaseURL(mArticle.getUrl(), mArticle.getArticleHTML(), "text/html", "UTF-8", null);

    }

    // A small WebChromeClient showing the progressbar while site loads and dismisses it when site is loaded
    private class MyWebChromeClient extends WebChromeClient{

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setProgress(newProgress);

            if(newProgress == 100){
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            // Back button
            case android.R.id.home:
                finish();
                return true;

            // Share article
            case R.id.article_menu_share:
                mTools.shareArticle(mArticle.getTitle(), mArticle.getSiteName(), mArticle.getUrl());
                break;

            // Go to original article site
            case R.id.article_menu_go_to_site:
                mTools.goToSite(mArticle.getUrl());
                break;

                // Copy article link into clipboard
            case R.id.article_menu_copy_link:
                mTools.copyTextToClipboard(mArticle.getUrl());
                break;


        }

        return super.onOptionsItemSelected(item);
    }
}
