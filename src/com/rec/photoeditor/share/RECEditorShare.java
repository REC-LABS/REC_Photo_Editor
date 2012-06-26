package com.rec.photoeditor.share;

import java.util.Observable;
import java.util.Observer;


import android.content.Context;
import android.util.Log;

public abstract class RECEditorShare extends Observable   {

	protected boolean finished = true;
	protected boolean success = false;
	private static final String TAG = "Photo Editor";
	
	
	RECEditorShare(Context context, Observer observer){
		this.addObserver(observer);
	
	}

	@Override
	public boolean hasChanged() {
		return true;
	}

	public boolean isFinished() {
		return finished;
	}
	public void setFinished(boolean _finished) {
		 finished = _finished;
	}
	@Override
	public void notifyObservers(Object data) {
		super.notifyObservers(data);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
