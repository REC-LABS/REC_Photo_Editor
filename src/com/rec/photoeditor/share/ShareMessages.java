package com.rec.photoeditor.share;

public class ShareMessages {

	public static final int TWITTER_STATUS = 10;
	public static final int TWITPIC_STATUS = 11;
	public static final int TWITTER_IMAGE_IS_SENDING = 12;
	public static final int TWITTER_LOGIN_SUCESS = 13;
	public static final int TWITTER_LOGIN_FAILED = 14;
	public static final int FACEBOOK_AUTH_FAIL = 21;
	public static final int FACEBOOK_LOGIN_COMPLEATE = 22;
	public static final int FACEBOOK_LOGIN_SUCCESS = 23;
	public static final int FACEBOOK_UPLOADING_PHOTO = 24;
	public static final int FACEBOOK_UPLOAD_COMPLEATED = 25;
	public static final int FACEBOOK_UPLOAD_FAILED = 26;
	public static final int FACEBOOK_LOGOUT_SUCCESS = 27;
	public static final int PICASA_EXCEPTION = 30;
	public static final int GOOGLE_TOKEN_EXPIRED = 31;
	private int messageCode;
	private String message;

	public ShareMessages() {
		super();
	}

	public ShareMessages(int messageCode, String message) {
		super();
		this.messageCode = messageCode;
		this.message = message;
	}

	public int getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(int messageCode) {
		this.messageCode = messageCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
