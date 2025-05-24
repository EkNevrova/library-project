package ru.itgirl.libraryproject.service.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.itgirl.libraryproject.dto.AuthorCreateDto;
import ru.itgirl.libraryproject.dto.AuthorDto;
import ru.itgirl.libraryproject.dto.AuthorUpdateDto;
import ru.itgirl.libraryproject.dto.BookDto;
import ru.itgirl.libraryproject.model.Author;
import ru.itgirl.libraryproject.repository.AuthorRepository;
import ru.itgirl.libraryproject.service.AuthorService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public AuthorDto getAuthorById(Long id) {
        log.info("Пытаемся найти автора по id {}", id);
        Optional<Author> author = authorRepository.findById(id);
        if (author.isPresent()) {
            AuthorDto authorDto = convertToDto(author.get());
            log.info("getAuthorById - Автор: {}", authorDto.toString());
            return authorDto;
        } else {
            log.error("getAuthorById - Автор с id {} не найден", id);
            throw new NoSuchElementException("getAuthorById - Значение отсутствует");
        }
    }

    @Override
    public AuthorDto getByNameV1(String name) {
        log.info("getByNameV1 - Пытаемся найти автора по имени {}", name);
        Optional<Author> author = authorRepository.findAuthorByName(name);
        if (author.isPresent()) {
            AuthorDto authorDto = convertToDto(author.get());
            log.info("getByNameV1 - Автор: {}", authorDto);
            return authorDto;
        } else {
            log.error("getByNameV1 - Автор с именем {} не найден", name);
            throw new NoSuchElementException("getByName - Автор с именем '" + name + "' не найден");
        }
    }

    @Override
    public AuthorDto getByNameV2(String name) {
        log.info("getByNameV2 - Пытаемся найти автора по имени используя SQL: {}", name);
        Author author = authorRepository.findAuthorByNameBySql(name)
                .orElseThrow(() -> {
                    log.error("getByNameV2 - Автор не найден с именем: {}", name);
                    return new RuntimeException("Автор не найден с именем: " + name);
                });
        AuthorDto authorDto = convertToDto(author);
        log.info("getByNameV2 - Автор найден и конвертирован в DTO: {}", authorDto);
        return authorDto;
    }

    @Override
    public AuthorDto getByNameV3(String name) {
        log.info("getByNameV3 - Пытаемся найти автора по имени используя Specification: {}", name);
        Specification<Author> specification = Specification.where(new Specification<Author>() {
            @Override
            public Predicate toPredicate(Root<Author> root,
                                         CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("name"), name);
            }
        });

        Author author = authorRepository.findOne(specification)
                .orElseThrow(() -> {
                    log.error("getByNameV3 - Автор не найден с именем: {}", name);
                    return new RuntimeException("getByNameV3 - Автор не найден с именем: " + name);
                });

        AuthorDto authorDto = convertToDto(author);
        log.info("getByNameV3 - Автор найден: {}", authorDto);
        return authorDto;
    }


    private Author convertDtoToEntity(AuthorCreateDto authorCreateDto) {
        return Author.builder()
                .name(authorCreateDto.getName())
                .surname(authorCreateDto.getSurname())
                .build();
    }

    @Override
    public AuthorDto createAuthor(AuthorCreateDto authorCreateDto) {
        log.info("createAuthor - Received request to create author: {}", authorCreateDto);
        Author authorEntity = convertDtoToEntity(authorCreateDto);
        log.debug("createAuthor - Converted DTO to entity: {}", authorEntity);
        Author savedAuthor = authorRepository.save(authorEntity);
        log.info("createAuthor - Author saved with ID: {}", savedAuthor.getId());
        AuthorDto authorDto = convertToDto(savedAuthor);
        log.debug("createAuthor - Converted entity to DTO: {}", authorDto);
        return authorDto;
    }

    @Override
    public AuthorDto updateAuthor(AuthorUpdateDto authorUpdateDto) {
        log.info("updateAuthor - Received update request for author ID: {}", authorUpdateDto.getId());
        Author author = authorRepository.findById(authorUpdateDto.getId())
                .orElseThrow(() -> {
                    log.error("updateAuthor - Author with ID {} not found", authorUpdateDto.getId());
                    return new NoSuchElementException("Author not found");
                });
        log.debug("updateAuthor - Found author: {}", author);
        author.setName(authorUpdateDto.getName());
        author.setSurname(authorUpdateDto.getSurname());
        log.debug("updateAuthor - Updated author entity with new data: name={}, surname={}",
                author.getName(), author.getSurname());
        Author savedAuthor = authorRepository.save(author);
        log.info("updateAuthor - Author with ID {} updated successfully", savedAuthor.getId());
        AuthorDto authorDto = convertToDto(savedAuthor);
        log.debug("updateAuthor - Converted updated entity to DTO: {}", authorDto);
        return authorDto;
    }

    @Override
    public void deleteAuthor(Long id) {
        log.info("deleteAuthor - Attempting to delete author with ID: {}", id);
        if (authorRepository.existsById(id)) {
            authorRepository.deleteById(id);
            log.info("deleteAuthor - Author with ID {} deleted", id);
        } else {
            log.warn("deleteAuthor - Author with ID {} does not exist", id);
        }
    }

    @Override
    public List<AuthorDto> getAllAuthors() {
        log.info("getAllAuthors - Retrieving all authors from the repository");
        List<Author> authors = authorRepository.findAll();
        log.info("getAllAuthors - Found {} authors", authors.size());
        List<AuthorDto> authorDtos = authors.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        log.debug("getAllAuthors - Converted authors to DTOs: {}", authorDtos);
        return authorDtos;
    }

    private AuthorDto convertToDto(Author author) {
        List<BookDto> bookDtoList = null;
        if (author.getBooks() != null) {
            bookDtoList = author.getBooks().stream()
                    .map(book -> BookDto.builder()
                            .genre(book.getGenre() != null ? book.getGenre().getName() : null) // ← исправлено
                            .name(book.getName())
                            .id(book.getId())
                            .build())
                    .collect(Collectors.toList());
        }
        return AuthorDto.builder()
                .id(author.getId())
                .name(author.getName())
                .surname(author.getSurname())
                .books(bookDtoList)
                .build();
    }
}
