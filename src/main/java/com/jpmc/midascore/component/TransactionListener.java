package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

  private final TransactionService transactionService;
  private final UserRepository userRepository;

  public TransactionListener(TransactionService transactionService, UserRepository userRepository) {
    this.transactionService = transactionService;
    this.userRepository = userRepository;
  }

  @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
  public void listen(Transaction transaction) {
    transactionService.processTransaction(transaction);

    System.out.println("=== CURRENT BALANCES ===");
    for (UserRecord user : userRepository.findAll()) {
      System.out.println(user.getName() + ": " + user.getBalance());
    }
    System.out.println("========================");
  }
}