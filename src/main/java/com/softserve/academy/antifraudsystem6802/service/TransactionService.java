package com.softserve.academy.antifraudsystem6802.service;

import com.softserve.academy.antifraudsystem6802.model.Result;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    public Result process(long amount) {
        if (amount <= 200) {
            return Result.ALLOWED;
        } else if (amount <= 1500) {
            return Result.MANUAL_PROCESSING;
        } else {
            return Result.PROHIBITED;
        }
    }
}
