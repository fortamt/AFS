package com.softserve.academy.antifraudsystem6802.controller;

import com.softserve.academy.antifraudsystem6802.model.IpHolder;
import com.softserve.academy.antifraudsystem6802.model.StolenCard;
import com.softserve.academy.antifraudsystem6802.model.entity.Transaction;
import com.softserve.academy.antifraudsystem6802.model.request.TransactionFeedback;
import com.softserve.academy.antifraudsystem6802.model.response.TransactionResultResponse;
import com.softserve.academy.antifraudsystem6802.model.validator.CreditCardConstraint;
import com.softserve.academy.antifraudsystem6802.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
@Validated
@RestController
@RequestMapping("/api/antifraud")
@AllArgsConstructor
public class TransactionController {
    TransactionService transactionService;

    @PostMapping("/transaction")
    @ResponseStatus(HttpStatus.OK)
    TransactionResultResponse transactionPost(@Valid @RequestBody Transaction request) {
        return transactionService.process(request);
    }

    @PutMapping("/transaction")
    @ResponseStatus(HttpStatus.OK)
    Transaction transactionPut(@Valid @RequestBody TransactionFeedback feedback) {
        return transactionService.feedbackProcess(feedback);
    }

    @GetMapping("/history")
    @ResponseStatus(HttpStatus.OK)
    List<Transaction> transactionHistory() {
        return transactionService.history();
    }

    @GetMapping("/history/{number}")
    @ResponseStatus(HttpStatus.OK)
    List<Transaction> transactionHistoryByCardNumber(@PathVariable
                                                            @CreditCardConstraint String number) {
        return transactionService.historyByCardNumber(number);
    }

    @PostMapping("/stolencard")
    @ResponseStatus(HttpStatus.OK)
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

    @PostMapping("/suspicious-ip")
    @ResponseStatus(HttpStatus.OK)
    IpHolder saveSuspiciousIp(@Valid @RequestBody IpHolder ip) {
        return transactionService.addSuspiciousIp(ip)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    @ResponseStatus(HttpStatus.OK)
    Map<String, String> deleteSuspiciousIp(@PathVariable("ip") String ip) {
        if (transactionService.deleteSuspiciousIp(ip)) {
            return Map.of(
                    "status", "IP " + ip + " successfully removed!"
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/suspicious-ip")
    @ResponseStatus(HttpStatus.OK)
    List<IpHolder> listSuspiciousAddresses() {
        return transactionService.listSuspiciousAddresses();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
