package org.cashcard.controller;

import org.cashcard.domain.CashCard;
import org.cashcard.repository.CashCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {


    @Autowired
    private CashCardRepository cashCardRepository;

    @GetMapping
    public ResponseEntity<List<CashCard>> getCashCards(Pageable pageable) {
        Page<CashCard> page = cashCardRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.DESC, "amount"))));
        return ResponseEntity.ok(page.getContent());
    }
    @GetMapping("/{id}")
    public ResponseEntity<CashCard> findById(@PathVariable Integer id) {
        Optional<CashCard> cashCardOpt = cashCardRepository.findById(id);
        if(cashCardOpt.isPresent()){
            return ResponseEntity.ok(cashCardOpt.get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCard, UriComponentsBuilder ucb) {
        CashCard savedCashCard = cashCardRepository.save(newCashCard);
        URI locationOfCreateCashCard = ucb.path("cashcards/{id}").buildAndExpand(savedCashCard.id()).toUri();
        return ResponseEntity.created(locationOfCreateCashCard).build();
    }

}
