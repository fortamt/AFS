package com.softserve.academy.antifraudsystem6802.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.softserve.academy.antifraudsystem6802.model.validator.Regexp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Entity
@Getter
@Setter
@Table(name = "suspicious_ip")
@AllArgsConstructor
@NoArgsConstructor
public class IpHolder {
    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    @NotEmpty
    @Column(unique = true)
    @Pattern(regexp = Regexp.IP)
    String ip;
}
