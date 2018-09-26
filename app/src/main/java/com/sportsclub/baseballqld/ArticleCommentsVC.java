package com.sportsclub.baseballqld;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sportsclub.baseballqld.models.Article;
import com.sportsclub.baseballqld.models.ArticleComment;
import com.sportsclub.baseballqld.models.Event;
import com.sportsclub.baseballqld.models.Group;
import com.sportsclub.baseballqld.views.TextPoster;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleCommentsVC extends Fragment {

    //some new stuff
    public Article article;
    public ArticleComment articleComment;
    public Group group;

    private ListView listView;
    private ArrayAdapter<Event> listAdapter;
    private SwipeRefreshLayout refreshLayout;
    private TextPoster textPoster;

    public ArticleCommentsVC() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v =inflater.inflate(R.layout.fragment_event_comments_vc, container, false); //SAME AS EVENTS

        textPoster = v.findViewById(R.id.textposter);
        textPoster.setOnSendListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment(textPoster.getText());
            }
        });

        listView = v.findViewById(R.id.list);
        listView.setDividerHeight(0);
        listAdapter= new ArrayAdapter<Event>(this.getContext(), R.layout.main_cell){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.main_cell, parent, false);
                }

                final ArticleComment c = article.comments.get(position);

                TextView firstTV = convertView.findViewById(R.id.firstTV);
                String topString = "<font color='#e2441f'>"+c.author+"</font>";
                firstTV.setText(Html.fromHtml(topString));

                TextView secondTV = convertView.findViewById(R.id.secondTV);
                secondTV.setText(c.articleCommentDescription);

                TextView thirdTV = convertView.findViewById(R.id.thirdTV);
                thirdTV.setText(c.getTimeAgo()+"");

                TextView usernameTV = convertView.findViewById(R.id.usernameTV);
                usernameTV.setText(c.author);

                ImageView userIV = convertView.findViewById(R.id.imageView);
                Picasso p = Picasso.with(this.getContext());
                p.setIndicatorsEnabled(true);
                p.load(c.authorAvatar)
                        .placeholder(R.drawable.icon)
                        //.fetch();
                        .into(userIV);

                return convertView;
            }

            @Override
            public int getCount() {
                return article.comments.size();
            }
        };
        listView.setAdapter(listAdapter);

        refreshLayout = v.findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshArticle();
            }
        });
        return v;
    }

    private void postComment(String text)
    {
        if(text.length() == 0)
        {
            Toast.makeText(this.getActivity(),"You must enter text",Toast.LENGTH_LONG).show();
            return;
        }

        if(group == null)
        {
            Toast.makeText(this.getActivity(),"Could not find group try later",Toast.LENGTH_LONG).show();
            return;
        }

        if(article == null)
        {
            Toast.makeText(this.getActivity(),"Could not find article try later",Toast.LENGTH_LONG).show();
            return;
        }

        DM.hideKeyboard(this.getActivity());

        final ProgressDialog pd = DM.getPD(this.getContext(),"Posting Comment...");
        pd.show();
        /*DM.getApi().postArticleComment(DM.getAuthString(), group.groupId,article.articleId, text, new Callback<ArticleComment>() {
            @Override
            public void success(ArticleComment response, Response response2) {

                Toast.makeText(getContext(),"Comment Posted!",Toast.LENGTH_LONG).show();
                textPoster.clearText();
                refreshArticle();
                pd.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(getContext(),"Comment failed "+error.getMessage(),Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        });
*/
        DM.getApi().postArticleComments(DM.getAuthString(), group.groupId, article.articleId, text, new Callback<ArticleComment>() {
            @Override
            public void success(ArticleComment response, Response response2) {
                Toast.makeText(ArticleCommentsVC.this.getActivity(), "Comment Posted!", Toast.LENGTH_SHORT).show();
                textPoster.clearText();
                refreshArticle();
                pd.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(ArticleCommentsVC.this.getActivity(), "Comment Failed", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void refreshArticle()
    {
        final ProgressDialog pd = DM.getPD(this.getContext(),"Refreshing Article...");
        pd.show();
        DM.getApi().getArticles(DM.getAuthString(), article.articleId, new Callback<Article>() {
            @Override
            public void success(Article a, Response response) {
                article = a;
                listAdapter.notifyDataSetChanged();
                pd.dismiss();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                pd.dismiss();
                refreshLayout.setRefreshing(false);
            }
        });
    }
}
