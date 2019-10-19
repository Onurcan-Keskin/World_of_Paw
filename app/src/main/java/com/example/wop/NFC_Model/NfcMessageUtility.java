
package com.example.wop.NFC_Model;

import android.nfc.FormatException;
import android.nfc.NdefMessage;

import org.jetbrains.annotations.NotNull;

public interface NfcMessageUtility {

    NdefMessage createUri(@NotNull String urlAddress) throws FormatException;

    NdefMessage createTel(@NotNull String telephone) throws FormatException;

    NdefMessage createSms(@NotNull String number, String message) throws FormatException;

    NdefMessage createGeolocation(Double latitude, Double longitude) throws FormatException;

    NdefMessage createEmail(@NotNull String recipient, String subject, String message) throws FormatException;

    NdefMessage createBluetoothAddress(@NotNull String macAddress) throws InsufficientCapacityException, FormatException, ReadOnlyTagException;

    NdefMessage createText(@NotNull String text) throws FormatException;

    NdefMessage createUri(String urlAddress, byte payloadHeader) throws FormatException;
}
