package com.mike.TelegramRummikub;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SSLAuthController {
	@GetMapping("/.well-known/pki-validation/{filename}")
	public ResponseEntity<Resource> getTextFile(@PathVariable String filename) {
		// Load the file as a resource
		ClassPathResource resource = new ClassPathResource(filename);
		
		// Read file content
		
		// Set response headers
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
		
		// Return file content as response
		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}
}