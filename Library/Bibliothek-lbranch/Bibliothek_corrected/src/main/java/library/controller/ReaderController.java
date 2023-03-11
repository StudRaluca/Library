package library.controller;


import library.model.Book;
import library.model.Reader;
import library.repository.databaseRepo.DatabaseReaderRepository;
import library.repository.interfaces.BookRepository;
import library.repository.interfaces.ReaderRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ReaderController {
    private static ReaderRepository readerRepository;
    private static BookRepository bookRepository;

    public ReaderController(ReaderRepository readerRepository, BookRepository bookRepository) {
        ReaderController.readerRepository = readerRepository;
        ReaderController.bookRepository = bookRepository;
    }

    /**
     * checks if the username of the reader is in the database
     *
     * @param username username of the reader
     * @return reader with this username
     * @throws NullPointerException if the reader with this username is not found
     */
    public static Reader checkUsernameReader(String username) throws NullPointerException {
        return readerRepository.findByUsername(username);
    }

    /**
     * This method sorts the readers from the reader repository by lastname in ascending or
     * descending order.
     *
     * @param ascending This boolean parameter determines if the readers will be sorted by lastname
     *                  in ascending or descending order. If true, the sorting will be in ascending
     *                  order, otherwise it will be in descending order.
     * @return A list with the sorted readers.
     */

    public List<Reader> sortReadersByLastName(boolean ascending) {
        List<Reader> sortedReadersByLastName = new ArrayList<>(List.copyOf(readerRepository.findAll()));
        sortedReadersByLastName.sort((Reader r1, Reader r2) -> r1.getLastName().compareToIgnoreCase(r2.getLastName()));
        if (!ascending) {
            Collections.reverse(sortedReadersByLastName);
        }
        return sortedReadersByLastName;
    }

    /**
     * reserve a book by reader
     *
     * @param book   book to be reserved
     * @param reader reader who wants to reserve the book
     * @return satatus message
     */
    public String reserveBook(Book book, Reader reader) {
        for (Book b : bookRepository.findAll()) {
            if (b.getBookId() == book.getBookId()) {
                b.setNrOfActualExemplars(b.getNrOfActualExemplars() - 1); // the actual nr of exemplars is lower
            }
        }
        if (reader.getTotalBooks() == 5)
            throw new IllegalArgumentException("You're not allowed to reserve a book because you have already 5 borrowed books");
        else {
            reader.reservedBooks.add(book);
            if (readerRepository.getClass() == DatabaseReaderRepository.class) {
                EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                entityManager.getTransaction().begin();

                Query query1 = entityManager.createNativeQuery("update Book set nrOfActualExemplars=:numar where bookId=:b");
                query1.setParameter("b", book.getBookId());
                query1.setParameter("numar", book.getNrOfActualExemplars());
                query1.executeUpdate();

                Query query = entityManager.createNativeQuery("insert into reservedBook_reader values (?,?)");
                query.setParameter(1, reader.getUserId());
                query.setParameter(2, book.getBookId());
                query.executeUpdate();

                entityManager.getTransaction().commit();
            }
            return "The book is now successfully reserved!";
        }
    }
}
