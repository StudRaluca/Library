package library.tests;

import library.controller.*;
import library.exceptions.*;
import library.model.*;
import library.repository.interfaces.*;
import library.repository.memoryRepo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {

    LibrarianController librarianController;
    ReaderController readerController;
    ReaderRepository readerRepository;
    LibrarianRepository librarianRepository;
    BookRepository bookRepository;
    AuthorRepository authorRepository;

    @BeforeEach
    void setUpData(){
        authorRepository = new InMemoryAuthorRepository();
        bookRepository = new InMemoryBookRepository();
        librarianRepository = new InMemoryLibrarianRepository();
        readerRepository = new InMemoryReaderRepository();
        librarianController = new LibrarianController(librarianRepository, readerRepository, authorRepository, bookRepository);
        readerController = new ReaderController(readerRepository, bookRepository);
    }

    @Test
    @DisplayName("Test for populate function")
    void testPopulate(){
        librarianController.populate();
        assertEquals(3, bookRepository.findAll().size() ,"Populate book repository");
        assertEquals(3, readerRepository.findAll().size(), "Populate reader repository");
        assertEquals(3, librarianRepository.findAll().size(), "Populate librarian repository");
        assertEquals(2, authorRepository.findAll().size(), "Populate author repository");
    }

    @Test
    @DisplayName("Tests for checking methods")
    void testCheckMethods() throws DatabaseObjectException, InvalidActionException {
        List<Book> emptylist = new ArrayList<>();
        Author author = new Author("Ion","Creanga", emptylist);
        author.setAuthorId(1);
        authorRepository.add(author);
        librarianController.checkAuthor("Ion Creanga");
        assertInstanceOf(Author.class, librarianController.checkAuthor("Ion Creanga"), "Return an author");
        assertEquals(author, librarianController.checkAuthor("Ion Creanga"), "Return the searched author");

        List<Reader> readersFirstBook = new ArrayList<>();
        Book book = new Book("Povestea lui Harap-Alb", author, 1877, readersFirstBook, 7, 1);
        book.setBookId(1);
        bookRepository.add(book);
        assertInstanceOf(Book.class, librarianController.checkBook("Povestea lui Harap-Alb"), "Return a book");
        assertEquals(book, librarianController.checkBook("Povestea lui Harap-Alb"), "Return the searched book");

        List<String> emptydatelist = new ArrayList<>();
        Reader reader = new Reader("Maria", "Straton","mstraton88", "1234", emptylist, emptydatelist, 0, emptylist);
        reader.setUserId(1);
        readerRepository.add(reader);
        librarianController.allReaders();
        assertInstanceOf(Reader.class, librarianController.checkReader(1),"Return a reader");
        assertEquals(reader, librarianController.checkReader(1),"Return the searched reader");

        List<Reader> readerList = new ArrayList<>();
        Librarian librarian = new Librarian("Ana", "Pop","parola", "AnaPop", "2022-11-20 10:03", "2022-11-20 18:00", 5000, readerList);
        librarian.setUserId(1);
        librarianRepository.add(librarian);
        assertInstanceOf(Librarian.class, LibrarianController.checkUsernameLibrarian("AnaPop"), "Return a librarian");
        assertEquals(librarian, LibrarianController.checkUsernameLibrarian("AnaPop"),"Return the searched librarian");
    }

    @Test
    @DisplayName("Test for borrowBook")
    void testBorrowBook() throws InvalidActionException {
        librarianController.populate();

        Reader reader = readerRepository.findbyID(1); // reader has total nr of books < 5 => program should run without any exception
        int initialNrOfBooks = reader.getTotalBooks();

        String bookTitle = "Patul lui Procust"; // the book is in the database => program should run without any exception
        Book book = bookRepository.findByTitle("Patul lui Procust");
        int nrOfExemplarsBefore = book.getNrOfActualExemplars();
        int nrOfReaders = book.getBorrowedByReaders().size();

        Author author = authorRepository.findByName("Camil Petrescu");

        librarianController.borrowBook(reader,bookTitle,author); // testing the actual borrow
        assertTrue(reader.getBorrowedBooks().contains(book));
        assertTrue(reader.getTotalBooks() > initialNrOfBooks);
        assertEquals(nrOfExemplarsBefore-1, book.getNrOfActualExemplars());
        assertTrue(book.getBorrowedByReaders().contains(reader));
        assertEquals(nrOfReaders+1, book.getBorrowedByReaders().size());

        reader.setTotalBooks(5); // reader is not able to borrow => exception
        Exception exception = assertThrows(InvalidActionException.class, () -> {
            librarianController.borrowBook(reader,bookTitle,author); // testing the exception for reader
        });

        String expectedMessage = "A reader can not borrow more than 5 books";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        reader.setTotalBooks(2);
        String wrongBookTitle = "DGGHFdfd"; // title is not correct => exception
        Exception exception1 = assertThrows(InvalidActionException.class, () ->{
            librarianController.borrowBook(reader,wrongBookTitle,author); // testing the exception for wrong title
        });

        String expectedMessage1 = "The book is not available now";
        String actualMessage1 = exception1.getMessage();

        assertTrue(actualMessage1.contains(expectedMessage1));

        book.setNrOfActualExemplars(0); // it is not possible to borrow the book if nr of exemplars=0
        Exception exception2 = assertThrows(InvalidActionException.class, () ->{
            librarianController.borrowBook(reader,bookTitle,author); // testing the exception for number of actual books = 0
        });

        String expectedMessage2 = "The book is not available now";
        String actualMessage2 = exception2.getMessage();

        assertTrue(actualMessage2.contains(expectedMessage2));
    }

    @Test
    @DisplayName("Test for allBorrowedBooks")
    void testAllBorrowedBooks(){
        librarianController.populate();

        Reader reader = readerRepository.findbyID(1);
        Book book = bookRepository.findByTitle("Povestea lui Harap-Alb");
        List<Book> bookList = new ArrayList<>();
        bookList.add(book);
        reader.setBorrowedBooks(bookList);

        Boolean[] printedBook= new Boolean[50];
        for(Reader r: readerRepository.findAll()){
            if(r.getBorrowedBooks().size()>0) {
                for(Book b : r.getBorrowedBooks()){
                    printedBook[b.getBookId()] = true;
                }
            }
        }
        assertTrue(printedBook[3]);
        assertTrue(printedBook[1]);
    }

    @Test
    @DisplayName("Test for listOfBooksByAuthor")
    void testListOfBooksByAuthor() throws DatabaseObjectException {
        librarianController.populate();
        Author author = authorRepository.findAll().get(1);
        System.out.println(author);
        librarianController.listOfBooksByAuthor(author);

        List<Book> bookList = new ArrayList<>();
        author.setBooks(bookList);
        Exception exception = assertThrows(DatabaseObjectException.class, () -> librarianController.listOfBooksByAuthor(author));

        String expectedMessage = "We don't have any book written by this author";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
