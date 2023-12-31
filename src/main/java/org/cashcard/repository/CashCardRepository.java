package org.cashcard.repository;

import org.cashcard.domain.CashCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashCardRepository extends CrudRepository<CashCard, Integer>, PagingAndSortingRepository<CashCard, Integer> {
}
