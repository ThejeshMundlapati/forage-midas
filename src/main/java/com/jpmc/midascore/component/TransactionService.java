package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

  private final UserRepository userRepository;
  private final TransactionRecordRepository transactionRecordRepository;
  private final IncentiveService incentiveService;

  public TransactionService(UserRepository userRepository,
                            TransactionRecordRepository transactionRecordRepository,
                            IncentiveService incentiveService) {
    this.userRepository = userRepository;
    this.transactionRecordRepository = transactionRecordRepository;
    this.incentiveService = incentiveService;
  }

  @Transactional
  public boolean processTransaction(Transaction transaction) {
    UserRecord sender = userRepository.findById(transaction.getSenderId());
    UserRecord recipient = userRepository.findById(transaction.getRecipientId());

    if (sender == null || recipient == null) {
      return false;
    }

    if (sender.getBalance() < transaction.getAmount()) {
      return false;
    }

    Incentive incentive = incentiveService.getIncentive(transaction);
    float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0;

    sender.setBalance(sender.getBalance() - transaction.getAmount());
    recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

    userRepository.save(sender);
    userRepository.save(recipient);

    TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
    transactionRecordRepository.save(record);

    return true;
  }
}