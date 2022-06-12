package com.softserve.academy.antifraudsystem6802.model.response;

import com.softserve.academy.antifraudsystem6802.model.Region;
import com.softserve.academy.antifraudsystem6802.model.Result;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
public class TransactionResultResponse {
    private Result result;
    private String info = "";
   private long count;
    public TransactionResultResponse() {

    }
    public void appendInfo(String str) {
        info += str;
    }
    public String getInfo() {
        return info.length() == 0 ? "none" : info.trim();
    }
}
