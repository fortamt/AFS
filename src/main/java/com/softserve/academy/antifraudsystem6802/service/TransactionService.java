package com.softserve.academy.antifraudsystem6802.service;

import com.softserve.academy.antifraudsystem6802.model.IpHolder;
import com.softserve.academy.antifraudsystem6802.model.Result;
import com.softserve.academy.antifraudsystem6802.model.StolenCard;
import com.softserve.academy.antifraudsystem6802.model.entity.Transaction;
import com.softserve.academy.antifraudsystem6802.model.request.TransactionFeedback;
import com.softserve.academy.antifraudsystem6802.model.response.TransactionResultResponse;
import com.softserve.academy.antifraudsystem6802.model.validator.Regexp;
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

    private static final Pattern IPV4_PATTERN = Pattern.compile(Regexp.IP);


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
        final String regionCorrelation = "region-correlation";
        if(regions == 3L){
            response.setResult(Result.MANUAL_PROCESSING);
            response.addInfo(regionCorrelation);
        } else if(regions > 3L){
            response.setResult(Result.PROHIBITED);
            response.addInfo(regionCorrelation);
        }
        String ipCorrelation = "ip-correlation";
        if(ips == 3L){
            response.setResult(Result.MANUAL_PROCESSING);
            response.addInfo(ipCorrelation);
        } else if(ips > 3L){
            response.setResult(Result.PROHIBITED);
            response.addInfo(ipCorrelation);
        }
        final String amount = "amount";
        if(request.getAmount() > 1500){
            response.setResult(Result.PROHIBITED);
            response.addInfo(amount);
        }

        if(response.getInfo().isEmpty()){
            if(request.getAmount() <= TransactionAmountChanger.getAllowed()){
                response.setResult(Result.ALLOWED);
                response.addInfo("none");
            } else if (request.getAmount() <= TransactionAmountChanger.getManualProcessing()) {
                response.setResult(Result.MANUAL_PROCESSING);
                response.addInfo(amount);
            } else if (request.getAmount() > TransactionAmountChanger.getManualProcessing()) {
                response.setResult(Result.PROHIBITED);
                response.addInfo(amount);
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

    public Optional<IpHolder> addSuspiciousIp(IpHolder ip) {
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

    public List<IpHolder> listSuspiciousAddresses() {
        return ipRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(IpHolder::getId))
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
        transactionRequest.setFeedback(feedback.getFeedback().name());
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