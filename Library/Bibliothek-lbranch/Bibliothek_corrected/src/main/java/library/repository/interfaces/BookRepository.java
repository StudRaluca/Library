package library.repository.interfaces;

import library.model.Book;

import java.util.List;


public interface BookRepository extends ICrudRepository<Integer, Book> {
    int count(); // returns the number of books
    List<Book> findByAuthor(String firstName, String lastName);
    Book findByTitle(String title);
}
