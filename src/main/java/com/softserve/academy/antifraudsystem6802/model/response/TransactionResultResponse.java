package com.softserve.academy.antifraudsystem6802.model.response;

import com.softserve.academy.antifraudsystem6802.model.Result;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class TransactionResultResponse {
    private Result result;
    private Set<String> info = new HashSet<>();

    public void addInfo(String s){
        info.add(s);
    }

    public String getInfo() {
        return String.join(", ", info);
    }

}
