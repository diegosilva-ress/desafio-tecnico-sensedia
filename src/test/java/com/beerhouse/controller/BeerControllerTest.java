package com.beerhouse.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;

import com.beerhouse.model.Beer;
import com.beerhouse.repository.BeerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class BeerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BeerRepository beerRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void getAllBeerTest() throws Exception {
		this.mockMvc.perform(get("/beers")).andExpect(status().isNotFound());
	}

	@Test
	public void getBeerNotFoundTest() throws Exception {
		this.mockMvc.perform(get("/beers", 99)).andExpect(status().isNotFound());
	}

	@Test
	public void postBeerTest() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
				.post("/beers")
				.content(objectMapper.writeValueAsString(
						createBeer(10L, "Test", "Test", "Test", new BigDecimal(10), "Test")))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

	@Test
	public void putBeerTest() throws Exception {
		Mockito.when(this.beerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(
				createBeer(1L, "Put test", "Put test", "Put test", new BigDecimal(10), "Put test")));

		this.mockMvc.perform( MockMvcRequestBuilders
				.put("/beers/{id}", 1L)
				.content(objectMapper.writeValueAsString(
						createBeer(1L, "Put", "Put", "Put", new BigDecimal(20), "Put")))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteBeerTest() throws Exception {
		Mockito.when(this.beerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(
				createBeer(1L, "Delete test", "Delete test", "Delete test", new BigDecimal(100), "Delete test")));

		this.mockMvc.perform( MockMvcRequestBuilders.delete("/beers/{id}", 1) )
				.andExpect(status().isNoContent());
	}

	public Beer createBeer(Long id, String name, String ingredients, String alcoholIngredients, BigDecimal price, String category) {
		Beer beer = new Beer();

		beer.setId(id);
		beer.setName(name);
		beer.setIngredients(ingredients);
		beer.setAlcoholContent(alcoholIngredients);
		beer.setPrice(price);
		beer.setCategory(category);

		return beer;
	}

}