package com.softserve.academy.antifraudsystem6802.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.softserve.academy.antifraudsystem6802.model.RegionCodes;
import com.softserve.academy.antifraudsystem6802.model.validator.CreditCardConstraint;
import com.softserve.academy.antifraudsystem6802.model.validator.Regexp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long transactionId;
    @NotNull
    @Positive
    Long amount;
    @NotEmpty
    @Pattern(regexp = Regexp.IP)
    String ip;
    @NotEmpty
    @CreditCardConstraint
    String number;
    @NotNull
    @Enumerated(EnumType.STRING)
    RegionCodes region;
    LocalDateTime date;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String result;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String feedback ="";
}
