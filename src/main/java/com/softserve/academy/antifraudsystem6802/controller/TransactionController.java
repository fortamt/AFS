package com.softserve.academy.antifraudsystem6802.controller;

import com.softserve.academy.antifraudsystem6802.model.Result;
import com.softserve.academy.antifraudsystem6802.model.StolenCard;
import com.softserve.academy.antifraudsystem6802.model.request.TransactionRequest;
import com.softserve.academy.antifraudsystem6802.model.response.TransactionResultResponse;
import com.softserve.academy.antifraudsystem6802.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/stolencard")
    @ResponseStatus(HttpStatus.CREATED)
    StolenCard addStolenCard(@Valid @RequestBody StolenCard stolenCard) {
        return transactionService.addStolenCard(stolenCard);
    }

    @DeleteMapping("/stolencard/{number}")
    @ResponseStatus(HttpStatus.OK)
    Map<String, String> deleteStolenCard(@PathVariable String number) {
        return transactionService.deleteStolenCard(number);
    }

    @GetMapping("/stolencard")
    @ResponseStatus(HttpStatus.OK)
    List<StolenCard> listStolenCards() {
        return transactionService.listStolenCards();
    }
}
