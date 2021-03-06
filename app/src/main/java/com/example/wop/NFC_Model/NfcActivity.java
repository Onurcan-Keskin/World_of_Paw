/*
 * NfcActivity.java
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

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

abstract public class NfcActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    private static final String TAG = NfcActivity.class.getName();

    private static final String beamEnabled = "androidBeamEnabled";

    NfcAdapter mNfcAdapter;
    private List<String> mNfcMessageStrings;

    private NfcMessageUtility mNfcMessageUtility = new NfcMessageUtilityImpl();
    private NfcReadUtility mNfcReadUtility = new NfcReadUtilityImpl();

    private PendingIntent pendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mTechLists;
    private boolean mBeamEnabled;

    public NfcMessageUtility getNfcMessageUtility() {
        return mNfcMessageUtility;
    }

    public NfcReadUtility getNfcReadUtility() {
        return mNfcReadUtility;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAdapter();
        initFields();
    }

    @Override
    protected void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean(beamEnabled)) {
            enableBeam();
        } else {
            disableBeam();
        }
    }

    public void onResume() {
        super.onResume();
        initAdapter();

        if (getNfcAdapter() != null) {
            getNfcAdapter().enableForegroundDispatch(this, pendingIntent, mIntentFilters, mTechLists);
            Log.d(TAG, "FGD enabled");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "Received intent!");
        setIntent(intent);
        if (getIntent() != null) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
                mNfcMessageStrings = transformSparseArrayToArrayList(mNfcReadUtility.readFromTagWithSparseArray(intent));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(beamEnabled, mBeamEnabled);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getNfcAdapter() != null) {
            getNfcAdapter().disableForegroundDispatch(this);
            Log.d(TAG, "FGD disabled");
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        return new NfcMessageUtilityImpl().createText("You're seeing this message because you have not overridden the createNdefMessage(NfcEvent event) in your activity.");

    }

    protected List<String> getNfcMessages() {
        return mNfcMessageStrings;
    }

    private void initFields() {
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        mIntentFilters = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)};
        mTechLists = new String[][]{new String[]{Ndef.class.getName()},
                new String[]{NdefFormatable.class.getName()}};
    }

    private void initAdapter() {
        if (getNfcAdapter() == null) {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
            Log.d(TAG, "Adapter initialized");
        }
    }

    protected void enableBeam() {
        if (getNfcAdapter() != null) {
            getNfcAdapter().setNdefPushMessageCallback(this, this);
            mBeamEnabled = true;
            Log.d(TAG, "Beam enabled");
        }

    }

    protected boolean beamEnabled() {
        return mBeamEnabled;
    }

    protected void disableBeam() {
        if (getNfcAdapter() != null) {
            getNfcAdapter().setNdefPushMessageCallback(null, this);
            mBeamEnabled = false;
            Log.d(TAG, "Beam disabled");
        }
    }

    protected NfcAdapter getNfcAdapter() {
        return this.mNfcAdapter;
    }

    protected void setNfcAdapter(NfcAdapter nfcAdapter) {
        this.mNfcAdapter = nfcAdapter;
    }

    protected List<String> transformSparseArrayToArrayList(SparseArray<String> sparseArray) {
        List<String> list = new ArrayList<String>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++) {
            list.add(sparseArray.valueAt(i));
        }
        return list;
    }
}
