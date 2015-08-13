package com.example.news.newsfeed.model;

import java.io.Serializable;

public class FeedItem implements Serializable {

	private String HeadLine;
	private String DateLine;
	private String Photo;
	private String NewsItemId;
	private String Story;
	private String WebURL;

	public String getHeadLine() {
		return HeadLine;
	}

	public void setHeadLine(String headLine) {
		HeadLine = headLine;
	}

	public String getDateLine() {
		return DateLine;
	}

	public void setDateLine(String dateLine) {
		DateLine = dateLine;
	}

	public String getImage() {
		return Photo;
	}

	public void setImage(String image) {
		Photo = image;
	}

	public String getNewsItemId() {
		return NewsItemId;
	}

	public void setNewsItemId(String newsItemId) {
		NewsItemId = newsItemId;
	}

	public String getStory() {
		return Story;
	}

	public void setStory(String story) {
		Story = story;
	}

	public String getWebURL() {
		return WebURL;
	}

	public void setWebURL(String webURL) {
		WebURL = webURL;
	}
}
