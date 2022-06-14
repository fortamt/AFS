package com.softserve.academy.antifraudsystem6802.service;

import com.softserve.academy.antifraudsystem6802.model.Result;
import com.softserve.academy.antifraudsystem6802.model.request.TransactionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TransactionAmountChanger {

    public static long ALLOWED = 200;
    public static long MANUAL_PROCESSING = 1500;

    static void changeLimit(TransactionRequest transaction){
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

    private static void decreaseAllowed(TransactionRequest transaction) {
        ALLOWED = (long) Math.ceil(0.8 * ALLOWED - 0.2 * transaction.getAmount());
    }

    private static void increaseAllowed(TransactionRequest transaction) {
        ALLOWED = (long) Math.ceil(0.8 * ALLOWED + 0.2 * transaction.getAmount());
    }

    private static void increaseManualProcessing(TransactionRequest transaction) {
        MANUAL_PROCESSING = (long) Math.ceil(0.8 * MANUAL_PROCESSING + 0.2 * transaction.getAmount());
    }

    private static void decreaseManualProcessing(TransactionRequest transaction) {
        MANUAL_PROCESSING = (long) Math.ceil(0.8 * MANUAL_PROCESSING - 0.2 * transaction.getAmount());
    }


}
