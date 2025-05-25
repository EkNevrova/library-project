package ru.itgirl.libraryproject.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import ru.itgirl.libraryproject.dto.AuthorDto;
import ru.itgirl.libraryproject.dto.BookCreateDto;
import ru.itgirl.libraryproject.dto.BookDto;
import ru.itgirl.libraryproject.dto.BookUpdateDto;
import ru.itgirl.libraryproject.model.Author;
import ru.itgirl.libraryproject.model.Book;
import ru.itgirl.libraryproject.model.Genre;
import ru.itgirl.libraryproject.repository.BookRepository;
import ru.itgirl.libraryproject.repository.GenreRepository;
import ru.itgirl.libraryproject.service.impl.BookServiceImpl;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void testGetByNameV1() {
        Genre genre = Genre.builder()
                .id(1L)
                .name("Фантастика")
                .build();

        Book book = Book.builder()
                .id(1L)
                .name("Туманность Андромеды")
                .genre(genre)
                .authors(new HashSet<>())
                .build();

        when(bookRepository.findBookByName("Туманность Андромеды")).thenReturn(Optional.of(book));

        BookDto dto = bookService.getByNameV1("Туманность Андромеды");

        Assertions.assertEquals("Туманность Андромеды", dto.getName());
    }

    @Test
    void testGetByNameV1NotFound() {
        when(bookRepository.findBookByName("Missing")).thenReturn(Optional.empty());
        Assertions.assertThrows(NoSuchElementException.class, () -> bookService.getByNameV1("Missing"));
    }

    @Test
    void testGetByNameV2() {
        Genre genre = Genre.builder()
                .id(2L)
                .name("Tech")
                .build();

        Book book = Book.builder()
                .id(2L)
                .name("SQL Book")
                .genre(genre)
                .authors(new HashSet<>())
                .build();

        when(bookRepository.findBookByNameBySql("SQL Book")).thenReturn(Optional.of(book));

        BookDto dto = bookService.getByNameV2("SQL Book");

        Assertions.assertEquals("SQL Book", dto.getName());
    }

    @Test
    void testGetByNameV3() {
        Genre genre = Genre.builder()
                .id(3L)
                .name("Драма")
                .build();

        Book book = Book.builder()
                .id(3L)
                .name("Дядя Ваня")
                .genre(genre)
                .authors(new HashSet<>())
                .build();

        when(bookRepository.findOne(any(Specification.class))).thenReturn(Optional.of(book));

        BookDto dto = bookService.getByNameV3("Дядя Ваня");

        Assertions.assertEquals("Дядя Ваня", dto.getName());
    }

    @Test
    void testCreateBook() {
        BookCreateDto bookCreateDto = new BookCreateDto();
        bookCreateDto.setName("Test Book");
        bookCreateDto.setGenre("Комедия");

        Genre genre = Genre.builder()
                .id(1L)
                .name("Комедия")
                .build();

        Book book = Book.builder()
                .id(1L)
                .name("Test Book")
                .genre(genre)
                .build();

        when(genreRepository.findGenreByName("Комедия")).thenReturn(Optional.of(genre));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookDto result = bookService.createBook(bookCreateDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Test Book", result.getName());
        Assertions.assertEquals("Fiction", result.getGenre());

        verify(genreRepository).findGenreByName("Fiction");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testUpdateBook() {

        BookUpdateDto updateDto = new BookUpdateDto();
        updateDto.setId(7L);
        updateDto.setName("Updated Book");
        updateDto.setGenre("UpdatedGenre");

        Genre oldGenre = Genre.builder()
                .id(1L)
                .name("OldGenre")
                .build();

        Genre newGenre = Genre.builder()
                .id(15L)
                .name("UpdatedGenre")
                .build();

        Book book = Book.builder()
                .id(7L)
                .name("Old Book")
                .genre(oldGenre)
                .build();

        when(bookRepository.findById(7L)).thenReturn(Optional.of(book));
        when(genreRepository.findGenreByName("UpdatedGenre")).thenReturn(Optional.of(newGenre));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookDto result = bookService.updateBook(updateDto);

        Assertions.assertEquals("Updated Book", result.getName());
        Assertions.assertEquals("UpdatedGenre", result.getGenre());
    }

    @Test
    void testDeleteBook() {
        Long id = 100L;
        doNothing().when(bookRepository).deleteById(id);

        bookService.deleteBook(id);

        verify(bookRepository).deleteById(id);
    }

    @Test
    void testGetAllBooks() {
        Genre genre = Genre.builder()
                .id(1L)
                .name("Поэма")
                .build();

        Book book1 = Book.builder()
                .id(1L)
                .name("Евгений Онегин")
                .genre(genre)
                .authors(new HashSet<>())
                .build();

        Book book2 = Book.builder()
                .id(2L)
                .name("Мцыри")
                .genre(genre)
                .authors(new HashSet<>())
                .build();

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        List<BookDto> results = bookService.getAllBooks();

        Assertions.assertEquals(2, results.size());
        Assertions.assertEquals("Евгений Онегин", results.get(0).getName());
        Assertions.assertEquals("Мцыри", results.get(1).getName());
    }
}
