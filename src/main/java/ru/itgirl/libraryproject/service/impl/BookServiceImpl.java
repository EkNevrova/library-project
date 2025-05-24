package ru.itgirl.libraryproject.service.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.itgirl.libraryproject.dto.AuthorDto;
import ru.itgirl.libraryproject.dto.BookCreateDto;
import ru.itgirl.libraryproject.dto.BookDto;
import ru.itgirl.libraryproject.dto.BookUpdateDto;
import ru.itgirl.libraryproject.model.Book;
import ru.itgirl.libraryproject.model.Genre;
import ru.itgirl.libraryproject.repository.BookRepository;
import ru.itgirl.libraryproject.repository.GenreRepository;
import ru.itgirl.libraryproject.service.BookService;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;

    @Override
    public BookDto getByNameV1(String name) {
        log.info("getByNameV1 - Получение книги по имени: {}", name);
        Book book = bookRepository.findBookByName(name).orElseThrow();
        return convertEntityToDto(book);
    }

    @Override
    public BookDto getByNameV2(String name) {
        log.info("getByNameV2 - Получение книги по имени с помощью SQL: {}", name);
        Book book = bookRepository.findBookByNameBySql(name).orElseThrow();
        return convertEntityToDto(book);
    }

    @Override
    public BookDto getByNameV3(String name) {
        log.info("getByNameV3 - Получение книги по имени с помощью Specification: {}", name);
        Specification<Book> bookSpecification = Specification.where(new Specification<Book>() {
            @Override
            public Predicate toPredicate(Root<Book> root,
                                         CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("name"), name);
            }
        });
        Book book = bookRepository.findOne(bookSpecification).orElseThrow();
        return convertEntityToDto(book);
    }

    @Override
    public BookDto createBook(BookCreateDto bookCreateDTO) {
        log.info("Создание новой книги: {}", bookCreateDTO.getName());
        Book book = bookRepository.save(convertDtoToEntity(bookCreateDTO));
        BookDto bookDto = convertEntityToDto(book);
        log.info("Книга создана с ID: {}", bookDto.getId());
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookUpdateDto bookUpdateDto) {
        log.info("Обновление книги с ID: {}", bookUpdateDto.getId());
        Book book = bookRepository.findById(bookUpdateDto.getId()).orElseThrow();
        book.setName(bookUpdateDto.getName());

        Genre genre = genreRepository.findGenreByName(bookUpdateDto.getGenre())
                .orElseThrow(() ->{
                    log.error("Жанр не найден: {}", bookUpdateDto.getGenre());
                    return new RuntimeException("Жанр не найден");
                });
        book.setGenre(genre);

        Book savedBook = bookRepository.save(book);
        log.info("Книга обновлена: {}", savedBook.getId());
        return convertEntityToDto(savedBook);
    }

    @Override
    public void deleteBook(Long id) {
        log.info("Удаление книги с ID: {}", id);
        bookRepository.deleteById(id);
        log.info("Книга с ID {} удалена", id);
    }

    @Override
    public List<BookDto> getAllBooks() {
        log.info("Получение всех книг");
        List<Book> books = bookRepository.findAll();
        log.info("Найдено {} книг", books.size());
        return books.stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    private BookDto convertEntityToDto(Book book) {
        List<AuthorDto> authorDtoList = null;

        if (book.getAuthors() != null) {
            authorDtoList = book.getAuthors()
                    .stream()
                    .map(author -> AuthorDto.builder()
                            .name(author.getName())
                            .surname(author.getSurname())
                            .id(author.getId())
                            .build())
                    .toList();
        }
        BookDto bookDto = BookDto.builder()
                .id(book.getId())
                .name(book.getName())
                .genre(book.getGenre() != null ? book.getGenre().getName() : null)
                .authors(authorDtoList)
                .build();
        return bookDto;
    }

    private Book convertDtoToEntity(BookCreateDto bookCreateDto) {
        Genre genre = genreRepository.findGenreByName(bookCreateDto.getGenre())
                .orElseThrow(() -> {
                    log.error("Жанр не найден при создании книги: {}", bookCreateDto.getGenre());
                    return new RuntimeException("Жанр не найден");
                });

        return Book.builder()
                .name(bookCreateDto.getName())
                .genre(genre)
                .build();
    }
}
