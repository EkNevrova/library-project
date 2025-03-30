package ru.itgirl.libraryproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itgirl.libraryproject.model.Book;

public interface BookRepositary extends JpaRepository<Book, Long> {
}
