package ru.itgirl.libraryproject.service;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import ru.itgirl.libraryproject.dto.AuthorCreateDto;
import ru.itgirl.libraryproject.dto.AuthorDto;
import ru.itgirl.libraryproject.dto.AuthorUpdateDto;
import ru.itgirl.libraryproject.model.Author;
import ru.itgirl.libraryproject.model.Book;
import ru.itgirl.libraryproject.model.Genre;
import ru.itgirl.libraryproject.repository.AuthorRepository;
import ru.itgirl.libraryproject.service.impl.AuthorServiceImpl;

import java.util.*;


@SpringBootTest
public class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    public void testGetAuthorById() {
        Long id = 1L;
        String name = "John";
        String surname = "Doe";
        Set<Book> books = new HashSet<>();

        Author author = new Author(id, name, surname, books);

        when(authorRepository.findById(id)).thenReturn(Optional.of(author));

        AuthorDto authorDto = authorService.getAuthorById(id);

        verify(authorRepository).findById(id);
        Assertions.assertEquals(authorDto.getId(), author.getId());
        Assertions.assertEquals(authorDto.getName(), author.getName());
        Assertions.assertEquals(authorDto.getSurname(), author.getSurname());
    }

    @Test
    public void testGetAuthorByIdNotFound() {
        Long id = 1L;
        when(authorRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(NoSuchElementException.class, () -> authorService.getAuthorById(id));
        verify(authorRepository).findById(id);
    }

    @Test
    void testGetByNameV1() {
        Author author = new Author(1L, "John", "Doe", new HashSet<>());
        when(authorRepository.findAuthorByName("John")).thenReturn(Optional.of(author));

        AuthorDto dto = authorService.getByNameV1("John");

        Assertions.assertEquals("John", dto.getName());
        verify(authorRepository).findAuthorByName("John");
    }

    @Test
    void testGetByNameV1NotFound() {
        when(authorRepository.findAuthorByName("Матвей")).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> authorService.getByNameV1("Матвей"));
    }

    @Test
    void testGetByNameV2() {
        Author author = new Author(2L, "Jane", "Smith", new HashSet<>());
        when(authorRepository.findAuthorByNameBySql("Jane")).thenReturn(Optional.of(author));

        AuthorDto dto = authorService.getByNameV2("Jane");

        Assertions.assertEquals("Jane", dto.getName());
        verify(authorRepository).findAuthorByNameBySql("Jane");
    }

    @Test
    void testGetByNameV2NotFound() {
        when(authorRepository.findAuthorByNameBySql("Ghost")).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> authorService.getByNameV2("Ghost"));
    }

    @Test
    void testGetByNameV3() {
        Author author = new Author(3L, "Tom", "Hardy", new HashSet<>());
        when(authorRepository.findOne(any(Specification.class))).thenReturn(Optional.of(author));

        AuthorDto dto = authorService.getByNameV3("Tom");

        Assertions.assertEquals("Tom", dto.getName());
    }

    @Test
    void testCreateAuthor() {
        AuthorCreateDto dto = new AuthorCreateDto("Anna", "Bell");
        Author saved = new Author(10L, "Anna", "Bell", new HashSet<>());

        when(authorRepository.save(any())).thenReturn(saved);

        AuthorDto result = authorService.createAuthor(dto);

        Assertions.assertEquals("Anna", result.getName());
        verify(authorRepository).save(any());
    }

    @Test
    void testUpdateAuthor() {
        Author existing = new Author(1L, "Old", "Name", new HashSet<>());
        AuthorUpdateDto updateDto = new AuthorUpdateDto(1L, "New", "Surname");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(authorRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AuthorDto result = authorService.updateAuthor(updateDto);

        Assertions.assertEquals("New", result.getName());
        Assertions.assertEquals("Surname", result.getSurname());
    }

    @Test
    void testDeleteAuthor() {
        when(authorRepository.existsById(1L)).thenReturn(true);
        authorService.deleteAuthor(1L);
        verify(authorRepository).deleteById(1L);
    }

    @Test
    void testDeleteAuthorNotExists() {
        when(authorRepository.existsById(99L)).thenReturn(false);
        authorService.deleteAuthor(99L);
        verify(authorRepository, never()).deleteById(any());
    }

    @Test
    void testGetAllAuthors() {
        Genre genre = Genre.builder()
                .id(1L)
                .name("Фантастика")
                .build();

        Book book = Book.builder()
                .id(1L)
                .name("Book Name")
                .genre(genre)
                .build();

        Set<Book> books = new HashSet<>();
        books.add(book);

        Author author = Author.builder()
                .id(5L)
                .name("Emily")
                .surname("Clark")
                .books(books)
                .build();

        when(authorRepository.findAll()).thenReturn(List.of(author));

        List<AuthorDto> results = authorService.getAllAuthors();
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("Emily", results.get(0).getName());
        Assertions.assertEquals("Фантастика", results.get(0).getBooks().get(0).getGenre());
    }
}
