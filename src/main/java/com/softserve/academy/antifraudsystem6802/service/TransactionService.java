package com.softserve.academy.antifraudsystem6802.service;

import com.softserve.academy.antifraudsystem6802.model.Ip;
import com.softserve.academy.antifraudsystem6802.model.Result;
import com.softserve.academy.antifraudsystem6802.model.StolenCard;
import com.softserve.academy.antifraudsystem6802.model.User;
import com.softserve.academy.antifraudsystem6802.model.request.TransactionRequest;
import com.softserve.academy.antifraudsystem6802.model.response.TransactionResultResponse;
import com.softserve.academy.antifraudsystem6802.repository.StolenCardRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import com.softserve.academy.antifraudsystem6802.repository.IpRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TransactionService {

    IpRepository ipRepository;
    StolenCardRepository stolenCardRepository;

    public TransactionResultResponse process(TransactionRequest request) {
        TransactionResultResponse response = new TransactionResultResponse();
        if (request.getAmount() <= 200) {
            response.setResult(Result.ALLOWED);
        } else if (request.getAmount() <= 1500) {
            response.setResult(Result.MANUAL_PROCESSING);
        } else {
            response.setResult(Result.PROHIBITED);
            response.appendInfo(" amount");
        }
        if(ipRepository.existsByIpAddressIgnoreCase(request.getIp())) {
            response.setResult(Result.PROHIBITED);
            response.appendInfo(" ip");
        }
        if(stolenCardRepository.existsByNumber(request.getNumber())) {
            response.setResult(Result.PROHIBITED);
            response.appendInfo(" number");
        }
        return response;
    }

    @Transactional
    public StolenCard addStolenCard(StolenCard stolenCard) {
        if(stolenCardRepository.existsByNumber(stolenCard.getNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else {
            stolenCardRepository.save(stolenCard);
            return stolenCard;
        }
    }

    @Transactional
    public Map<String, String> deleteStolenCard(String number) {
        if(number.length() != 16) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if(!stolenCardRepository.existsByNumber(number)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            StolenCard stolenCard = stolenCardRepository.findByNumber(number);
            stolenCardRepository.delete(stolenCard);
            return Map.of("status", "Card " + stolenCard.getNumber() + " successfully removed!");
        }
    }

    @Transactional
    public List<StolenCard> listStolenCards() {
        return stolenCardRepository.findAll(
                Sort.sort(StolenCard.class).by(StolenCard::getId).ascending()
        );
    }

    public Optional<Ip> addSuspiciousIp(Ip ip) {
        if(ipRepository.existsByIpAddressIgnoreCase(ip.getIpAddress())){
            return Optional.empty();
        }
        return Optional.of(ipRepository.save(ip));
    }

}
