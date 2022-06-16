package com.softserve.academy.antifraudsystem6802.service;

import com.softserve.academy.antifraudsystem6802.model.IpHolder;
import com.softserve.academy.antifraudsystem6802.model.RegionCodes;
import com.softserve.academy.antifraudsystem6802.model.Result;
import com.softserve.academy.antifraudsystem6802.model.StolenCard;
import com.softserve.academy.antifraudsystem6802.model.entity.Transaction;
import com.softserve.academy.antifraudsystem6802.model.request.TransactionFeedback;
import com.softserve.academy.antifraudsystem6802.model.response.TransactionResultResponse;
import com.softserve.academy.antifraudsystem6802.repository.IpRepository;
import com.softserve.academy.antifraudsystem6802.repository.StolenCardRepository;
import com.softserve.academy.antifraudsystem6802.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private StolenCardRepository stolenCardRepository;
    @Mock private IpRepository ipRepository;

    private TransactionService underTest;

    private Transaction allowedTransaction;
    private Transaction manualTransaction;
    private Transaction prohibitedTransaction;
    private StolenCard stolenCard;
    private IpHolder ipHolder;
    private TransactionFeedback feedback;

    @BeforeEach
    void setUp() {
        underTest = new TransactionService(ipRepository, stolenCardRepository, transactionRepository);

        ipHolder = new IpHolder(null, "192.168.0.1");

        feedback = new TransactionFeedback(1L, Result.MANUAL_PROCESSING);

        allowedTransaction = new Transaction(null,
                100L,
                "192.168.0.1",
                "5375418803162254",
                RegionCodes.EAP,
                LocalDateTime.now(),
                "ALLOWED",
                "");

        manualTransaction = new Transaction(null,
                1000L,
                "192.168.0.1",
                "5375418803162254",
                RegionCodes.EAP,
                LocalDateTime.now(),
                null,
                "");

        prohibitedTransaction = new Transaction(null,
                5000L,
                "192.168.0.1",
                "5375418803162254",
                RegionCodes.EAP,
                LocalDateTime.now(),
                null,
                "");

        stolenCard = new StolenCard(null, "5375418803162254");
    }

    @Test
    void process1() {
        Mockito.when(stolenCardRepository.existsByNumber(allowedTransaction.getNumber())).thenReturn(false);
        Mockito.when(ipRepository.existsByIp(allowedTransaction.getIp())).thenReturn(false);

        TransactionResultResponse response = underTest.process(allowedTransaction);
        verify(transactionRepository, times(2)).save(allowedTransaction);

        assertThat(response.getResult()).isEqualTo(Result.ALLOWED);
        assertThat(response.getInfo()).isEqualTo("none");
    }

    @Test
    void process2() {
        Mockito.when(stolenCardRepository.existsByNumber(allowedTransaction.getNumber())).thenReturn(true);
        Mockito.when(ipRepository.existsByIp(allowedTransaction.getIp())).thenReturn(true);

        TransactionResultResponse response = underTest.process(allowedTransaction);
        verify(transactionRepository, times(2)).save(allowedTransaction);

        assertThat(response.getResult()).isEqualTo(Result.PROHIBITED);
        assertThat(response.getInfo()).isEqualTo("card-number, ip");
    }

    @Test
    void process3() {
        Mockito.when(transactionRepository.countDistinctByRegionAndDateBetween(allowedTransaction.getRegion(), allowedTransaction.getDate().minusHours(1), allowedTransaction.getDate()))
                        .thenReturn(3L);
        Mockito.when(transactionRepository.countDistinctByIpAndDateBetween(allowedTransaction.getIp(), allowedTransaction.getDate().minusHours(1), allowedTransaction.getDate()))
                .thenReturn(3L);
        Mockito.when(stolenCardRepository.existsByNumber(allowedTransaction.getNumber())).thenReturn(false);
        Mockito.when(ipRepository.existsByIp(allowedTransaction.getIp())).thenReturn(false);

        TransactionResultResponse response = underTest.process(allowedTransaction);
        verify(transactionRepository, times(2)).save(allowedTransaction);

        assertThat(response.getResult()).isEqualTo(Result.MANUAL_PROCESSING);
        assertThat(response.getInfo()).isEqualTo("ip-correlation, region-correlation");
    }

    @Test
    void process4() {
        Mockito.when(transactionRepository.countDistinctByRegionAndDateBetween(allowedTransaction.getRegion(), allowedTransaction.getDate().minusHours(1), allowedTransaction.getDate()))
                .thenReturn(4L);
        Mockito.when(transactionRepository.countDistinctByIpAndDateBetween(allowedTransaction.getIp(), allowedTransaction.getDate().minusHours(1), allowedTransaction.getDate()))
                .thenReturn(4L);
        Mockito.when(stolenCardRepository.existsByNumber(allowedTransaction.getNumber())).thenReturn(false);
        Mockito.when(ipRepository.existsByIp(allowedTransaction.getIp())).thenReturn(false);

        TransactionResultResponse response = underTest.process(allowedTransaction);
        verify(transactionRepository, times(2)).save(allowedTransaction);

        assertThat(response.getResult()).isEqualTo(Result.PROHIBITED);
        assertThat(response.getInfo()).isEqualTo("ip-correlation, region-correlation");
    }

    @Test
    void process5() {
        Mockito.when(stolenCardRepository.existsByNumber(manualTransaction.getNumber())).thenReturn(false);
        Mockito.when(ipRepository.existsByIp(manualTransaction.getIp())).thenReturn(false);

        TransactionResultResponse response = underTest.process(manualTransaction);
//        verify(transactionRepository, times(2)).save(allowedTransaction);

        assertThat(response.getResult()).isEqualTo(Result.MANUAL_PROCESSING);
        assertThat(response.getInfo()).isEqualTo("amount");
    }

    @Test
    void process6() {
        Mockito.when(stolenCardRepository.existsByNumber(prohibitedTransaction.getNumber())).thenReturn(false);
        Mockito.when(ipRepository.existsByIp(prohibitedTransaction.getIp())).thenReturn(false);

        TransactionResultResponse response = underTest.process(prohibitedTransaction);
//        verify(transactionRepository, times(2)).save(allowedTransaction);

        assertThat(response.getResult()).isEqualTo(Result.PROHIBITED);
        assertThat(response.getInfo()).isEqualTo("amount");
    }

    @Test
    void addStolenCardException() {
        Mockito.when(stolenCardRepository.existsByNumber(stolenCard.getNumber())).thenReturn(true);
        assertThatThrownBy(() -> underTest.addStolenCard(stolenCard))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void addStolenCard() {
        Mockito.when(stolenCardRepository.existsByNumber(stolenCard.getNumber())).thenReturn(false);
        underTest.addStolenCard(stolenCard);

        ArgumentCaptor<StolenCard> stolenCardArgumentCaptor = ArgumentCaptor.forClass(StolenCard.class);
        verify(stolenCardRepository).save(stolenCardArgumentCaptor.capture());

        StolenCard capturedStolenCard = stolenCardArgumentCaptor.getValue();

        assertThat(capturedStolenCard).isEqualTo(stolenCard);
    }

    @Test
    void deleteStolenCardWrongNumber() {
        assertThatThrownBy(() -> underTest.deleteStolenCard("1"))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void deleteNotExistedStolenCard() {
        Mockito.when(stolenCardRepository.existsByNumber(stolenCard.getNumber())).thenReturn(false);
        assertThatThrownBy(() -> underTest.deleteStolenCard(stolenCard.getNumber()))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void deleteStolenCard() {
        Mockito.when(stolenCardRepository.existsByNumber(stolenCard.getNumber())).thenReturn(true);
        Mockito.when(stolenCardRepository.findByNumber(stolenCard.getNumber())).thenReturn(stolenCard);

        underTest.deleteStolenCard(stolenCard.getNumber());
        ArgumentCaptor<StolenCard> stolenCardArgumentCaptor = ArgumentCaptor.forClass(StolenCard.class);
        verify(stolenCardRepository).delete(stolenCardArgumentCaptor.capture());
        StolenCard capturedStolenCard = stolenCardArgumentCaptor.getValue();

        assertThat(stolenCard).isEqualTo(capturedStolenCard);
    }

    @Test
    void listStolenCards() {
        underTest.listStolenCards();

        verify(stolenCardRepository).findAll(Sort.sort(StolenCard.class).by(StolenCard::getId).ascending());
    }

    @Test
    void addExistedSuspiciousIp() {
        Mockito.when(ipRepository.existsByIp(allowedTransaction.getIp())).thenReturn(true);

        Optional<IpHolder> ip = underTest.addSuspiciousIp(ipHolder);

        assertThat(ip).isNotPresent();
    }

    @Test
    void addSuspiciousIp() {
        Mockito.when(ipRepository.existsByIp(allowedTransaction.getIp())).thenReturn(false);
        Mockito.when(ipRepository.save(ipHolder)).thenReturn(ipHolder);

        Optional<IpHolder> ip = underTest.addSuspiciousIp(ipHolder);

        ArgumentCaptor<IpHolder> ipHolderArgumentCaptor = ArgumentCaptor.forClass(IpHolder.class);
        verify(ipRepository).save(ipHolderArgumentCaptor.capture());
        IpHolder capturedIpHolder = ipHolderArgumentCaptor.getValue();

        assertThat(ip).isPresent();
        assertThat(capturedIpHolder).isEqualTo(ipHolder);
    }

    @Test
    void deleteWrongSuspiciousIp() {
        assertThatThrownBy(() -> underTest.deleteSuspiciousIp("wrong"))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void deleteSuspiciousIp() {
        Mockito.when(ipRepository.deleteByIp(ipHolder.getIp())).thenReturn(1);

        boolean flag = underTest.deleteSuspiciousIp(ipHolder.getIp());

        assertThat(flag).isTrue();
    }

    @Test
    void listSuspiciousAddresses() {
        underTest.listSuspiciousAddresses();

        verify(ipRepository).findAll();
    }

    @Test
    void feedbackProcessTransactionNotExisted() {
        Mockito.when(transactionRepository.findByTransactionId(feedback.getTransactionId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.feedbackProcess(feedback))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void feedbackProcessFeedbackResultIsNotEmpty() {
        Mockito.when(transactionRepository.findByTransactionId(feedback.getTransactionId())).thenReturn(Optional.ofNullable(allowedTransaction));
        allowedTransaction.setFeedback("NOT EMPTY");


        assertThatThrownBy(() -> underTest.feedbackProcess(feedback))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void feedbackProcessFeedbackIsProhibitedResultIsAllowed() {
        Mockito.when(transactionRepository.findByTransactionId(feedback.getTransactionId())).thenReturn(Optional.ofNullable(allowedTransaction));
        allowedTransaction.setResult("ALLOWED");
        feedback.setFeedback(Result.PROHIBITED);

        Transaction transaction = underTest.feedbackProcess(feedback);

        assertThat(transaction.getFeedback()).isEqualTo(Result.PROHIBITED.name());
        verify(transactionRepository).save(transaction);
    }

    @Test
    void feedbackProcessFeedbackIsAllowedResultIsManual() {
        Mockito.when(transactionRepository.findByTransactionId(feedback.getTransactionId())).thenReturn(Optional.ofNullable(allowedTransaction));
        allowedTransaction.setResult("MANUAL_PROCESSING");
        feedback.setFeedback(Result.ALLOWED);

        Transaction transaction = underTest.feedbackProcess(feedback);

        assertThat(transaction.getFeedback()).isEqualTo(Result.ALLOWED.name());
        verify(transactionRepository).save(transaction);
    }

    @Test
    void feedbackProcessFeedbackIsProhibitedResultIsManual() {
        Mockito.when(transactionRepository.findByTransactionId(feedback.getTransactionId())).thenReturn(Optional.ofNullable(allowedTransaction));
        allowedTransaction.setResult("MANUAL_PROCESSING");
        feedback.setFeedback(Result.PROHIBITED);

        Transaction transaction = underTest.feedbackProcess(feedback);

        assertThat(transaction.getFeedback()).isEqualTo(Result.PROHIBITED.name());
        verify(transactionRepository).save(transaction);
    }

    @Test
    void feedbackProcessFeedbackIsAllowedResultIsProhibited() {
        Mockito.when(transactionRepository.findByTransactionId(feedback.getTransactionId())).thenReturn(Optional.ofNullable(allowedTransaction));
        allowedTransaction.setResult("PROHIBITED");
        feedback.setFeedback(Result.ALLOWED);

        Transaction transaction = underTest.feedbackProcess(feedback);

        assertThat(transaction.getFeedback()).isEqualTo(Result.ALLOWED.name());
        verify(transactionRepository).save(transaction);
    }

    @Test
    void feedbackProcessFeedbackIsManualResultIsProhibited() {
        Mockito.when(transactionRepository.findByTransactionId(feedback.getTransactionId())).thenReturn(Optional.ofNullable(allowedTransaction));
        allowedTransaction.setResult("PROHIBITED");
        feedback.setFeedback(Result.MANUAL_PROCESSING);

        Transaction transaction = underTest.feedbackProcess(feedback);

        assertThat(transaction.getFeedback()).isEqualTo(Result.MANUAL_PROCESSING.name());
        verify(transactionRepository).save(transaction);
    }

    @Test
    void feedbackProcessFeedbackIsEquals() {
        Mockito.when(transactionRepository.findByTransactionId(feedback.getTransactionId())).thenReturn(Optional.ofNullable(allowedTransaction));
        allowedTransaction.setResult("MANUAL_PROCESSING");

        assertThatThrownBy(() -> underTest.feedbackProcess(feedback))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void feedbackProcess() {
        Mockito.when(transactionRepository.findByTransactionId(feedback.getTransactionId())).thenReturn(Optional.ofNullable(allowedTransaction));

        Transaction transaction = underTest.feedbackProcess(feedback);

        assertThat(transaction.getFeedback()).isEqualTo(Result.MANUAL_PROCESSING.name());
        verify(transactionRepository).save(transaction);
    }



    @Test
    void history() {
        underTest.history();

        verify(transactionRepository).findAll(Sort.sort(Transaction.class)
                .by(Transaction::getTransactionId)
                .ascending());
    }

    @Test
    void historyByNotExistedCardNumber() {
        Mockito.when(transactionRepository.existsByNumber(stolenCard.getNumber())).thenReturn(false);

        assertThatThrownBy(() -> underTest.historyByCardNumber(stolenCard.getNumber()))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void historyByCardNumber() {
        Mockito.when(transactionRepository.existsByNumber(stolenCard.getNumber())).thenReturn(true);

        underTest.historyByCardNumber(stolenCard.getNumber());

        verify(transactionRepository).findAllByNumber(stolenCard.getNumber());
    }
}