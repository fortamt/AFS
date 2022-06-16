package com.softserve.academy.antifraudsystem6802.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.softserve.academy.antifraudsystem6802.model.validator.CreditCardConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "t_stolencard")
@AllArgsConstructor
@NoArgsConstructor
public class StolenCard {
    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    @CreditCardConstraint
    String number;

}
