
package com.example.wop.NFC_Model;

public class ReadOnlyTagException extends TagNotWritableException {

    public ReadOnlyTagException() {
    }

    public ReadOnlyTagException(String message) {
        super(message);
    }


}
