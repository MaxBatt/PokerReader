package beatmax.pokerreader.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import beatmax.pokerreader.R;
import beatmax.pokerreader.db.DatabaseManager;
import beatmax.pokerreader.models.RealmArticle;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmResults;

public class DbDebugActivity extends AppCompatActivity {

    @Bind(R.id.textView)
    TextView mTextView;
    private DatabaseManager mDatabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_debug);
        ButterKnife.bind(this);

        mDatabaseManager = new DatabaseManager(this);

        RealmResults<RealmArticle> articles = mDatabaseManager.getPersistedArticles();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        mTextView.append(articles.size() + " articles in Database + \n\n");

        for (RealmArticle article: articles) {
            String line = article.getId() + " | " + article.getCreatedAt() + " | " + article.getTitle() + " | " + article.getSiteName() + "\n\n";
            mTextView.append(line);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_db_debug, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
