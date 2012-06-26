package com.rec.photoeditor.share;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Observer;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.picasa.PicasaClient;
import com.google.api.services.picasa.PicasaUrl;
import com.google.api.services.picasa.model.AlbumEntry;
import com.google.api.services.picasa.model.PhotoEntry;
import com.google.api.services.picasa.model.UserFeed;

public class PicasaShare extends RECEditorShare {

	public static final int DIALOG_ACCOUNTS = 0;

	private static final String TAG = "Photo Editor";

	private static final String EDITOR_DEFAULT_FOLDER_NAME = "Rec Editor Album";

	protected static final String AUTH_TOKEN_TYPE = "lh2";

	private PicasaClient client;

	private final HttpTransport transport = AndroidHttp
			.newCompatibleTransport();

	private ClientLogin.Response clientLogin = new ClientLogin.Response();

	private String postLink;

	private String authToken;
	
	private String googleUsername;

	private Context context;

	public PicasaShare(Context _context, Observer _observer) {
		super(_context, _observer);
		context = _context;
		client = new PicasaClient(transport.createRequestFactory(clientLogin));
		client.setApplicationName("REC Photo Editor/1.0");

	}

	public void uploadImage(String imagePath, String imageComment) {

		uploadImage(imagePath,imageComment,0);

	}
	private void uploadImage(String imagePath, String imageComment, int count) {
		
	
		PicasaUrl url = PicasaUrl.relativeToRoot("feed/api/user/default");
		UserFeed userFeed;
		
		try {
			userFeed = client.executeGetUserFeed(url);
			this.postLink = userFeed.getPostLink();
			AlbumEntry album = findDefaultEditorAlbum();
			uploadImage(album, imagePath, imageComment);
		} catch (FileNotFoundException e) {
			handleException(e);
		} catch (IOException e) {
			if(count<10){
			gotAccount(true);
			uploadImage(imagePath,imageComment,count+1);
			}else{
				handleException(e);
			}
				
		}
		
	}

	public void gotAccount(final AccountManager manager, final Account account) {

		manager.invalidateAuthToken("com.google", this.authToken);

		new Thread() {

			@Override
			public void run() {
				try {

					final Bundle bundle = manager.getAuthToken(account,
							AUTH_TOKEN_TYPE, true, null, null).getResult();
					authenticatedClientLogin(bundle
							.getString(AccountManager.KEY_AUTHTOKEN));
				} catch (Exception e) {
					handleException(e);
				}
			}
		}.start();
	}

	  private void gotAccount(boolean tokenExpired) {
		    //SharedPreferences settings = getPreferences(MODE_PRIVATE);
		    if (googleUsername != null) {
		      AccountManager manager = AccountManager.get(context);
		      Account[] accounts = manager.getAccountsByType("com.google");
		      int size = accounts.length;
		      for (int i = 0; i < size; i++) {
		        Account account = accounts[i];
		        if (googleUsername.equals(account.name)) {
		          if (tokenExpired) {
		            manager.invalidateAuthToken("com.google", this.authToken);
		          }
		          gotAccount(manager, account);
		          return;
		        }
		      }
		    }
		    notifyObservers(new ShareMessages(ShareMessages.GOOGLE_TOKEN_EXPIRED, ""));
		  }
	
	
	private AlbumEntry findDefaultEditorAlbum() {

		AlbumEntry album = getDefaultEditorAlbum();
		if (album == null) {
			album = createDefaultEditorAlbum();
		}

		return album;
	}

	private void uploadImage(AlbumEntry album, String imagePath,
			String imageComment) throws FileNotFoundException, IOException {
		PicasaUrl url = new PicasaUrl(album.getFeedLink());
		InputStreamContent content = new InputStreamContent("image/jpeg",
				new FileInputStream(imagePath));


		PhotoEntry photo = new PhotoEntry();
		photo.summary = imageComment;
		photo.title = imageComment;
		photo.updated = imageComment;

		PhotoEntry postedPhoto = client.executeInsertPhotoEntry(url, content, imagePath);

		if(postedPhoto!=null){
			success=true;
		}
		
		finished= true;
	}

	void authenticatedClientLogin(String authToken) {
		this.authToken = authToken;
		clientLogin.auth = authToken;
	}

	private AlbumEntry getDefaultEditorAlbum() {

		PicasaUrl url = PicasaUrl.relativeToRoot("feed/api/user/default");
		UserFeed userFeed;
		try {
			userFeed = client.executeGetUserFeed(url);
			this.postLink = userFeed.getPostLink();
			if (userFeed.albums != null) {
				for (AlbumEntry album : userFeed.albums) {

					if (album != null
							&& EDITOR_DEFAULT_FOLDER_NAME.equals(album.title)) {
						return album;
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private AlbumEntry createDefaultEditorAlbum() {
		AlbumEntry album = new AlbumEntry();
		album.access = "private";
		album.title = EDITOR_DEFAULT_FOLDER_NAME;
		try {
			return client.executeInsert(new PicasaUrl(postLink), album);
		} catch (IOException e) {
			handleException(e);
		}

		return null;
	}

	private void handleException(Exception e) {
		notifyObservers(new ShareMessages(ShareMessages.PICASA_EXCEPTION, ""));
		finished = true;
		e.printStackTrace();
	}

	public void setGoogleUsername(String name) {
		googleUsername= name;
		
	}
	public String getGoogleUsername( ) {
		return googleUsername;
		
	}

}
