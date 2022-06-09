package com.softserve.academy.antifraudsystem6802.service;

import com.softserve.academy.antifraudsystem6802.model.Ip;
import com.softserve.academy.antifraudsystem6802.model.Result;
import com.softserve.academy.antifraudsystem6802.repository.IpRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionService {
    IpRepository ipRepository;

    public TransactionService(IpRepository ipRepository) {
        this.ipRepository = ipRepository;
    }

    public Result process(long amount) {
        if (amount <= 200) {
            return Result.ALLOWED;
        } else if (amount <= 1500) {
            return Result.MANUAL_PROCESSING;
        } else {
            return Result.PROHIBITED;
        }
    }


    public Optional<Ip> addSuspiciousIp(Ip ip) {
        if(ipRepository.existsByIpAddressIgnoreCase(ip.getIpAddress())){
            return Optional.empty();
        }
        return Optional.of(ipRepository.save(ip));
    }
}
