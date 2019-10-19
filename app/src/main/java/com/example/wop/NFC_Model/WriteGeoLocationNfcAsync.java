
package com.example.wop.NFC_Model;

import android.content.Intent;
import android.nfc.FormatException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WriteGeoLocationNfcAsync extends AbstractNfcAsync {

    public WriteGeoLocationNfcAsync(AsyncUiCallback asyncUiCallback) {
        super(asyncUiCallback);
    }

    public WriteGeoLocationNfcAsync(@Nullable AsyncUiCallback asyncUiCallback, @NotNull AsyncOperationCallback asyncOperationCallback) {
        super(asyncUiCallback, asyncOperationCallback);
    }

    public WriteGeoLocationNfcAsync(@Nullable AsyncUiCallback asyncUiCallback, @NotNull AsyncOperationCallback asyncOperationCallback, @NotNull NfcWriteUtility nfcWriteUtility) {
        super(asyncUiCallback, asyncOperationCallback, nfcWriteUtility);
    }

    @Override
    public void executeWriteOperation(final Intent intent, final Object... args) {
        if (checkDoubleArguments(args.getClass()) || args.length != 2 || intent == null) {
            throw new UnsupportedOperationException("Invalid arguments");
        }

        setAsyncOperationCallback(new AsyncOperationCallback() {
            @Override
            public boolean performWrite(NfcWriteUtility writeUtility) throws ReadOnlyTagException, InsufficientCapacityException, TagNotPresentException, FormatException {
                return writeUtility.writeGeolocationToTagFromIntent((Double) args[0], (Double) args[1], intent);
            }
        });

        super.executeWriteOperation();
    }

}
