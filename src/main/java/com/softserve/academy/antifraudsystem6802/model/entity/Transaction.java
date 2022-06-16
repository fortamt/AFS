package com.softserve.academy.antifraudsystem6802.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.softserve.academy.antifraudsystem6802.model.RegionCodes;
import com.softserve.academy.antifraudsystem6802.model.validator.CreditCardConstraint;
import com.softserve.academy.antifraudsystem6802.model.validator.ValueOfEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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
public class TransactionRequest {
    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long transactionId;
    @NotNull
    @Positive
    Long amount;
    @NotEmpty
    @Pattern(regexp = "^((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])$")
    String ip;
    @NotEmpty
    @CreditCardConstraint
    String number;
    @NotEmpty
    @ValueOfEnum(enumClass = RegionCodes.class)
    String region;
    LocalDateTime date;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String result;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String feedback ="";
}
