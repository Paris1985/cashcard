package org.cashcard.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "CASHCARD")
public record CashCard(@Id Integer id, Double amount){
}