
package com.example.wop.NFC_Model;

import android.nfc.FormatException;

public interface AsyncOperationCallback {

    boolean performWrite(NfcWriteUtility writeUtility) throws ReadOnlyTagException, InsufficientCapacityException, TagNotPresentException, FormatException;
}
