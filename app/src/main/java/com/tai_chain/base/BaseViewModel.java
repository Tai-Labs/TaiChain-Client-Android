package com.tai_chain.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.tai_chain.bean.ErrorEnvelope;
import com.tai_chain.bean.ServiceException;
import com.tai_chain.utils.ToastUtils;

import io.reactivex.disposables.Disposable;

public class BaseViewModel extends ViewModel {

	protected final MutableLiveData<ErrorEnvelope> error = new MutableLiveData<>();
	protected final MutableLiveData<Boolean> progress = new MutableLiveData<>();
	protected Disposable disposable;

	@Override
	protected void onCleared() {
		cancel();
	}

	protected void cancel() {
		if (disposable != null && !disposable.isDisposed()) {
			disposable.dispose();
		}
	}

	public LiveData<ErrorEnvelope> error() {
		return error;
	}

	public LiveData<Boolean> progress() {
		return progress;
	}

	protected void onError(Throwable throwable) {

		if (throwable instanceof ServiceException) {
			error.postValue(((ServiceException) throwable).error);
		} else {
			error.postValue(new ErrorEnvelope(Constants.ErrorCode.UNKNOWN, null, throwable));
			// TODO: Add dialog with offer send error log to developers: notify about error.
			Log.d("SESSION", "Err", throwable);
		}
	}
}
