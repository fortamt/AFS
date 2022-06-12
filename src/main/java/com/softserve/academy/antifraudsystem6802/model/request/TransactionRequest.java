package com.softserve.academy.antifraudsystem6802.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.softserve.academy.antifraudsystem6802.model.Region;
import com.softserve.academy.antifraudsystem6802.model.Result;
import com.softserve.academy.antifraudsystem6802.model.validator.CreditCardConstraint;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity
@Table(name ="transaction_request")
@Getter
@Setter
@NoArgsConstructor
public class TransactionRequest {

    @Positive
    long amount;
    @NotEmpty
    @Pattern(regexp = "^((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])$")
    String ip;
    @CreditCardConstraint
    String number;
    @Enumerated(EnumType.STRING)
    Region region;

    LocalDateTime date;
    @JsonIgnore
    Result result;
    @Id
    @GeneratedValue
    @JsonIgnore
    Long id;
}
