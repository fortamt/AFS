package com.softserve.academy.antifraudsystem6802.model.request;

import com.softserve.academy.antifraudsystem6802.model.Result;
import com.softserve.academy.antifraudsystem6802.model.validator.ValueOfEnum;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class TransactionFeedback {
    @NotNull
    Long transactionId;
    @NotNull
    Result feedback;
}
