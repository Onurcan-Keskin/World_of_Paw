
package com.example.wop.NFC_Model;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

public interface NdefWrite {

    boolean writeToNdef(NdefMessage message, Ndef ndef) throws ReadOnlyTagException, InsufficientCapacityException, FormatException;

    boolean writeToNdefAndMakeReadonly(NdefMessage message, Ndef ndef) throws ReadOnlyTagException, InsufficientCapacityException, FormatException;

    boolean writeToNdefFormatable(NdefMessage message, NdefFormatable ndefFormatable) throws FormatException;

    boolean writeToNdefFormatableAndMakeReadonly(NdefMessage message, NdefFormatable ndefFormat) throws FormatException;

}
