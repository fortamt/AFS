package com.softserve.academy.antifraudsystem6802.model.request;

import lombok.Data;

import javax.validation.constraints.Positive;

@Data
public class TransactionRequest {
    @Positive
    long amount;
}
