package com.rec.photoeditor.share;

import java.io.File;
import java.net.URL;
import java.util.Observer;

import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;

import com.rec.photoeditor.twitter.TwitterAPICredentials;
import com.rec.photoeditor.twitter.TwitterApp;
import com.rec.photoeditor.twitter.TwitterApp.TwitterDialogListener;
import com.rec.photoeditor.twitter.TwitterSession;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class TwitterShare extends RECEditorShare {
	private static final String TAG = "Photo Editor";
	private Context context;
	String pathToImage;
	String message;
	TwitterApp twitterApp;

	public TwitterShare(Context _context, Observer _observer) {
		super(_context, _observer);
		context = _context;

		twitterApp = new TwitterApp(context,
				TwitterAPICredentials.twitter_consumer_key,
				TwitterAPICredentials.twitter_secret_key);

	}

	public void setTwitterAppListner(TwitterDialogListener listner) {

	}

	public void uploadImage(String _pathToImage, String _comment) {
		pathToImage = _pathToImage;
		message = _comment;
		new ImageSender().send();

	}

	public TwitterApp getTwitterApp() {
		return twitterApp;
	}

	private class ImageSender extends AsyncTask<URL, Integer, Long> {
		private String url;

		protected void onPreExecute() {

			notifyObservers(new ShareMessages(
					ShareMessages.TWITTER_IMAGE_IS_SENDING, ""));
		}

		protected Long doInBackground(URL... urls) {
			long result = 0;

			TwitterSession twitterSession = new TwitterSession(context);
			AccessToken accessToken = twitterSession.getAccessToken();

			Configuration conf = new ConfigurationBuilder()
					.setOAuthConsumerKey(
							TwitterAPICredentials.twitter_consumer_key)
					.setOAuthConsumerSecret(
							TwitterAPICredentials.twitter_secret_key)
					.setOAuthAccessToken(accessToken.getToken())
					.setOAuthAccessTokenSecret(accessToken.getTokenSecret())
					.setMediaProvider("twitpic")
					.setMediaProviderAPIKey(
							TwitterAPICredentials.twitpic_api_key).build();

			// OAuthAuthorization auth = new OAuthAuthorization (conf);

			ImageUploadFactory imageUploadFactory = new ImageUploadFactory(conf);

			ImageUpload upload = imageUploadFactory
					.getInstance(MediaProvider.TWITPIC);

			Log.d(TAG, "Start sending image...");

			try {
				url = upload.upload(new File(pathToImage), message);
				result = 1;
				postToTwitter(message + "\n" + url);
				Log.d(TAG, "Image uploaded, Twitpic url is " + url);
			} catch (Exception e) {
				Log.e(TAG, "Failed to send image");
				finished = true;
				e.printStackTrace();
			}

			return result;
		}
		protected Long send() {
			long result = 0;
			
			TwitterSession twitterSession = new TwitterSession(context);
			AccessToken accessToken = twitterSession.getAccessToken();
			
			Configuration conf = new ConfigurationBuilder()
			.setOAuthConsumerKey(
					TwitterAPICredentials.twitter_consumer_key)
					.setOAuthConsumerSecret(
							TwitterAPICredentials.twitter_secret_key)
							.setOAuthAccessToken(accessToken.getToken())
							.setOAuthAccessTokenSecret(accessToken.getTokenSecret())
							.setMediaProvider("twitpic")
							.setMediaProviderAPIKey(
									TwitterAPICredentials.twitpic_api_key).build();
			
			// OAuthAuthorization auth = new OAuthAuthorization (conf);
			
			ImageUploadFactory imageUploadFactory = new ImageUploadFactory(conf);
			
			ImageUpload upload = imageUploadFactory
					.getInstance(MediaProvider.TWITPIC);
			
			Log.d(TAG, "Start sending image...");
			
			try {
				url = upload.upload(new File(pathToImage), message);
				result = 1;
				postToTwitter(message + "\n" + url);
				Log.d(TAG, "Image uploaded, Twitpic url is " + url);
			} catch (Exception e) {
				Log.e(TAG, "Failed to send image");
				finished = true;
				e.printStackTrace();
			}
			
			return result;
		}

		protected void onProgressUpdate(Integer... progress) {
		}

		protected void onPostExecute(Long result) {

			String text = (result == 1) ? "Image sent successfully.\n Twitpic url is: "
					+ url
					: "Failed to send image";

			

			/*
			 * notifyObservers(new ShareMessages(ShareMessages.TWITPIC_STATUS,
			 * text));
			 */
		}
	}

	private void postToTwitter(final String message) {
		int what = 0;

		try {
			twitterApp.updateStatus(message);
		} catch (Exception e) {			
			what = 1;
			finished = true;
		}

		mHandler.sendMessage(mHandler.obtainMessage(what));
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String text = (msg.what == 0) ? "Posted to Twitter"
					: "Post to Twitter failed";
			
			success  = (msg.what == 0) ? true : false;
			
			finished = true;

			notifyObservers(new ShareMessages(ShareMessages.TWITTER_STATUS,
					text));
		}
	};

	public final TwitterDialogListener mTwLoginDialogListener = new TwitterDialogListener() {
		@Override
		public void onComplete(String value) {
			String username = twitterApp.getUsername();
			username = (username.equals("")) ? "No Name" : username;

			notifyObservers(new ShareMessages(
					ShareMessages.TWITTER_LOGIN_SUCESS, username));
		}

		@Override
		public void onError(String value) {
			notifyObservers(new ShareMessages(
					ShareMessages.TWITTER_LOGIN_FAILED, ""));
			finished = true;

		}
	};

}
