package com.example.news.newsfeed.API;

import com.example.news.newsfeed.model.FeedItem;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Ashutosh on 13/08/15.
 */
public interface api {

    @GET("/feeds/newsdefaultfeeds.cms?feedtype=sjson")
    public void getFeed(Callback<Response> response);
}
