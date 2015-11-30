package beatmax.pokerreader.helper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.astuetz.PagerSlidingTabStrip;
import com.github.pwittchen.prefser.library.Prefser;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

import beatmax.pokerreader.R;

/**
 * Created by Max Batt on 02.09.2015.
 */
public class Tools {

    private final Prefser mPrefser;
    private Context mContext;

    public Tools(Context context) {
        mContext = context;
        mPrefser = new Prefser(mContext);
    }

    /**
     * Get the local Folder for saving images
     * @return daraDir
     */
    public File getLocalImgFolder() {
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

    /**
     * A method to find height of the status bar
     * @return the status bar height
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Sends a share intent with article description and url to article
     * @param title
     * @param site
     * @param url
     */
    public void shareArticle(String title, String site, String url){

        site = StringUtils.capitalize(site.split("\\.")[0]);

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, mContext.getString(R.string.share_text, title, site));
        mContext.startActivity(Intent.createChooser(intent, "Share"));
    }

    // Sends a browser intent with the article URL
    public void goToSite(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        mContext.startActivity(i);
    }

    // Copies link to the original article to clipboard
    public void copyTextToClipboard(String text){
        ClipboardManager clipboard = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("poker_reader_url", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(mContext, R.string.toast_link_copied, Toast.LENGTH_SHORT).show();
    }

    // Sets the app theme colors in main activity
    public void setTheme(Toolbar toolbar, PagerSlidingTabStrip tabStrip){

        // Set toolbar color
        setTheme(toolbar);
        int themeColor = Color.parseColor(mPrefser.get(mContext.getString(R.string.pref_key_app_theme), String.class, mContext.getString(R.string.blue_value)));

        tabStrip.setIndicatorColor(themeColor);
        tabStrip.setDividerColor(themeColor);
        tabStrip.setUnderlineColor(themeColor);

    }

    // Sets the app theme colors in articleActivity
    public void setTheme(Toolbar toolbar, RoundCornerProgressBar progressBar){

        // Set toolbar color
        setTheme(toolbar);
        int themeColor = Color.parseColor(mPrefser.get(mContext.getString(R.string.pref_key_app_theme), String.class, mContext.getString(R.string.blue_value)));

        int progressColor = darkenColor(themeColor, 0.8F);

        progressBar.setBackgroundColor(progressColor);
        progressBar.setProgressColor(progressColor);
        progressBar.setSecondaryProgressColor(progressColor);

    }

    // set toolbar color
    public void setTheme(Toolbar toolbar){

        // Set the padding to match the Status Bar height
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        int themeColor = Color.parseColor(mPrefser.get(mContext.getString(R.string.pref_key_app_theme), String.class, mContext.getString(R.string.blue_value)));
        toolbar.setBackgroundColor(themeColor);

    }

    /**
     * Returns darker version of specified <code>color</code>.
     */
    private static int darkenColor (int color, float factor) {
        int a = Color.alpha( color );
        int r = Color.red( color );
        int g = Color.green( color );
        int b = Color.blue( color );

        return Color.argb( a,
                Math.max( (int)(r * factor), 0 ),
                Math.max( (int)(g * factor), 0 ),
                Math.max( (int)(b * factor), 0 ) );
    }
}
