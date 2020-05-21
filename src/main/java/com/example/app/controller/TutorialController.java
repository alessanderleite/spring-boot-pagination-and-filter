package com.example.app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
