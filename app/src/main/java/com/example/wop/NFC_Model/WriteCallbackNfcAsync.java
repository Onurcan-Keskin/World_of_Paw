
package com.example.wop.NFC_Model;

import android.content.Intent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WriteCallbackNfcAsync extends AbstractNfcAsync {

    public WriteCallbackNfcAsync(AsyncUiCallback asyncUiCallback) {
        super(asyncUiCallback);
    }

    public WriteCallbackNfcAsync(@Nullable AsyncUiCallback asyncUiCallback, @NotNull AsyncOperationCallback asyncOperationCallback) {
        super(asyncUiCallback, asyncOperationCallback);
    }

    public WriteCallbackNfcAsync(@Nullable AsyncUiCallback asyncUiCallback, @NotNull AsyncOperationCallback asyncOperationCallback, @NotNull NfcWriteUtility nfcWriteUtility) {
        super(asyncUiCallback, asyncOperationCallback, nfcWriteUtility);
    }

    @Override
    public void executeWriteOperation(Intent intent, Object... args) {
        super.executeWriteOperation();
    }
}
