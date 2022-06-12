package com.softserve.academy.antifraudsystem6802.service;

import com.softserve.academy.antifraudsystem6802.model.*;
import com.softserve.academy.antifraudsystem6802.model.request.TransactionRequest;
import com.softserve.academy.antifraudsystem6802.model.response.TransactionResultResponse;
import com.softserve.academy.antifraudsystem6802.repository.StolenCardRepository;
import com.softserve.academy.antifraudsystem6802.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import com.softserve.academy.antifraudsystem6802.repository.IpRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransactionService {

    private final String IPV4_REGEX = "(([0-1]?\\d{1,2}\\.)|(2[0-4]\\d\\.)|(25[0-5]\\.)){3}(([0-1]?\\d{1,2})|(2[0-4]\\d)|(25[0-5]))";
    private final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

    IpRepository ipRepository;
    StolenCardRepository stolenCardRepository;
    TransactionRepository transactionRepository;


    public TransactionResultResponse process(TransactionRequest request) {


        TransactionResultResponse response = new TransactionResultResponse();

        LocalDateTime startTime = request.getDate().minusHours(1);
        LocalDateTime endTime = request.getDate();

//        long regionCount = transactionRepository.calculateRegions(startTime, endTime, request.getNumber());
//        long ipCount = transactionRepository.calculateId(startTime, endTime, request.getNumber());


        List<TransactionRequest> transactionsDuringLastHour = transactionRepository.
                findAllByNumberAndDateBetween(request.getNumber(), startTime, endTime);
        long regionCount = transactionsDuringLastHour.stream().map(TransactionRequest::getRegion).
                filter(region -> region != request.getRegion()).distinct().count();
//        Optional<Region> region = transactionsDuringLastHour.stream().map(TransactionRequest::getRegion).findFirst();
        long ipCount = transactionsDuringLastHour.stream().map(TransactionRequest::getIp).
                filter(ip -> !Objects.equals(ip, request.getIp())).distinct().count();
        if (regionCount == 2 && request.getResult() != Result.PROHIBITED) {
            response.setResult(Result.MANUAL_PROCESSING);
            response.appendInfo("region-correlation");
        }
        if (ipCount == 2 && request.getResult() != Result.PROHIBITED) {
            response.setResult(Result.MANUAL_PROCESSING);
            response.appendInfo("ip-correlation");
        }
        if (regionCount > 2) {
            response.setResult(Result.PROHIBITED);
            response.appendInfo("region-correlation");
        }
        if (ipCount > 2) {
            response.setResult(Result.PROHIBITED);
            response.appendInfo("ip-correlation");
        }
        if (request.getAmount() <= 200) {
            response.setResult(Result.ALLOWED);
            response.setCount(transactionsDuringLastHour.size());
        } else if (request.getAmount() <= 1500) {
            response.setResult(Result.MANUAL_PROCESSING);
        } else {
            response.setResult(Result.PROHIBITED);
            response.appendInfo(" amount");
        }
        if (ipRepository.existsByIpAddressIgnoreCase(request.getIp())) {
            response.setResult(Result.PROHIBITED);
            response.appendInfo(" ip");
        }
        if (stolenCardRepository.existsByNumber(request.getNumber())) {
            response.setResult(Result.PROHIBITED);
            response.appendInfo(" number");
        }

        return response;
    }


    @Transactional
    public StolenCard addStolenCard(StolenCard stolenCard) {
        if (stolenCardRepository.existsByNumber(stolenCard.getNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else {
            stolenCardRepository.save(stolenCard);
            return stolenCard;
        }
    }

    @Transactional
    public Map<String, String> deleteStolenCard(String number) {
        if (number.length() != 16) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!stolenCardRepository.existsByNumber(number)) {
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
        if (ipRepository.existsByIpAddressIgnoreCase(ip.getIpAddress())) {
            return Optional.empty();
        }
        return Optional.of(ipRepository.save(ip));
    }


    @Transactional
    public boolean deleteSuspiciousIp(String ip) {
        if (!isValidIPV4(ip)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ipRepository.deleteByIpAddressIgnoreCase(ip) == 1;
    }

    private boolean isValidIPV4(final String s) {
        return IPV4_PATTERN.matcher(s).matches();
    }

    public List<Ip> listSuspiciousAddresses() {
        return ipRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Ip::getId))
                .collect(Collectors.toList());
    }
}
