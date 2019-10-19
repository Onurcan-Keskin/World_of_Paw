
package com.example.wop.NFC_Model;

public class TagNotPresentException extends Exception {

    public TagNotPresentException(){
        super("Intent does not contain a tag");
    }

    public TagNotPresentException(String message){
        super(message);
    }

    public TagNotPresentException(Exception e){
        this.setStackTrace(e.getStackTrace());
    }
}
