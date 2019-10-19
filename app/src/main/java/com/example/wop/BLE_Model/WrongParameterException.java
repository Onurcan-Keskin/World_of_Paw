package com.example.wop.BLE_Model;

public class WrongParameterException extends Exception {
    public WrongParameterException() {
        super("Some of the parameters are invalid!");
    }
}
