package beatmax.pokerreader.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.StringUtils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import beatmax.pokerreader.BackgroundService;
import beatmax.pokerreader.R;
import beatmax.pokerreader.db.DatabaseManager;
import beatmax.pokerreader.helper.Tools;
import beatmax.pokerreader.models.RealmArticle;
import beatmax.pokerreader.models.SitesE;
import beatmax.pokerreader.prefs.PrefManager;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

/**
 * Created by Max Batt on 09.09.2015.
 */
public class ArticleListFragment extends Fragment {

    @Bind(R.id.swype_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.carddemo_recyclerview)
    CardRecyclerView mRecyclerView;

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private SitesE mSite;

    private BackgroundService mService;

    private DatabaseManager mDatabaseManager;
    private PrefManager mPrefManager;
    private Tools mTools;

    public static ArticleListFragment newInstance(SitesE site) {
        ArticleListFragment fragment = new ArticleListFragment();
        fragment.setSite(site);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mService = ((MainActivity) getActivity()).getService();
        mDatabaseManager = new DatabaseManager(getActivity());
        mPrefManager = new PrefManager(getActivity());
        mTools = new Tools(getActivity());
    }

    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_article_list, container, false);
        ButterKnife.bind(this, view);

        refreshList();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity m = (MainActivity) getActivity();
                m.synchronizeArticles();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mPrefManager.isAppCleared()){
            refreshList();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setSite(SitesE site) {
        mSite = site;
    }

    private void refreshList() {
        ArrayList<SitesE> sitesToSync = new ArrayList<SitesE>();
        sitesToSync.add(mSite);

        RealmResults<RealmArticle> articles = mDatabaseManager.getPersistedArticles(sitesToSync);

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
            header.setSiteName(StringUtils.capitalize(article.getSiteName()));
            header.setDate(formatter.format(article.getCreatedAt()));

            // Header Logo
            if(article.getSiteName().equals(SitesE.POKERSTRATEGY.getValue())){
                header.setSiteLogo(R.drawable.logo_pokerstrategy);
            }
            else if(article.getSiteName().equals(SitesE.POKERFIRMA.getValue())){
                header.setSiteLogo(R.drawable.logo_pokerfirma);
            }
            else if(article.getSiteName().equals(SitesE.POKEROLYMP.getValue())){
                header.setSiteLogo(R.drawable.logo_pokerolymp);
            }
            else if(article.getSiteName().equals(SitesE.POKERNEWS.getValue())){
                header.setSiteLogo(R.drawable.logo_pokernews);
            }

//            //Add a popup menu. This method sets OverFlow button to visibile
            header.setPopupMenu(R.menu.menu_article, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                @Override
                public void onMenuItemClick(BaseCard card, MenuItem item) {

                    switch(item.getItemId()){

                        // Share article
                        case R.id.article_menu_share:
                            mTools.shareArticle(article.getTitle(), article.getSiteName(), article.getUrl());
                            break;

                        // Go to original article site
                        case R.id.article_menu_go_to_site:
                            mTools.goToSite(article.getUrl());
                            break;

                        // Copy article link into clipboard
                        case R.id.article_menu_copy_link:
                            mTools.copyTextToClipboard(article.getUrl());
                            break;
                    }
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

    }

}