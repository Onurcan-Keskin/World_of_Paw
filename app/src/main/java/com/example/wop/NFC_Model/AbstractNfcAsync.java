/*
 * AbstractNfcAsync.java
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

import android.content.Intent;
import android.os.AsyncTask;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractNfcAsync implements AsyncNfcWriteOperation {

    protected NfcWriteUtility mNfcWriteUtility;

    protected AsyncOperationCallback mAsyncOperationCallback;
    protected AsyncUiCallback mAsyncUiCallback;

    public AbstractNfcAsync(final AsyncUiCallback asyncUiCallback) {
        setAsyncUiCallback(asyncUiCallback);
    }

    public AbstractNfcAsync(final AsyncUiCallback asyncUiCallback, NfcWriteUtility nfcWriteUtility) {
        setAsyncUiCallback(asyncUiCallback);
        setNfcWriteUtility(nfcWriteUtility);
    }

    public AbstractNfcAsync(final @Nullable AsyncUiCallback asyncUiCallback, final @NotNull AsyncOperationCallback asyncOperationCallback) {
        this(asyncUiCallback);
        setAsyncOperationCallback(asyncOperationCallback);
    }

    public AbstractNfcAsync(final @Nullable AsyncUiCallback asyncUiCallback, final @NotNull AsyncOperationCallback asyncOperationCallback, final @NotNull NfcWriteUtility nfcWriteUtility) {
        this(asyncUiCallback, nfcWriteUtility);
        setAsyncOperationCallback(asyncOperationCallback);
    }

    protected AsyncUiCallback getAsyncUiCallback() {
        return mAsyncUiCallback;
    }

    protected void setAsyncUiCallback(AsyncUiCallback asyncUiCallback) {
        mAsyncUiCallback = asyncUiCallback;
    }

    protected AsyncOperationCallback getAsyncOperationCallback() {
        return mAsyncOperationCallback;
    }

    protected void setAsyncOperationCallback(AsyncOperationCallback asyncOperationCallback) {
        mAsyncOperationCallback = asyncOperationCallback;
    }

    protected NfcWriteUtility getNfcWriteUtility() {
        return mNfcWriteUtility;
    }

    protected void setNfcWriteUtility(NfcWriteUtility nfcWriteUtility) {
        mNfcWriteUtility = nfcWriteUtility;
    }

    /**
     * Creates an async task with the current {@link #getAsyncOperationCallback()} as action
     *
     * @throws NullPointerException
     *         if {@link #getAsyncOperationCallback()} is null
     * @see AsyncNfcWriteOperation#executeWriteOperation()
     */
    @Override
    public void executeWriteOperation() {
        if (getNfcWriteUtility() != null) {
            new GenericTask(mAsyncUiCallback, getAsyncOperationCallback(), getNfcWriteUtility()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GenericTask(mAsyncUiCallback, getAsyncOperationCallback()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public abstract void executeWriteOperation(Intent intent, Object... args);

    protected boolean checkStringArguments(Class<?> type) {
        return (type.equals(String[].class));
    }

    protected boolean checkDoubleArguments(Class<?> type) {
        return type.equals(Double[].class);
    }
}
