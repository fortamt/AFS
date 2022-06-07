package com.softserve.academy.antifraudsystem6802.controller;

import com.softserve.academy.antifraudsystem6802.model.Result;
import com.softserve.academy.antifraudsystem6802.model.request.TransactionRequest;
import com.softserve.academy.antifraudsystem6802.model.response.TransactionResultResponse;
import com.softserve.academy.antifraudsystem6802.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
}
