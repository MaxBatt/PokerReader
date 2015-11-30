package beatmax.pokerreader.ui;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import it.gmariotti.cardslib.library.internal.CardThumbnail;

/**
 * Created by Max Batt on 03.09.2015.
 */
public class MyCardThumbnail extends CardThumbnail {

    private String mThumbPath;

    public MyCardThumbnail(Context context, String thumbPath) {
        super(context);
        this.mThumbPath = thumbPath;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View viewImage) {

        ImageView imageView = (ImageView) viewImage ;
        Drawable myDrawable = new BitmapDrawable(mContext.getResources(), mThumbPath);
        imageView.setImageDrawable(myDrawable);

    }
}
