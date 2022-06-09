package com.softserve.academy.antifraudsystem6802.controller;

import com.softserve.academy.antifraudsystem6802.model.Ip;
import com.softserve.academy.antifraudsystem6802.model.Result;
import com.softserve.academy.antifraudsystem6802.model.request.TransactionRequest;
import com.softserve.academy.antifraudsystem6802.model.response.TransactionResultResponse;
import com.softserve.academy.antifraudsystem6802.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/antifraud")
@AllArgsConstructor
public class TransactionController {
    TransactionService transactionService;

    @PostMapping("/transaction")
    TransactionResultResponse transactionPost(@Valid @RequestBody TransactionRequest request) {
        Result result = transactionService.process(request.getAmount());
        return new TransactionResultResponse(result);
    }

    @PostMapping("/suspicious-ip")
    @ResponseStatus(HttpStatus.CREATED)
    Ip saveSuspiciousIp(@Valid @RequestBody Ip ip){
        return transactionService.addSuspiciousIp(ip)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
    }

}
