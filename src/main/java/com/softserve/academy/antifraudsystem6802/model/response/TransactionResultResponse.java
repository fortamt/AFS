package com.softserve.academy.antifraudsystem6802.model.response;

import com.softserve.academy.antifraudsystem6802.model.Result;
import lombok.*;

@Getter
@Setter
public class TransactionResultResponse {
    private Result result;
    private String info = "";
    public TransactionResultResponse() {

    }
    public void appendInfo(String str) {
        info += str;
    }
    public String getInfo() {
        return info.length() == 0 ? "none" : info.trim();
    }
}
