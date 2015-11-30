package beatmax.pokerreader.ui;

import android.content.Context;

import beatmax.pokerreader.models.RealmArticle;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Max Batt on 02.09.2015.
 */
public class ArticleCard extends Card {

    private RealmArticle mArticle;


    public ArticleCard(Context context) {
        super(context);
    }

    public ArticleCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public RealmArticle getArticle() {
        return mArticle;
    }

    public void setArticle(RealmArticle article) {
        this.mArticle = article;
    }


}
