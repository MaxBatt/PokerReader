package beatmax.pokerreader.debug;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.github.snowdream.android.util.Log;
import com.orhanobut.bee.BeeConfig;
import com.orhanobut.bee.BeeLog;
import com.orhanobut.bee.widgets.Button;
import com.orhanobut.bee.widgets.Title;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import beatmax.pokerreader.ui.DbDebugActivity;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by fabdeuch on 13.07.2015.
 * <p/>
 * config for the bee debug menu in the MainMenuActivity
 */
public class MainBeeConfig extends BeeConfig
{

    private final static String LOGFILE_NAME = "pokerreader.log";

    private final String TAG = this.getClass().getName();
    private final ExecutorService mExecutorService;

    public MainBeeConfig()
    {
        mExecutorService = Executors.newSingleThreadExecutor();


        //Log.addLogFilter(new LogFilter.LevelFilter(Log.LEVEL.DEBUG));
    }

    /**
     * Add extra information by using content object.
     */
    @Override
    public void onInfoContentCreated(Map<String, String> content)
    {
    }

    /**
     * It is called when the close button is pressed
     */
    @Override
    public void onClose()
    {
        super.onClose();
    }


    @Title("Log cat")
    @Button
    public void onLogCatClicked()
    {
        DebugUtil.startLogCatActivity(getContext());
    }


    @Title("LogCat to BeeLog")
    @Button
    public void onLogCatToBeeLogClicked()
    {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Process process = Runtime.getRuntime().exec("logcat -d");
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));

                    StringBuilder log = new StringBuilder();
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        BeeLog.d("", line);
                    }

                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            }
        });

    }

    @Title("Show Database")
    @Button
    public void showDatabase()
    {
        getContext().startActivity(new Intent(getContext(), DbDebugActivity.class));
    }





    // copy the log file from internal to external storage and share it via intent
    @Title("Share Log File")
    @Button
    public void onShareClicked()
    {
        final File destinationFile = new File(Environment.getExternalStorageDirectory().toString(), LOGFILE_NAME);


        Observable.create(new OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                try {
                    // copy the log file from internal to external storage
                    copy(new File(getContext().getFilesDir(), "app.log"), destinationFile);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }

            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Subscriber<Object>()
        {
            @Override
            public void onCompleted()
            {
                Toast.makeText(getContext(), "Log file successfully copied to external storage directory", Toast.LENGTH_SHORT).show();

                // share the file to er apps
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(destinationFile));
                sendIntent.setType("text/plain");
                getContext().startActivity(sendIntent);
            }

            @Override
            public void onError(Throwable e)
            {
                Log.e(TAG, "", e);
                Toast.makeText(getContext(), "Error Copying log file. There might be no log data at the moment. ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Object o)
            {

            }
        });

    }


    // simple file copy util
    public void copy(File src, File dst) throws IOException
    {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }




    //    /**
//     * A sample checkbox implementation
//     */
//    @Title("Show splash screen")
//    @CheckBox
//    public void onShowSplashChecked(boolean isChecked)
//    {
//        Log.d(TAG, "onShowSplashChecked");
//    /**
//     * A sample spinner implementation
//     */
//    @Title("End Point")
//    @Spinner({"Staging", "Live", "Mock"})
//    public void onEndPointSelected(String selectedValue)
//    {
//        Log.d(TAG, "onEndPointSelected");
//    }

}
