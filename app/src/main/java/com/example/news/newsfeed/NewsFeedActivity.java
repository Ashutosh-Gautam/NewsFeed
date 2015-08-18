package com.example.news.newsfeed;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.news.newsfeed.API.api;
import com.example.news.newsfeed.model.FeedItem;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import de.greenrobot.event.EventBus;

public class NewsFeedActivity extends AppCompatActivity {


    private ArrayList<FeedItem> feedList = null;
    private ProgressBar progressbar = null;
    private ListView feedListView = null;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    DatabaseHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_list);

        db = new DatabaseHandler(this);
        EventBus.getDefault().register(this);
        loadNewsFeed();

        progressbar = (ProgressBar) findViewById(R.id.progressBar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Refreshing data on server
                loadNewsFeed();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onEvent(String response){
        try {
            parseJson(new JSONObject(response));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void loadNewsFeed() {
        String url = "http://timesofindia.indiatimes.com";

        //new DownloadFilesTask().execute(url);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(url).build();
        api git = restAdapter.create(api.class);

        git.getFeed(new Callback<Response>() {
            @Override
            public void success(Response detail, Response response) {

                String detailsString = getStringFromRetrofitResponse(detail);
                //Post the user to the default EventBus
                EventBus.getDefault().post(detailsString);
                progressbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("JSON Parser", "Error parsing data " + error);
                ArrayList<FeedItem> feedItemList = db.fetchItem();
                updateList(feedItemList);
                progressbar.setVisibility(View.INVISIBLE);
            }
        });
    }


    public static String getStringFromRetrofitResponse(Response response) {
        //Try to get response body
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {

            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

            String line;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateList(ArrayList<FeedItem> feedList) {

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        feedListView= (ListView) findViewById(R.id.custom_list);
        feedListView.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.GONE);

        feedListView.setAdapter(new CustomListAdapter(this, feedList));
        feedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = feedListView.getItemAtPosition(position);
                FeedItem newsData = (FeedItem) o;

                Intent intent = new Intent(NewsFeedActivity.this, FeedDetailsActivity.class);
                intent.putExtra("feed", newsData);
                startActivity(intent);
            }
        });
    }

    private class DownloadFilesTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Void result) {
            if (null != feedList) {
                updateList(feedList);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];

            // getting JSON string from URL
            JSONObject json = getJSONFromUrl(url);

            //parsing json data
            parseJson(json);
            return null;
        }
    }


    public JSONObject getJSONFromUrl(String url) {
        InputStream is = null;
        JSONObject jObj = null;
        String json = null;

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }

    public void parseJson(JSONObject json) {
        try {

                JSONArray posts = json.getJSONArray("NewsItem");


                db.clearTable();
                feedList = new ArrayList<FeedItem>();

                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = (JSONObject) posts.getJSONObject(i);
                    FeedItem item = new FeedItem();
                    item.setHeadLine(post.getString("HeadLine"));
                    item.setDateLine(post.getString("DateLine"));
                    item.setNewsItemId(post.getString("NewsItemId"));
                    item.setWebURL(post.getString("WebURL"));
                    item.setStory(post.getString("Story"));

                    JSONObject image  = post.getJSONObject("Image");
                    String photo = image.getString("Photo");
                    item.setImage(photo);

                    db.addNewsFeed(item);

                    feedList.add(item);
                }

                if (null != feedList) {
                    updateList(feedList);
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
