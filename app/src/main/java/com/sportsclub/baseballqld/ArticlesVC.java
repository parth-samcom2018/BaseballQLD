package com.sportsclub.baseballqld;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sportsclub.baseballqld.models.Article;
import com.sportsclub.baseballqld.models.ArticleResponse;
import com.sportsclub.baseballqld.models.Event;
import com.sportsclub.baseballqld.models.Group;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArticlesVC extends Fragment {

    public Article article;
    public Group group;
    public List<Article> articles = new Vector<Article>();

    private ListView listView;
    private ArrayAdapter<Event> listAdapter;
    private SwipeRefreshLayout refreshLayout;
    private ImageView emptyIV;

    public ArticlesVC() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.fragment_articles_vc, container, false);

        emptyIV = v.findViewById(R.id.empty);
        listView = v.findViewById(R.id.list);
        listAdapter= new ArrayAdapter<Event>(this.getContext(), R.layout.article_cell_item){


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.article_cell_item, parent, false);
                }

                final Article a = articles.get(position);
                //final ArticleComment c = article.comments.get(position);

                ImageView iv = convertView.findViewById(R.id.bodyIV);

                if (iv!=null || a.authorAvatar!=null){

                    Picasso.Builder builder = new Picasso.Builder(ArticlesVC.this.getContext());
                    builder.listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            Log.d("hq", "uri: " + uri.getPath());
                            exception.printStackTrace();
                        }
                    });
                    Picasso p = builder.build();
                    try{
                        //p.load(a.authorAvatar).networkPolicy(NetworkPolicy.NO_CACHE).into(iv);

                        //p.load(a.authorAvatar).transform(new RoundedCornersTransform()).into(iv);
                    }
                    catch (IllegalArgumentException e){
                        e.printStackTrace();
                    }
                }

                TextView firstTV = convertView.findViewById(R.id.firstTV);
                firstTV.setText("Added new article "+a.getTimeAgo());

                //comments
                TextView fourthTv = convertView.findViewById(R.id.fourthTV);
                fourthTv.setText(a.getCommentsString());

                TextView thirdTV = convertView.findViewById(R.id.thirdTV);
                thirdTV.setText(a.shortDescription);

                TextView usernameTV = convertView.findViewById(R.id.usernameTV);
                usernameTV.setText(a.author);

                ImageView userIV = convertView.findViewById(R.id.imageView);
                Picasso p = Picasso.with(this.getContext());
                p.setIndicatorsEnabled(true);
                p.load(a.authorAvatar)
                        .placeholder(R.drawable.icon)
                        //.fetch();
                        .into(userIV);


                Button flagButton = convertView.findViewById(R.id.flagButton);
                flagButton.setOnClickListener(DM.getFlagOnClickListener(getActivity()));

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ArticleVC.article = a;
                        ArticleVC.group = group;
                        Intent i = new Intent(ArticlesVC.this.getActivity(), ArticleVC.class);
                        startActivity(i);
                    }
                });

                return convertView;
            }

            @Override
            public int getCount() {
                return articles.size();
            }
        };
        listView.setAdapter(listAdapter);

        refreshLayout = v.findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadData();
            }
        });
        return v;
    }

    // NEEDED with configChange in manifest, stops view changer from recalling onCreateView
    private boolean initialLoaded = false;
    public void loadIfUnloaded()
    {
        if(initialLoaded == false) loadData();
    }

    private void loadData()
    {
        initialLoaded = true;
        final ProgressDialog pd = DM.getPD(this.getContext(),"Loading Articles...");
        pd.show();
        /*DM.getApi().getGroupArticles(DM.getAuthString(), group.groupId, new Callback<List<Article>>() {
            @Override
            public void success(List<Article> as, Response response) {
                articles = as;
                listAdapter.notifyDataSetChanged();
                pd.dismiss();
                refreshLayout.setRefreshing(false);

                if(as.size()==0) emptyIV.setVisibility(View.VISIBLE);
                else emptyIV.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                pd.dismiss();
                refreshLayout.setRefreshing(false);
            }
        });*/

        DM.getApi().getGroupArticlesnew(DM.getAuthString(), group.groupId, new Callback<ArticleResponse>() {
            @Override
            public void success(ArticleResponse as, Response response) {
                articles = as.getData();
                listAdapter.notifyDataSetChanged();
                pd.dismiss();
                refreshLayout.setRefreshing(false);


                if (as.getData().size()==0) emptyIV.setVisibility(View.VISIBLE);
                else emptyIV.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                pd.dismiss();
                refreshLayout.setRefreshing(false);
            }
        });

    }


    private class RoundedCornersTransform implements Transformation {

        public Bitmap getRoundedCornerBitmap(Bitmap bitmap, float r, float v, float r1, float v1) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = 12;

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            float r = size / 4f;

            Bitmap roundedBitmap = getRoundedCornerBitmap(squaredBitmap, r, r, r, r);

            squaredBitmap.recycle();

            return roundedBitmap;
        }

        @Override
        public String key() {
            return "rounded_corners";
        }
    }

}
