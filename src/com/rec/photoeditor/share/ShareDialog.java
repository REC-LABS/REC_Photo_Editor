package com.rec.photoeditor.share;

import java.util.Observable;
import java.util.Observer;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.rec.photoeditor.Platform;
import com.rec.photoeditor.R;
import com.rec.photoeditor.twitter.TwitterApp;

public class ShareDialog extends Dialog implements
		android.view.View.OnClickListener, OnCheckedChangeListener {

	private static final String TAG = "Photo Editor";

	private Button facebookButton;
	private Button twitterButton;
	private Button picasaButton;
	private Button postButton;

	private CheckBox facebookCB;
	private CheckBox twitterCB;
	private CheckBox picasaCB;
	private EditText message;
	private Context context;
	private Activity parentActivity;

	private TwitterApp twitterApp;
	private String pathToImage;
	private PicasaShare picasaShare;
	private TwitterShare twitterShare;
	private FacebookShare facebookShare;
	private ShareComponentChangeHandler shareComponentChangeHandler;

	public ShareDialog(Context _context, Activity _parentActivity,
			String _pathToImage) {
		super(_context);
		context = _context;
		pathToImage = _pathToImage;
		parentActivity = _parentActivity;
		shareComponentChangeHandler = new ShareComponentChangeHandler();

		initComponents();
		initSocialMedia();

	}

	private void initComponents() {
		setContentView(R.layout.share_dialog);
		setTitle("Share Photo");
		// Dialog class set layout as wrap_content automatically, so i need to
		// correct it
		this.getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		facebookButton = (Button) findViewById(R.id.facebook_log_in);
		facebookButton.setOnClickListener(this);
		twitterButton = (Button) findViewById(R.id.twitter_log_in);
		twitterButton.setOnClickListener(this);
		picasaButton = (Button) findViewById(R.id.gplus_log_in);
		picasaButton.setOnClickListener(this);
		postButton = (Button) findViewById(R.id.post_button);
		postButton.setOnClickListener(this);

		facebookCB = (CheckBox) findViewById(R.id.facebook_check_box);
		facebookCB.setOnCheckedChangeListener(this);
		twitterCB = (CheckBox) findViewById(R.id.twitter_check_box);
		twitterCB.setOnCheckedChangeListener(this);
		picasaCB = (CheckBox) findViewById(R.id.gplus_check_box);
		picasaCB.setOnCheckedChangeListener(this);

		message = (EditText) findViewById(R.id.description_edit_text);
	}

	private void initSocialMedia() {
		initFacebook(shareComponentChangeHandler);
		initPicasa(shareComponentChangeHandler);
		initTwitter(shareComponentChangeHandler);

	}

	private void initTwitter(ShareComponentChangeHandler changeHandler) {
		twitterShare = new TwitterShare(context, changeHandler);
		twitterApp = twitterShare.getTwitterApp();
		twitterApp.setListener(twitterShare.mTwLoginDialogListener);
		validateTwitterSession();
	}

	private void initPicasa(ShareComponentChangeHandler changeHandler) {
		picasaShare = new PicasaShare(context, changeHandler);
		validateGoogleSession();
	}

	private void initFacebook(ShareComponentChangeHandler changeHandler) {
		facebookShare = new FacebookShare(context, changeHandler);
		facebookShare.setParentActivity(parentActivity);
		validateFacebookSession();
	}

	private void validateTwitterSession() {
		if (twitterApp.hasAccessToken()) {
			twitterCB.setChecked(true);

			String username = twitterApp.getUsername();
			username = (username.equals("")) ? getContext().getString(
					R.string.unknown) : username;
			twitterCB.setText(username);
			twitterButton.setText(getContext().getString(R.string.log_out));
		}
	}

	private void validateGoogleSession() {
		AccountManager manager = AccountManager.get(context);
		final Account[] accounts = manager.getAccountsByType("com.google");
		final int size = accounts.length;
		if (size > 0) {
			picasaShare.gotAccount(manager, accounts[0]);
			picasaShare.setGoogleUsername(accounts[0].name);
			picasaCB.setChecked(true);
			picasaCB.setText(accounts[0].name);
			picasaButton.setText(getContext()
					.getString(R.string.change_account));
		}

	}

	private void validateFacebookSession() {
		facebookShare.initFacebookSession();

		if (Platform.facebook.isSessionValid()) {
			facebookCB.setChecked(true);
			facebookCB.setText(facebookShare.getFacebookUserName());
			facebookButton.setText(getContext().getString(R.string.log_out));

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.facebook_log_in:
			onFacebookButtonClick();
			break;
		case R.id.twitter_log_in:
			onTwitterButtonClick();
			break;
		case R.id.gplus_log_in:
			onPicasaButtonClick();
			break;
		case R.id.post_button:
			onPostButtonClick();
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonChanged, boolean state) {
		switch (buttonChanged.getId()) {
		case R.id.facebook_check_box:
			handleFacbookCheckChanged(state);
			break;
		case R.id.twitter_check_box:
			handleTwitterCheckChanged(state);
			break;
		case R.id.gplus_check_box:
			handlePicasaCheckChanged(state);
			break;
		default:
			break;
		}

	}

	private void handlePicasaCheckChanged(boolean state) {
		if (state
				&& (picasaShare.getGoogleUsername() == null || ""
						.equals(picasaShare.getGoogleUsername()))) {
			Toast.makeText(
					context,
					getContext().getString(
							R.string.please_choose_to_google_account_first),
					Toast.LENGTH_LONG).show();
			picasaCB.setChecked(false);
		}

	}

	private void handleTwitterCheckChanged(boolean state) {
		if (state && !twitterApp.hasAccessToken()) {
			Toast.makeText(
					context,
					getContext().getString(
							R.string.please_login_to_twitter_first),
					Toast.LENGTH_LONG).show();
			twitterCB.setChecked(false);

		}
	}

	private void handleFacbookCheckChanged(boolean state) {
		if (state && !Platform.facebook.isSessionValid()) {
			Toast.makeText(
					context,
					getContext().getString(
							R.string.please_login_to_facebook_first),
					Toast.LENGTH_LONG).show();
			facebookCB.setChecked(false);
		}

	}

	private void onFacebookButtonClick() {
		handleFacebookButtonClick();

	}

	private void handleFacebookButtonClick() {
		if (Platform.facebook.isSessionValid()) {
			facebookShare.facebookLogOut();
			facebookCB.setChecked(false);
			facebookCB.setText(getContext().getString(R.string.facebook));
			facebookButton.setText(getContext().getString(R.string.log_in));

		} else {

			facebookShare.facebookLogIn();
			Platform.facebook.authorize(parentActivity,
					Platform.facebookPermissions,
					FacebookShare.AUTHORIZE_FACEBOOK,
					facebookShare.new FacebookLoginDialogListener());

		}
	}

	private void onTwitterButtonClick() {
		handleOnTwitterButtonClick();

	}

	private void onPicasaButtonClick() {
		createChooseGmailAccountDialog().show();

	}

	private void handleOnTwitterButtonClick() {
		if (twitterApp.hasAccessToken()) {

			twitterApp.resetAccessToken(context);
			twitterCB.setChecked(false);
			twitterCB.setText(getContext().getString(R.string.twitter));
			twitterButton.setText(getContext().getString(R.string.log_in));

		} else {
			twitterApp.authorize();
		}
	}

	private Dialog createChooseGmailAccountDialog() {
		// context.showDialog(0);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(getContext().getString(
				R.string.select_a_google_account));
		final AccountManager manager = AccountManager.get(context);
		final Account[] accounts = manager.getAccountsByType("com.google");
		final int size = accounts.length;
		String[] names = new String[size];
		for (int i = 0; i < size; i++) {
			names[i] = accounts[i].name;
		}
		builder.setItems(names, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				picasaShare.gotAccount(manager, accounts[which]);
				picasaCB.setChecked(true);
				picasaCB.setText(accounts[which].name);

			}
		});
		return builder.create();

	}

	private void onPostButtonClick() {

		ShareHandler sh = new ShareHandler();
		sh.execute(new String[0]);

	}

	private void handlePostButtonClick() {

		Thread picassaThread = new Thread(new Runnable() {

			public void run() {

				if (picasaCB.isChecked()) {
					picasaShare.setFinished(false);
					picasaShare.uploadImage(pathToImage, message.getText()
							.toString());
				}
			}
		});
		picassaThread.start();

		Thread facebookThread = new Thread(new Runnable() {

			public void run() {

				if (facebookCB.isChecked()) {
					facebookShare.setFinished(false);
					facebookShare.postPhotoOnFacebook(pathToImage, message
							.getText().toString());

				}
			}
		});
		facebookThread.start();

		Thread twitterThread = new Thread(new Runnable() {

			public void run() {

				if (twitterCB.isChecked()) {
					twitterShare.setFinished(false);
					twitterShare.uploadImage(pathToImage, message.getText()
							.toString());

				}
			}
		});
		twitterThread.start();

		try {
			picassaThread.join(60000);
			facebookThread.join(30000);
			twitterThread.join(30000);
		} catch (InterruptedException e1) {
			Log.i(TAG, "problem with sending image" + e1.getLocalizedMessage());
			e1.printStackTrace();
		}

		Log.i(TAG, "facebookShare.isFinished()" + facebookShare.isFinished());
		Log.i(TAG, "twitterShare.isFinished()" + twitterShare.isFinished());
		Log.i(TAG, "picasaShare.isFinished()" + picasaShare.isFinished());

	}

	private class ShareHandler extends AsyncTask<String, String, String> {

		ProgressDialog progressDialog;

		public ShareHandler() {

			progressDialog = new ProgressDialog(context);
		}

		protected void onPreExecute() {
			progressDialog.setMessage(getContext().getString(
					R.string.please_wait_picture_is_uploading));
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		protected String doInBackground(String... strings) {
			handlePostButtonClick();
			return "";

		}

		protected void onPostExecute(String result) {

			progressDialog.cancel();
			StringBuilder messageText = new StringBuilder();
			boolean status = true;

			if (facebookShare.isSuccess() && facebookCB.isChecked()) {
				messageText.append(getContext().getString(
						R.string._message_to_facebook_send_succesfully));
			} else if (!facebookShare.isSuccess() && facebookCB.isChecked()) {
				messageText.append(getContext().getString(
						R.string._sending_message_to_twitter_failed_));
				status = false;
			}
			if (picasaShare.isSuccess() && picasaCB.isChecked()) {
				messageText.append(getContext().getString(
						R.string._message_to_picasa_send_succesfully));
			} else if (!picasaShare.isSuccess() && picasaCB.isChecked()) {
				messageText.append(getContext().getString(
						R.string._sending_message_to_picasa_failed_));
				status = false;
			}
			if (twitterShare.isSuccess() && twitterCB.isChecked()) {
				messageText.append(getContext().getString(
						R.string._message_to_twitter_send_succesfully));
			} else if (!twitterShare.isSuccess() && twitterCB.isChecked()) {
				messageText.append(getContext().getString(
						R.string._sending_message_to_twitter_failed_));
				status = false;
			}

			AlertDialog.Builder shareStatusDialog = new AlertDialog.Builder(
					context);

			if (status) {
				shareStatusDialog.setMessage(getContext().getString(
						R.string.photo_uploaded_succesfully));
			} else {
				shareStatusDialog.setMessage(getContext().getString(
						R.string.photo_upload_failed_see_datails_)
						+ messageText.toString());
			}
			shareStatusDialog.setNeutralButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							ShareDialog.this.cancel();
						}
					});

			shareStatusDialog.show();
		}
	}

	public class ShareComponentChangeHandler implements Observer {

		public void update(Observable changeCaller, Object data) {

			if (!(data instanceof ShareMessages))
				return;

			if (changeCaller instanceof FacebookShare) {
				handleFacebookChanges((ShareMessages) data);
			}
			if (changeCaller instanceof PicasaShare) {
				handlePicasaChanges((ShareMessages) data);
			}
			if (changeCaller instanceof TwitterShare) {
				handleTwitterChanges((ShareMessages) data);
			}
		}

		private void handleFacebookChanges(final ShareMessages message) {

			switch (message.getMessageCode()) {

			case ShareMessages.FACEBOOK_LOGIN_COMPLEATE: {

				parentActivity.runOnUiThread(new Runnable() {
					public void run() {
						facebookCB.setText(message.getMessage());
					}
				});
				break;
			}
			case ShareMessages.FACEBOOK_LOGIN_SUCCESS: {
				facebookCB.setChecked(true);
				facebookCB.setText(message.getMessage());
				facebookButton
						.setText(getContext().getString(R.string.log_out));
				break;
			}

			}

		}

		private void handlePicasaChanges(ShareMessages message) {

			switch (message.getMessageCode()) {

			case ShareMessages.PICASA_EXCEPTION: {
				Log.i(TAG, "Problem with picasa " + message.getMessage());
				break;
			}
			}
		}

		private void handleTwitterChanges(ShareMessages message) {

			switch (message.getMessageCode()) {

			case ShareMessages.TWITTER_LOGIN_SUCESS: {
				twitterCB.setChecked(true);
				twitterCB.setText(message.getMessage());
				twitterButton.setText(getContext().getString(R.string.log_out));
				break;
			}
			case ShareMessages.TWITTER_LOGIN_FAILED: {
				twitterCB.setChecked(false);
				break;
			}

			}

		}

	}
}
