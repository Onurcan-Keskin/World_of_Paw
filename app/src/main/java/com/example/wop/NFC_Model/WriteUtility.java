
package com.example.wop.NFC_Model;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.Tag;

public interface WriteUtility extends NdefWrite {

    boolean writeSafelyToTag(NdefMessage message, Tag tag);

    boolean writeToTag(NdefMessage message, Tag tag) throws FormatException, ReadOnlyTagException, InsufficientCapacityException;

    WriteUtility makeOperationReadOnly();
}
