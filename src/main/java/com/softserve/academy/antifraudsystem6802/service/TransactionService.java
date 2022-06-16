package com.softserve.academy.antifraudsystem6802.service;

import com.softserve.academy.antifraudsystem6802.model.Ip;
import com.softserve.academy.antifraudsystem6802.model.Result;
import com.softserve.academy.antifraudsystem6802.model.StolenCard;
import com.softserve.academy.antifraudsystem6802.model.request.Transaction;
import com.softserve.academy.antifraudsystem6802.model.request.TransactionFeedback;
import com.softserve.academy.antifraudsystem6802.model.response.TransactionResultResponse;
import com.softserve.academy.antifraudsystem6802.repository.IpRepository;
import com.softserve.academy.antifraudsystem6802.repository.StolenCardRepository;
import com.softserve.academy.antifraudsystem6802.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    TransactionRepository transactionRepository;

    public TransactionResultResponse process(Transaction request) {
        LocalDateTime localDateTime = request.getDate();
        TransactionResultResponse response = new TransactionResultResponse();
        transactionRepository.save(request);

        long regions = transactionRepository.findAllByNumberAndDateBetween(request.getNumber(), localDateTime.minusHours(1), localDateTime)
                .stream().map(Transaction::getRegion).distinct().count();
        long ips = transactionRepository.findAllByNumberAndDateBetween(request.getNumber(), localDateTime.minusHours(1), localDateTime)
                .stream().map(Transaction::getIp).distinct().count();

        if(stolenCardRepository.existsByNumber(request.getNumber())){
            response.setResult(Result.PROHIBITED);
            response.addInfo("card-number");
        }
        if(ipRepository.existsByIp(request.getIp())){
            response.setResult(Result.PROHIBITED);
            response.addInfo("ip");
        }
        if(regions == 3L){
            response.setResult(Result.MANUAL_PROCESSING);
            response.addInfo("region-correlation");
        } else if(regions > 3L){
            response.setResult(Result.PROHIBITED);
            response.addInfo("region-correlation");
        }
        if(ips == 3L){
            response.setResult(Result.MANUAL_PROCESSING);
            response.addInfo("ip-correlation");
        } else if(ips > 3L){
            response.setResult(Result.PROHIBITED);
            response.addInfo("ip-correlation");
        }
        if(request.getAmount() > 1500){
            response.setResult(Result.PROHIBITED);
            response.addInfo("amount");
        }

        if(response.getInfo().isEmpty()){
            if(request.getAmount() <= TransactionAmountChanger.ALLOWED){
                response.setResult(Result.ALLOWED);
                response.addInfo("none");
            } else if (request.getAmount() <= TransactionAmountChanger.MANUAL_PROCESSING) {
                response.setResult(Result.MANUAL_PROCESSING);
                response.addInfo("amount");
            } else if (request.getAmount() > TransactionAmountChanger.MANUAL_PROCESSING) {
                response.setResult(Result.PROHIBITED);
                response.addInfo("amount");
            }
        }
        request.setResult(response.getResult().name());
        transactionRepository.save(request);
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
        if (ipRepository.existsByIp(ip.getIp())) {
            return Optional.empty();
        }
        return Optional.of(ipRepository.save(ip));
    }

    @Transactional
    public boolean deleteSuspiciousIp(String ip) {
        if (!isValidIPV4(ip)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ipRepository.deleteByIp(ip) == 1;
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

    public Transaction feedbackProcess(TransactionFeedback feedback) {
        Transaction transactionRequest;
        if(transactionRepository.existsByTransactionId(feedback.getTransactionId())){
            transactionRequest = transactionRepository.findByTransactionId(feedback.getTransactionId());
            if(!transactionRequest.getFeedback().isEmpty()){
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        transactionRequest.setFeedback(feedback.getFeedback());
        TransactionAmountChanger.changeLimit(transactionRequest);
        transactionRepository.save(transactionRequest);
        return transactionRequest;
    }

    public List<Transaction> history() {
        return transactionRepository.findAll(Sort.sort(Transaction.class)
                .by(Transaction::getTransactionId)
                .ascending());
    }

    public List<Transaction> historyByCardNumber(String number) {
        if(!transactionRepository.existsByNumber(number)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return transactionRepository.findAllByNumber(number);
    }
}