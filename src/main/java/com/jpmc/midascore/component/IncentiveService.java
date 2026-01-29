package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveService {

  private final RestTemplate restTemplate;
  private static final String INCENTIVE_URL = "http://localhost:8080/incentive";

  public IncentiveService(RestTemplateBuilder builder) {
    this.restTemplate = builder.build();
  }

  public Incentive getIncentive(Transaction transaction) {
    try {
      return restTemplate.postForObject(INCENTIVE_URL, transaction, Incentive.class);
    } catch (Exception e) {
      return new Incentive(0);
    }
  }
}