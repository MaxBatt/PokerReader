package beatmax.pokerreader.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import beatmax.pokerreader.R;
import beatmax.pokerreader.models.RealmArticle;
import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Max Batt on 30.07.2015.
 */

@SuppressLint("ValidFragment")
public class ArticleFragment extends Fragment {

    private final static String TAG = ArticleFragment.class.getSimpleName();
    @Bind(R.id.tv)
    TextView mTv;

    private RealmArticle mArticle;


    @SuppressLint("ValidFragment")
    public ArticleFragment(RealmArticle article) {
        mArticle = article;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.article_fragment_layout, container, false);

        mTv.setText(mArticle.getTitle());

        ButterKnife.bind(this, view);


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();



    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Toast.makeText(getActivity(), "Attached", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();


    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
