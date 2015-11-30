package beatmax.pokerreader;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import beatmax.pokerreader.models.RealmArticle;
import beatmax.pokerreader.networking.ArticleSyncronizer;
import beatmax.pokerreader.ui.NavigationActivity;

/**
 * Created by Max Batt on 02.09.2015.
 */
public class BackgroundService extends Service
{

    // Debug
    private final static String TAG = BackgroundService.class.getSimpleName();

    private final static int START_FOREGROUND_ID = 1;

    IBinder mBinder = new Binder();
    private ArticleSyncronizer mSyncronizer;

    private RealmArticle mCurrentArticle;


    public class Binder extends android.os.Binder
    {
        public BackgroundService getService()
        {
            return BackgroundService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mSyncronizer = new ArticleSyncronizer(this);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        mSyncronizer.close();
    }

    // Wakelock stuff
    public void startForeground() {



            // pending intent to return to the activity when the foreground notification is clicked
            Intent activityIntent = new Intent(BackgroundService.this, NavigationActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(BackgroundService.this, 1, activityIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            // create a foreground notification
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(BackgroundService.this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(getResources().getString(R.string.app_name))
                            .setContentText(getResources().getString(R.string.notification_background_service))
                            .setContentIntent(pendingIntent);
            // The service is starting, due to a call to startService()
            startForeground(START_FOREGROUND_ID, mBuilder.build());


    }

    public void stopForeground() {
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    public ArticleSyncronizer getSyncronizer() {
        return mSyncronizer;
    }

    public RealmArticle getCurrentArticle() {
        return mCurrentArticle;
    }

    public void setCurrentArticle(RealmArticle currentArticle) {
        mCurrentArticle = currentArticle;
    }
}
