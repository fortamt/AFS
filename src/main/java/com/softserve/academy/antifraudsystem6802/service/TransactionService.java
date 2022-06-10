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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransactionService {

    private final String IPV4_REGEX = "(([0-1]?\\d{1,2}\\.)|(2[0-4]\\d\\.)|(25[0-5]\\.)){3}(([0-1]?\\d{1,2})|(2[0-4]\\d)|(25[0-5]))";
    private final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

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


    @Transactional
    public boolean deleteSuspiciousIp(String ip) {
        if(!isValidIPV4(ip)){
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
