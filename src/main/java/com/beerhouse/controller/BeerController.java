package com.beerhouse.controller;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.beerhouse.model.Beer;
import com.beerhouse.repository.BeerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BeerController {

	private final BeerRepository beerRepository;

	@Autowired
	public BeerController(BeerRepository beerRepository) {
		this.beerRepository = beerRepository;
	}

	@GetMapping("/beers")
	public ResponseEntity<List<Beer>> getAll() {
		List<Beer> beerList = this.beerRepository.findAll();
		if(beerList.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(beerList, HttpStatus.OK);
	}

	@GetMapping("/beers/{id}")
	public ResponseEntity<Beer> getById(@PathVariable Long id) {
		Optional<Beer> beerOptional = this.beerRepository.findById(id);
		if(!beerOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(beerOptional.get(), HttpStatus.OK);
	}

	@PostMapping("/beers")
	public ResponseEntity<Beer> create(@RequestBody Beer beer) {
		return new ResponseEntity<>(this.beerRepository.save(beer), HttpStatus.CREATED);
	}

	@PutMapping("/beers/{id}")
	public ResponseEntity<Beer> update(@PathVariable Long id, @RequestBody Beer beer) {
		Optional<Beer> beerOptional = beerRepository.findById(id);
		if(!beerOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		beer.setId(id);
		return new ResponseEntity<>(this.beerRepository.save(beer), HttpStatus.OK);
	}

	@PatchMapping("/beers/{id}")
	public ResponseEntity<Beer> updatePatch(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
		Optional<Beer> beerOptional = beerRepository.findById(id);
		if(!beerOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		fields.forEach((k, v) -> {
			Field field = ReflectionUtils.findField(Beer.class, k);
			field.setAccessible(true);
			if(field.getName().equals("price")) {
				ReflectionUtils.setField(field, beerOptional.get(), new BigDecimal(v.toString()));
			} else {
				ReflectionUtils.setField(field, beerOptional.get(), v);
			}
		});
		return new ResponseEntity<>(this.beerRepository.save(beerOptional.get()), HttpStatus.OK);
	}

	@DeleteMapping("/beers/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Optional<Beer> beerOptional = beerRepository.findById(id);
		if(!beerOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		this.beerRepository.delete(beerOptional.get());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
