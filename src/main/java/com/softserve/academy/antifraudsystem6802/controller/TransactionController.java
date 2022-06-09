package com.softserve.academy.antifraudsystem6802.controller;

import com.softserve.academy.antifraudsystem6802.model.Ip;
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
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
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
    @PostMapping("/suspicious-ip")
    @ResponseStatus(HttpStatus.CREATED)
    Ip saveSuspiciousIp(@Valid @RequestBody Ip ip) {
        return transactionService.addSuspiciousIp(ip)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    @ResponseStatus(HttpStatus.CREATED)
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
    List<Ip> listSuspiciousAddresses() {
        return transactionService.listSuspiciousAddresses();
    }
}
