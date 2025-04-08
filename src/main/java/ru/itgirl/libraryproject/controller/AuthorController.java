package ru.itgirl.libraryproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itgirl.libraryproject.dto.AuthorDto;
import ru.itgirl.libraryproject.service.AuthorService;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/{id}")
    public AuthorDto getAuthorById(@PathVariable Long id) {
        return authorService.getAuthorById(id);
    }

    @GetMapping("/search")
    AuthorDto getAuthorByName(@RequestParam("name") String name) {
        return authorService.getByNameV1(name);
    }

    @GetMapping("/v2")
    AuthorDto getAuthorByNameV2(@RequestParam("name") String name) {
        return authorService.getByNameV2(name);
    }

    @GetMapping("/v3")
    AuthorDto getAuthorByNameV3(@RequestParam("name") String name) {
        return authorService.getByNameV3(name);
    }
}
