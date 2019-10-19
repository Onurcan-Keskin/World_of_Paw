
package com.example.wop.NFC_Model;

import android.content.Intent;
import android.nfc.NdefMessage;

import java.util.Map;

public interface NfcReadUtility {

    android.util.SparseArray<String> readFromTagWithSparseArray(Intent nfcDataIntent);

    Map<Byte,String> readFromTagWithMap(Intent nfcDataIntent);

    java.util.Iterator<Byte> retrieveMessageTypes(NdefMessage message);

    String retrieveMessage(NdefMessage message);
}
