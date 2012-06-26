package com.rec.photoeditor.share;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.SessionEvents;
import com.facebook.android.SessionStore;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;
import com.google.common.io.ByteStreams;
import com.rec.photoeditor.Platform;

public class FacebookShare extends RECEditorShare {

	private static final String TAG = "Photo Editor";
	public static final int AUTHORIZE_FACEBOOK = 2;
	private static final String FACEBOOK_APP_ID = "384752688250981";
	private Context context;
	private Handler uiThreadHandler;
	private String facebookUserName = "";
	

	// private Activity parentActivity;

	public FacebookShare(Context _context, Observer _observer) {
		super(_context, _observer);
		context = _context;
		uiThreadHandler = new Handler();
	}

	public void handleLoginButtonClick() {

	}

	public void postPhotoOnFacebook(String pathToImage, String messageToSend) {
		if (!Platform.facebook.isSessionValid()) {
			facebookLogIn();
		} else {
			postImage(pathToImage, messageToSend);
		}
	}

	public void facebookLogIn() {
		Log.w("REC Photo Editor", "Log In Facebook");

	}

	public void facebookLogOut() {
		Log.w("REC Photo Editor", "Log Out Facebook");
		SessionEvents.onLogoutBegin();
		AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(
				Platform.facebook);
		asyncRunner.logout(context, new LogoutRequestListener());

	}

	private void postImage(String imagePath, String message) {
		Bundle params = new Bundle();
		try {
			params.putString("caption", message);
			params.putByteArray("photo", ByteStreams
					.toByteArray(new FileInputStream(new File(imagePath))));
			Platform.asyncFbRunner.request("me/photos", params, "POST",
					new PhotoUploadListener(), null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		success=true;
		notifyObservers(new ShareMessages(
				ShareMessages.FACEBOOK_UPLOADING_PHOTO, ""));
	}

	// FB
	public class FbAPIsAuthListener implements AuthListener {

		public void onAuthSucceed() {
			requestUserData();
			SessionStore.save(Platform.facebook, context);
		}

		public void onAuthFail(String error) {
			notifyObservers(new ShareMessages(ShareMessages.FACEBOOK_AUTH_FAIL,
					error));
		}
	}

	// FB
	public void requestUserData() {
		Bundle params = new Bundle();
		params.putString("fields", "name, picture");
		Platform.asyncFbRunner.request("me", params, new UserRequestListener());
	}

	public String getFacebookUserName() {
		return facebookUserName;
	}

	/*
	 * The Callback for notifying the application when log out starts and
	 * finishes.
	 */
	// FB
	public class FbAPIsLogoutListener implements LogoutListener {
		public void onLogoutBegin() {
			Log.w("REC Photo Editor", "Facebook Logging Out...");
		}

		public void onLogoutFinish() {
			SessionStore.clear(context);
			notifyObservers(new ShareMessages(
					ShareMessages.FACEBOOK_LOGOUT_SUCCESS, ""));

		}
	}

	/*
	 * Callback for fetching current user's name, picture, uid.
	 */
	// FB
	public class UserRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(response);

				final String name = jsonObject.getString("name");
				Platform.userUID = jsonObject.getString("id");
				facebookUserName = name;
				notifyObservers(new ShareMessages(
						ShareMessages.FACEBOOK_LOGIN_COMPLEATE, name));

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	// FB
	private class LogoutRequestListener extends BaseRequestListener {
		public void onComplete(String response, final Object state) {
			/*
			 * callback should be run in the original thread, not the background
			 * thread
			 */
			uiThreadHandler.post(new Runnable() {
				public void run() {
					SessionEvents.onLogoutFinish();
				}
			});
		}
	}

	// FB

	public class PhotoUploadListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			finished = true;
			uiThreadHandler.post(new Runnable() {
				public void run() {

					notifyObservers(new ShareMessages(
							ShareMessages.FACEBOOK_UPLOAD_COMPLEATED, ""));
					
				}
			});
		}

		public void onFacebookError(FacebookError error) {
			finished = true;
			notifyObservers(new ShareMessages(
					ShareMessages.FACEBOOK_UPLOAD_FAILED, error.getMessage()));
		}
	}

	public final class FacebookLoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			SessionEvents.onLoginSuccess();

			notifyObservers(new ShareMessages(
					ShareMessages.FACEBOOK_LOGIN_SUCCESS, facebookUserName));
		}

		public void onFacebookError(FacebookError error) {
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onError(DialogError error) {
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onCancel() {
			SessionEvents.onLoginError("Action Canceled");
		}
	}

	public void initFacebookSession() {
		// TODO Auto-generated method stubvoid initFacebookSession() {
		Platform.facebook = new Facebook(FACEBOOK_APP_ID);
		Platform.asyncFbRunner = new AsyncFacebookRunner(Platform.facebook);

		// restore session if one exists
		SessionStore.restore(Platform.facebook, context);

		SessionEvents.addAuthListener(new FbAPIsAuthListener());
		SessionEvents.addLogoutListener(new FbAPIsLogoutListener());

		if (Platform.facebook.isSessionValid()) {
			Log.w("REC Photo Editor", "FACEBOOK SESSION IS VALID");
			requestUserData();
		} else {
			Log.w("REC Photo Editor", "FACEBOOK SESSION IS NOT VALID");
		}

	}

	public void setParentActivity(Activity ownerActivity) {
		// this.parentActivity = ownerActivity;

	}


	
}
