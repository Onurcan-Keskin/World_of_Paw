
package com.example.wop.NFC_Model;

public interface AsyncUiCallback {

    void callbackWithReturnValue(Boolean result);

    void onProgressUpdate(Boolean... values);

    void onError(Exception e);
}
