package beatmax.pokerreader.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import beatmax.pokerreader.R;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Max Batt on 08.09.2015.
 */
public class CustomCardHeader extends CardHeader {

    private TextView mTvSiteName;
    private TextView mTvDate;
    private ImageView mImageView;
    private String mSiteName;
    private String mDate;
    private int mSiteLogo;

    public CustomCardHeader(Context context)
    {
        super(context, R.layout.card_header_layout);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view)
    {
        mTvSiteName = (TextView)view.findViewById(R.id.card_header_sitename);
        if(mTvSiteName != null)
        {
            mTvSiteName.setText(this.mSiteName);
        }

        mTvDate = (TextView)view.findViewById(R.id.card_header_date);
        if(mTvDate != null)
        {
            mTvDate.setText(this.mDate);
        }

        mImageView = (ImageView)view.findViewById(R.id.card_header_sitelogo);
        if(mImageView != null)
        {
            mImageView.setImageResource(this.mSiteLogo);
        }

    }

    public void setSiteName(String siteName)
    {
            mSiteName = siteName;
    }

    public void setDate(String date)
    {
            mDate = date;
    }

    public void setSiteLogo(int id)
    {
        mSiteLogo = id;
    }
}
