package beatmax.pokerreader.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.pwittchen.prefser.library.Prefser;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import beatmax.pokerreader.BackgroundService;
import beatmax.pokerreader.R;
import beatmax.pokerreader.db.DatabaseManager;
import beatmax.pokerreader.models.RealmArticle;
import beatmax.pokerreader.models.SitesE;
import beatmax.pokerreader.networking.ArticleSyncronizer;
import beatmax.pokerreader.networking.RequestParams;
import beatmax.pokerreader.prefs.PrefManager;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

@Deprecated
public class ArticleListFragmentDeprecated extends Fragment{


    @Bind(R.id.carddemo_recyclerview) CardRecyclerView mRecyclerView;
    private ArticleSyncronizer mSyncronizer;
    private BackgroundService mService;
    private DatabaseManager mDatabaseManager;
    private Prefser mPrefser;
    private ProgressDialog mDialog;
    private Subscription mSubscription;
    private PrefManager mPrefManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDialog = new ProgressDialog(getActivity());
        mDialog.setTitle(getString(R.string.sync_dialog_title));
        mDialog.setMessage(getString(R.string.sync_dialog_message));

        mService = ((NavigationActivity) getActivity()).getService();
        mSyncronizer = mService.getSyncronizer();
        mDatabaseManager = new DatabaseManager(getActivity());
        mPrefser = new Prefser(getActivity());
        mPrefManager = new PrefManager(getActivity());

        synchronizeArticles();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_article_list, container, false);
        ButterKnife.bind(this, view);


        mSyncronizer.addListener(new ArticleSyncronizer.SyncListener() {
            @Override
            public void onFinished() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        refreshList();

                    }
                });
            }
        });

        return  view;

    }

    @Override
    public void onResume() {
        super.onResume();

        mSubscription = mPrefser.observePreferences()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String key) {
                        if(key.equals(SitesE.POKERSTRATEGY.getValue()) || key.equals(SitesE.POKERFIRMA.getValue())) {
                            synchronizeArticles();
                        }
                    }
                });


    }

    @Override
    public void onPause() {
        super.onPause();
        mSubscription.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mDatabaseManager.close();
    }



    protected class BackgroundServiceConnection implements ServiceConnection {

        @SuppressLint("NewApi")
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mService = ((BackgroundService.Binder) binder).getService();
            mSyncronizer = mService.getSyncronizer();


        }


        // called when the service crashed or is killed
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private void synchronizeArticles()
    {
        mDialog.show();

        // Params for the Sync
        RequestParams params = new RequestParams();
        // Get sites to sync from Shared Prefs
        params.setSytesToSync(mPrefManager.getSitesToSync());

        mSyncronizer.synchronize(params);
    }


    private void refreshList(){


        RealmResults<RealmArticle> articles = mDatabaseManager.getPersistedArticles(mPrefManager.getSitesToSync());

        ArrayList<Card> cards = new ArrayList<Card>();


        for (RealmArticle article : articles) {

            //Create a Card
            ArticleCard card = new ArticleCard(getActivity());

            card.setArticle(article);

//            card.setTitle(article.getSiteName() + "\n" + article.getCreatedAt());
            card.setTitle(article.getTitle());

            card.setShadow(true);

            //Create thumbnail
            MyCardThumbnail thumb = new MyCardThumbnail(getActivity(), article.getThumbPath());

            thumb.setExternalUsage(true);

            //Add thumbnail to a card
            card.addCardThumbnail(thumb);

            //Create a CardHeader
            // CardHeader header = new CardHeader(getActivity());
            CustomCardHeader header = new CustomCardHeader(getActivity());

            //Set the header siteName and date
            Format formatter = new SimpleDateFormat("dd.MM.yyyy");
            header.setSiteName(upperCaseFirst(article.getSiteName()));
            header.setDate(formatter.format(article.getCreatedAt()));

            // Header Logo
            if(article.getSiteName().equals(SitesE.POKERSTRATEGY.getValue())){
                header.setSiteLogo(R.drawable.logo_pokerstrategy);
            }
            else if(article.getSiteName().equals(SitesE.POKERFIRMA.getValue())){
                header.setSiteLogo(R.drawable.logo_pokerfirma);
            }

//            header.setTitle(upperCaseFirst(article.getSiteName()) + " | " + formatter.format(article.getCreatedAt()));

//
//
//            //Add a popup menu. This method sets OverFlow button to visibile
            header.setPopupMenu(R.menu.menu_main, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                @Override
                public void onMenuItemClick(BaseCard card, MenuItem item) {
                    Toast.makeText(getActivity(), "Click on " + item.getTitle(), Toast.LENGTH_SHORT).show();
                }
            });

            //Add Header to card
            card.addCardHeader(header);


            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    ArticleCard aCard = (ArticleCard) card;

                    mService.setCurrentArticle(aCard.getArticle());

                    Bundle bundle = new Bundle();
                    bundle.putInt("articleId", aCard.getArticle().getId());

                    Intent i = new Intent(getActivity(), ArticleActivity.class);
                    i.putExtras(bundle);

                    startActivity(i);


                }
            });

           cards.add(card);


        }

        CardArrayRecyclerViewAdapter mCardArrayAdapter = new CardArrayRecyclerViewAdapter(getActivity(), cards);

        //Staggered grid view
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Set the empty view
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCardArrayAdapter);
        }

        mDialog.dismiss();
    }

    public BackgroundService getService() {
        return mService;
    }

    // Listener that checks if SitePreferences have changed
//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if(key.equals(getString(R.string.pref_key_site_pokerstrategy)) || key.equals(getString(R.string.pref_key_site_pokerfirma))){
//            refreshList();
//        }
//    }




    // uppercases the first letter of a string
    private static String upperCaseFirst(String value) {

        // Convert String to char array.
        char[] array = value.toCharArray();
        // Modify first element in array.
        array[0] = Character.toUpperCase(array[0]);
        // Return string.
        return new String(array);
    }

}

