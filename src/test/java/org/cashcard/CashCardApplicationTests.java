package org.cashcard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.cashcard.domain.CashCard;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;

	@Test
	void shouldReturnACashCardWhenDataIsSaved() throws JsonProcessingException {
		ResponseEntity<CashCard> response = restTemplate.getForEntity("/cashcards/1", CashCard.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

	    ObjectMapper objectMapper = new ObjectMapper();
		String cashCardJson = objectMapper.writeValueAsString(response.getBody());
		DocumentContext documentContext = JsonPath.parse(cashCardJson);
	    assertThat((Integer) documentContext.read("$.id")).isEqualTo(1);
		assertThat((Double) documentContext.read("$.amount")).isEqualTo(0.19);
	}

	@Test
	void shouldNotReturnACashCardWithAnUnknownId() {
		ResponseEntity<CashCard> response = restTemplate.getForEntity("/cashcards/100", CashCard.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldCreateANewCashCard() throws IOException {
		CashCard newCashCard = new CashCard(null, 345.50);

		ResponseEntity<Void> cashCardResponseEntity = restTemplate.postForEntity("/cashcards", newCashCard, Void.class);

		assertThat(cashCardResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI location = cashCardResponseEntity.getHeaders().getLocation();

		ResponseEntity<CashCard> saveCashCardEntity = restTemplate.getForEntity(location, CashCard.class);

		ObjectMapper objectMapper = new ObjectMapper();
		String saveCashCardJson = objectMapper.writeValueAsString(saveCashCardEntity.getBody());

		DocumentContext documentContext = JsonPath.parse(saveCashCardJson);
		assertThat((Integer) documentContext.read("$.id")).isNotNull();
		assertThat((Double) documentContext.read("$.amount")).isEqualTo(345.50);
	}

	@Test
	void shouldReturnPageByPage() {
		//given total size = 5
		//first page
		ResponseEntity<List<CashCard>> firstResponseEntity = restTemplate.exchange("/cashcards?page=0&size=3", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
		});
		assertThat(firstResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(firstResponseEntity.getBody().size()).isEqualTo(3);

		//second page
		ResponseEntity<List<CashCard>> sencodResponseEntity = restTemplate.exchange("/cashcards?page=1&size=3", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
		});
		assertThat(sencodResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(sencodResponseEntity.getBody().size()).isEqualTo(2);
	}
	@Test
	void shouldReturnInOrderByAmountDescAsDefault() {
		ResponseEntity<List<CashCard>> responseEntity = restTemplate.exchange("/cashcards?page=0&size=3", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
		});
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody().get(0).amount()).isEqualTo(1000.00);
		assertThat(responseEntity.getBody().get(1).amount()).isEqualTo(50.00);
		assertThat(responseEntity.getBody().get(2).amount()).isEqualTo(20.00);
	}
	@Test
	void shouldReturnInOrderByAmountAsc() {
		ResponseEntity<List<CashCard>> responseEntity = restTemplate.exchange("/cashcards?page=0&size=3&sort=amount,ASC", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
		});
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody().get(0).amount()).isEqualTo(0.19);
		assertThat(responseEntity.getBody().get(1).amount()).isEqualTo(10.00);
		assertThat(responseEntity.getBody().get(2).amount()).isEqualTo(20.00);
	}

}
