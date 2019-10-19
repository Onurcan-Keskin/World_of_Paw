
package com.example.wop.NFC_Model;

import android.content.Intent;

public interface AsyncNfcWriteOperation {

    void executeWriteOperation();

    void executeWriteOperation(Intent intent, Object... args);
}
