/*
 * GenericTask.java
 * NfcLibrary project.
 *
 * Created by : Daneo van Overloop - 17/6/2014.
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 AppFoundry. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package com.example.wop.NFC_Model;

import android.os.AsyncTask;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

public class GenericTask extends AsyncTask<Void, Boolean, Boolean> {

    private static final String TAG = GenericTask.class.getSimpleName();
    private final NfcWriteUtility mNfcWriteUtility;

    private AsyncUiCallback mAsyncUiCallback;
    private AsyncOperationCallback mAsyncOperationCallback;
    private Exception error;

    public GenericTask(AsyncUiCallback asyncUiCallback, @NotNull AsyncOperationCallback asyncOperationCallback) {
        mAsyncUiCallback = asyncUiCallback;
        mAsyncOperationCallback = asyncOperationCallback;
        mNfcWriteUtility = new NfcWriteUtilityImpl();
    }

    public GenericTask(AsyncUiCallback asyncUiCallback, @NotNull AsyncOperationCallback asyncOperationCallback, @NotNull NfcWriteUtility nfcWriteUtility) {
        mAsyncUiCallback = asyncUiCallback;
        mAsyncOperationCallback = asyncOperationCallback;
        mNfcWriteUtility = nfcWriteUtility;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d(TAG, "Writing ..");

        boolean res = false;
        try {
            publishProgress(true);
            if (mAsyncOperationCallback != null) {
                res = mAsyncOperationCallback.performWrite(mNfcWriteUtility);
            } else {
                error = new NullPointerException("OperationCallback is null");
            }

        } catch (Exception e) {
            Log.w(TAG, e);
            mAsyncUiCallback.onError(e);
            error = e;
        }

        // Remove tag from intent in order to prevent writing to a not present tag
        return res;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (mAsyncOperationCallback != null && mAsyncUiCallback != null) {
            if (error != null) {
                mAsyncUiCallback.onError(error);
            }
            mAsyncUiCallback.callbackWithReturnValue(result);
        }
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        super.onProgressUpdate(values);
        if (mAsyncUiCallback != null) {
            mAsyncUiCallback.onProgressUpdate(values);
        }
    }

}