package com.example.demo.note;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

	private final NoteRepository notes;

	public NoteController(NoteRepository notes) {
		this.notes = notes;
	}

	@GetMapping
	public List<Note> list() {
		return notes.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	@GetMapping("/{id}")
	public Note get(@PathVariable Long id) {
		return find(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Note create(@Valid @RequestBody Note note) {
		note.setId(null);
		return notes.save(note);
	}

	@PutMapping("/{id}")
	public Note update(@PathVariable Long id, @Valid @RequestBody Note input) {
		Note note = find(id);
		note.setTitle(input.getTitle());
		note.setBody(input.getBody());
		return notes.save(note);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		if (!notes.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		notes.deleteById(id);
	}

	private Note find(Long id) {
		return notes.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

}
