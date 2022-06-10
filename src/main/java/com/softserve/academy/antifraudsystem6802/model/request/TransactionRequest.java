package com.softserve.academy.antifraudsystem6802.model.request;

import com.softserve.academy.antifraudsystem6802.model.validator.CreditCardConstraint;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Data
public class TransactionRequest {
    @Positive
    long amount;
    @NotEmpty
    @Pattern(regexp = "^((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])$")
    String ip;
    @CreditCardConstraint
    String number;
}
