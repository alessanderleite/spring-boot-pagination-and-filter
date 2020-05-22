package com.example.app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.model.Tutorial;
import com.example.app.repository.TutorialRepository;

@RestController
@RequestMapping("/api")
public class TutorialController {

	@Autowired
	TutorialRepository tutorialRepository;
	
	@GetMapping("/tutorials")
	public ResponseEntity<Map<String, Object>> getAllTutorials(
			@RequestParam(required = false) String title,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size
			) {
		
		try {
			List<Tutorial> tutorials = new ArrayList<>();
			Pageable paging = PageRequest.of(page, size);
			
			Page<Tutorial> pageTutorials;
			if (title == null) {
				pageTutorials = tutorialRepository.findAll(paging);
			} else {
				pageTutorials = tutorialRepository.findByTitleContaining(title, paging);
			}
			
			tutorials = pageTutorials.getContent();
			
			if (tutorials.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			
			Map<String, Object> response = new HashMap<>();
			response.put("tutorials", tutorials);
			response.put("currentPage", pageTutorials.getNumber());
			response.put("totalItems", pageTutorials.getTotalElements());
			response.put("totalPages", pageTutorials.getTotalPages());
			
			return new ResponseEntity<>(response, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/tutorials/published")
	public ResponseEntity<Map<String, Object>> findByPublished(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size) {
		try {
			List<Tutorial> tutorials = new ArrayList<>();
			Pageable paging = PageRequest.of(page, size);
			
			Page<Tutorial> pageTutorials = tutorialRepository.findByPublished(true, paging);
			tutorials = pageTutorials.getContent();
			
			if (tutorials.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			
			Map<String, Object> response = new HashMap<>();
			response.put("tutorials", tutorials);
			response.put("currentPage", pageTutorials.getNumber());
			response.put("totalItems", pageTutorials.getTotalElements());
			response.put("totalPages", pageTutorials.getTotalPages());
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@GetMapping("/tutorials/{id}")
	public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
		Optional<Tutorial> tutorialData = tutorialRepository.findById(id);
		
		if (tutorialData.isPresent()) {
			return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/tutorials")
	public ResponseEntity<Tutorial> createTutorial(@Valid @RequestBody Tutorial tutorial) {
		 try {
			Tutorial _tutorial = tutorialRepository.save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(), tutorial.isPublished()));
			return new ResponseEntity<>(_tutorial, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@PutMapping("/tutorials/{id}")
	public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") long id, @Valid @RequestBody Tutorial tutorial) {
		Optional<Tutorial> tutorialData = tutorialRepository.findById(id);
		
		if (tutorialData.isPresent()) {
			Tutorial _tutorial = tutorialData.get();
			_tutorial.setTitle(tutorial.getTitle());
			_tutorial.setDescription(tutorial.getDescription());
			_tutorial.setPublished(tutorial.isPublished());
			return new ResponseEntity<>(tutorialRepository.save(_tutorial), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping("/tutorials/{id}")
	public ResponseEntity<HttpStatus> deleteTutorial(@PathVariable("id") long id) {
		try {
			tutorialRepository.deleteById(id);
			return new  ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@DeleteMapping("/tutorials")
	public ResponseEntity<HttpStatus> deleteAllTutorials() {
		try {
			tutorialRepository.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}
}
