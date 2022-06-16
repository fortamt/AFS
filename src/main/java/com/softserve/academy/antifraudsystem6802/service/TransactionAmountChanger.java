package com.softserve.academy.antifraudsystem6802.service;

import com.softserve.academy.antifraudsystem6802.model.Result;

import com.softserve.academy.antifraudsystem6802.model.entity.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TransactionAmountChanger {

    private TransactionAmountChanger(){}
    private static long allowed = 200;
    private static long manualProcessing = 1500;

    static void changeLimit(Transaction transaction){
        if(transaction.getResult().equals(transaction.getFeedback())){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if(transaction.getResult().equals(Result.ALLOWED.name())
                && transaction.getFeedback().equals(Result.MANUAL_PROCESSING.name())){
            decreaseAllowed(transaction);
        }
        if(transaction.getResult().equals(Result.ALLOWED.name())
                && transaction.getFeedback().equals(Result.PROHIBITED.name())){
            decreaseAllowed(transaction);
            decreaseManualProcessing(transaction);
        }
        if(transaction.getResult().equals(Result.MANUAL_PROCESSING.name())
                && transaction.getFeedback().equals(Result.ALLOWED.name())){
            increaseAllowed(transaction);
        }
        if(transaction.getResult().equals(Result.MANUAL_PROCESSING.name())
                && transaction.getFeedback().equals(Result.PROHIBITED.name())){
            decreaseManualProcessing(transaction);
        }
        if(transaction.getResult().equals(Result.PROHIBITED.name())
                && transaction.getFeedback().equals(Result.ALLOWED.name())){
            increaseAllowed(transaction);
            increaseManualProcessing(transaction);
        }
        if(transaction.getResult().equals(Result.PROHIBITED.name())
                && transaction.getFeedback().equals(Result.MANUAL_PROCESSING.name())){
            increaseManualProcessing(transaction);
        }
    }

    private static void decreaseAllowed(Transaction transaction) {
        allowed = (long) Math.ceil(0.8 * allowed - 0.2 * transaction.getAmount());
    }

    private static void increaseAllowed(Transaction transaction) {
        allowed = (long) Math.ceil(0.8 * allowed + 0.2 * transaction.getAmount());
    }

    private static void increaseManualProcessing(Transaction transaction) {
        manualProcessing = (long) Math.ceil(0.8 * manualProcessing + 0.2 * transaction.getAmount());
    }

    private static void decreaseManualProcessing(Transaction transaction) {
        manualProcessing = (long) Math.ceil(0.8 * manualProcessing - 0.2 * transaction.getAmount());
    }

    public static long getAllowed() {
        return allowed;
    }

    public static long getManualProcessing() {
        return manualProcessing;
    }


}
