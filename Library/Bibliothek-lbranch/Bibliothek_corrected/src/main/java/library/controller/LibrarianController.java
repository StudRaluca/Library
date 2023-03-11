package library.controller;

import library.exceptions.DatabaseObjectException;
import library.exceptions.InvalidActionException;
import library.model.*;
import library.repository.databaseRepo.DatabaseBookRepository;
import library.repository.databaseRepo.DatabaseReaderRepository;
import library.repository.interfaces.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LibrarianController {

    private static LibrarianRepository librarianRepository;
    private static ReaderRepository readerRepository;
    private static AuthorRepository authorRepository;
    private static BookRepository bookRepository;


    public LibrarianController(LibrarianRepository librarianRepository, ReaderRepository readerRepository, AuthorRepository authorRepository, BookRepository bookRepository) {
        LibrarianController.librarianRepository = librarianRepository;
        LibrarianController.readerRepository = readerRepository;
        LibrarianController.authorRepository = authorRepository;
        LibrarianController.bookRepository = bookRepository;
    }

    /**
     * checks if the username for the librarian is in the database
     * @param username username of the librarian
     * @return first librarian found with this username
     * @throws DatabaseObjectException if the librarian with this username is not found
     */
    public static Librarian checkUsernameLibrarian(String username) throws DatabaseObjectException {
        Librarian librarian = librarianRepository.findByUsername(username);
        if(librarian==null) throw new DatabaseObjectException();
        return librarian;
    }

    /**
     * checks if the reader is in the database
     * @param readerId id of the reader
     * @return reader with this readerId
     * @throws DatabaseObjectException if the reader with this readerId is not found
     */
    public Reader checkReader(int readerId) throws DatabaseObjectException{
        Reader reader = readerRepository.findbyID(readerId);
        if(reader==null) throw new DatabaseObjectException();
        return reader;
    }

    /**
     * checks if the author is in the database
     * @param name name of the author
     * @return author with this name
     * @throws DatabaseObjectException if the author with this name is not found
     */
    public Author checkAuthor(String name) throws DatabaseObjectException{
        Author author = authorRepository.findByName(name);
        if(author==null) throw new DatabaseObjectException();
        return author;
    }

    /**
     * checks if the book is in the database
     * @param title title of the book
     * @return book with this title
     * @throws DatabaseObjectException if the book with this title is not found
     */
    public Book checkBook(String title) throws DatabaseObjectException{
        Book book = bookRepository.findByTitle(title);
        if(book==null) throw new DatabaseObjectException();
        return book;
    }

    /**
     * print all the current books
     * @param author author of all the books to be printed
     * @throws DatabaseObjectException if there are no books written by the given author
     */
    public void listOfBooksByAuthor(Author author) throws DatabaseObjectException{
        List<Book> listOfBooks = bookRepository.findByAuthor(author.getFirstName(), author.getLastName());
        if(listOfBooks.size() == 0) throw new DatabaseObjectException("We don't have any book written by this author");
        for (Book b : listOfBooks) {
            System.out.println(b + " with actual number of exemplars: " + b.getNrOfActualExemplars()
                    + " , year of publication: " + b.getYearOfPublication() + "\nborrowed by in the past:  " + b.getBorrowedByReaders().toString() + "\nBookID: " + b.getBookId());
        }
    }

    /**
     * borrow book by reader, it will also save the current date in which the book was borrowed
     * @param reader reader who wants to borrow the book
     * @param book1 name of the book
     * @param author author of the book
     * @return status message
     * @throws InvalidActionException if the wanted book is not available/ is already borrowed or if the reader wants to
     * borrow more than 5 books
     */
    public String borrowBook(Reader reader, String book1, Author author) throws InvalidActionException{
        Book book = null;
        boolean found_book = false; // shows if the book is available (there are still a nr of exemplars)
        for (Book b : bookRepository.findAll()) {
            if (b.getTitle().equals(book1) && b.getAuthor().getFirstName().equals(author.getFirstName())
                    && b.getAuthor().getLastName().equals(author.getLastName()) && b.getNrOfActualExemplars()>0) {
                found_book = true;
                book = b;
                break;
            }
        }

        if (!found_book) throw new InvalidActionException("The book was not found");
        if (reader.getTotalBooks() >= 5) throw new InvalidActionException("A reader can not borrow more than 5 books");

        Reader reader1 = reader;
        Book book2 = book;
        reader.borrowedBooks.add(book2); // the book belongs now to the reader
        reader.totalBooks++;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String formatDateTime = now.format(format);
        reader.dateForBooks.add(formatDateTime); // the actual date and time

        book.nrOfActualExemplars--;
        book.borrowedByReaders.add(reader); // the book has another new reader on the list

        if(bookRepository.getClass() == DatabaseBookRepository.class) {
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            Query query2 = entityManager.createNativeQuery("update Reader set totalBooks=:carti where userId=:util");
            query2.setParameter("util", reader.getUserId());
            query2.setParameter("carti",reader.getTotalBooks());
            query2.executeUpdate();

            Query query3 = entityManager.createNativeQuery("insert into Reader_dateForBooks values (?, ?)");
            query3.setParameter(1, reader.getUserId());
            query3.setParameter(2, formatDateTime);
            query3.executeUpdate();

            Query query = entityManager.createNativeQuery("insert into borrowedBook_reader values (?, ?)");
            query.setParameter(1, book);
            query.setParameter(2, reader.getUserId());
            query.executeUpdate();

            Query query1 = entityManager.createNativeQuery("update Book set nrOfActualExemplars=:numar where bookId=:b");
            query1.setParameter("b", book.getBookId());
            query1.setParameter("numar", book.getNrOfActualExemplars());
            query1.executeUpdate();

            entityManager.getTransaction().commit();
        }

//        readerRepository.update(reader.getUserId(), reader1);
//        bookRepository.update(book.getBookId(), book2);

        return "The book " + book.getTitle() + " is now borrowed by the reader " + reader.getLastName() + " " + reader.getFirstName() +
                "\nThe reader must return the book in 14 days from " + reader.dateForBooks.get(reader.dateForBooks.size()-1);
    }

    /**
     * return a book by reader
     * @param reader reader who wants to return the book
     * @param book book to be returned
     * @return status message
     * @throws InvalidActionException if the reader didn't borrow the given book
     */
    public String returnBook(Reader reader, Book book) throws InvalidActionException {
        int index = 0;
        Reader reader1 = reader;
        for(Book b: reader1.getBorrowedBooks()) {
            if(b.getBookId() == book.getBookId()) {
                reader.dateForBooks.remove(index);
                reader.borrowedBooks.remove(b);
                reader1.totalBooks--;
//                readerRepository.update(reader.getUserId(), reader1);
                if(readerRepository.getClass() == DatabaseReaderRepository.class){
                    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
                    EntityManager entityManager = entityManagerFactory.createEntityManager();
                    entityManager.getTransaction().begin();

                    Query query2 = entityManager.createNativeQuery("update Reader set totalBooks=:carti where userId=:util");
                    query2.setParameter("util", reader.getUserId());
                    query2.setParameter("carti",reader.getTotalBooks());
                    query2.executeUpdate();

                    Query query1 = entityManager.createNativeQuery("delete from borrowedBook_reader where book_id=:book_id and reader_id=:reader_id");
                    query1.setParameter("book_id", book.getBookId());
                    query1.setParameter("reader_id", reader.getUserId());
                    query1.executeUpdate();

                    Query query = entityManager.createNativeQuery("update Book set nrOfActualExemplars=:numar where bookId=:b");
                    query.setParameter("b", book.getBookId());
                    query.setParameter("numar", book.getNrOfActualExemplars()+1);
                    query.executeUpdate();


                    entityManager.getTransaction().commit();
                }
                return "The book is successfully returned";
            }
            index++;
        }
        throw new InvalidActionException("The reader didn't borrow this book");
    }

    /**
     * add author in database (done only by librarian)
     * @param firstName firstname of the author to be added
     * @param lastName lastname of the author to be added
     * @return the added author
     */
    public Author addAuthor(String firstName, String lastName){
        Author author;
        List<Book> emptyList = new ArrayList<>();
        author = new Author(firstName, lastName, emptyList);
        authorRepository.add(author);
        return author;
    }

    /**
     * add book in database (done only by librarian)
     * it will auto-call addAuthor method if the given author is not in the database
     * @param title title of book
     * @param nameAuthor first and last name of author
     * @param yearOfPublication attribute of book: year of publication for the book
     * @param number attribute of book: number of initial exemplars
     * @return status message
     * @throws InvalidActionException if the book is already in the database
     */
    public String addBook(String title, String nameAuthor, int yearOfPublication, String number) throws InvalidActionException {
        Book book = bookRepository.findByTitle(title);
        if(book != null) throw  new InvalidActionException("The book is already in the database");

        List<Reader> emptyList = new ArrayList<>();
        Author author = authorRepository.findByName(nameAuthor);
        if(author == null){
            int index = nameAuthor.lastIndexOf(' ');
            String authorFirstName = nameAuthor.substring(0, index);
            String authorLastName = nameAuthor.substring(index + 1);
            author = addAuthor(authorFirstName, authorLastName); // adds a new author
        }

        int nrOfInitialExemplars = Integer.parseInt(number);
        // nrOfActualExemplars = nrOfInitialExemplars because no one has borrowed this book
        book = new Book(title, author, yearOfPublication, emptyList, nrOfInitialExemplars, nrOfInitialExemplars);
        List<Book> listBooksAuthor = author.getBooks();
        listBooksAuthor.add(book);
        author.setBooks(listBooksAuthor); // add the new book to the corresponding author
        bookRepository.add(book);
        return "Book successfully added";
    }

    /**
     * prints all books found in the database
     */
    public void allBooks() throws InvalidActionException {
        int number = bookRepository.findAll().size();
        if(number==0) throw new InvalidActionException("There are currently no books in the repository");
        int count = 1;
        System.out.println("You have a total of " + number + " books");
        for(Book b:bookRepository.findAll()){
            System.out.println(count++ + ". " + b.getTitle() + " , " + b.getAuthor().toString() + " , " + b.getNrOfInitialExemplars()
                    + " , " + b.getNrOfActualExemplars() + " , " + b.getYearOfPublication() + "\n   borrowed by:  " + b.getBorrowedByReaders().toString() + "\n   with BookID: " + b.getBookId());
        }
    }

    /**
     * prints all readers found in the database
     */
    public void allReaders() throws InvalidActionException {
        int number = readerRepository.findAll().size();
        if(number==0) throw new InvalidActionException("There are currently no readers in the repository");
        int count = 1;
        System.out.println("You have a total of " + number + " readers");
        ReaderController readerController = new ReaderController(readerRepository, bookRepository);
        List<Reader> orderedReaders = readerController.sortReadersByLastName(true);
        for(Reader r:orderedReaders){
            System.out.println(count++ + ". " + r.getFirstName() + " " + r.getLastName() + " , " + r.getBorrowedBooks().toString()
                    + " , " + r.totalBooks + "\n   with ReaderID: " + r.getUserId() + r.getDateForBooks());
        }
    }

    /**
     * it prints all borrowed books without repetition
     */
    public void allBorrowedBooks(){
        List<Boolean> prBk = new ArrayList<>();
        for(int i=0;i<=50;i++){
            prBk.add(false);
        }
        boolean finished = false;
        for(Reader r: readerRepository.findAll()){
            if(r.getBorrowedBooks().size()>0) {
                for(Book b : r.getBorrowedBooks()){
                    prBk.set(b.getBookId(), true);
                    finished = true;
                }
            }
        }
        for(int i=0;i<prBk.size();i++){
            if(prBk.get(i)){
                Book book = bookRepository.findbyID(i);
                System.out.println(book.toString());
            }
        }
        if(!finished) System.out.println("There are no borrowed books");
    }

    /**
     * it calculates all the return dates for the borrowed books, and it compares with today date
     * @param formatDateTime today date
     */
    public void booksToday(String formatDateTime){
        boolean finished = false;
        for(Reader r:readerRepository.findAll()){
            int index = 0;
            for(String date : r.getDateForBooks()){
                index++;
                String[] s = date.split("-");
                int day = Integer.parseInt(s[0]);
                int month = Integer.parseInt(s[1]);
                day += 14;
                if(month == 2 && day > 28){
                    month++;
                } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                    if(day > 30) month++;
                } else {
                    if(day > 31) month++;
                }
                String[] s2 = formatDateTime.split("-");
                int today = Integer.parseInt(s2[0]);
                int todaymonth = Integer.parseInt(s2[1]);
                if(day == today && month == todaymonth){
                    System.out.println(r.getBorrowedBooks().get(index-1).toString() + ", date of borrow: " + r.getDateForBooks().get(index-1));
                    finished = true;
                }
            }
        }
        if(!finished) System.out.println("There are no books to be returned today");
    }

    /**
     * it prints all readers and the librarians which interacted with them
     */
    public void interactions(){
        for(Reader r: readerRepository.findAll()){
            if(r.librarianList.size() > 0) {
                System.out.println(r + " has interacted with");
                for (Librarian l : r.librarianList) {
                    System.out.println(l.firstName + ' ' + l.lastName + " login date: " + l.getLoginDate() + ", logout date: "
                            + l.getLogoutDate());
                }
            }
        }
    }

    /**
     * This method sorts the books from the book repository by title in ascending or
     * descending order.
     * @param ascending This boolean parameter determines if the books will be sorted by title
     *                  in ascending or descending order. If true, the sorting will be in ascending
     *                  order, otherwise it will be in descending order.
     * @return A list with the sorted books.
     */

    public List<Book> sortBooksByTitle(boolean ascending) {
        List<Book> sortedBooksByTitle = new ArrayList<>(List.copyOf(bookRepository.findAll()));
        sortedBooksByTitle.sort((Book b1, Book b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle()));
        if(!ascending) {
            Collections.reverse(sortedBooksByTitle);
        }
        return sortedBooksByTitle;
    }

    /**
     * This method sorts the books from the book repository by year of publication in ascending or
     * descending order.
     * @param ascending This boolean parameter determines if the books will be sorted by year of publication
     *                  in ascending or descending order. If true, the sorting will be in ascending
     *                  order, otherwise it will be in descending order.
     * @return A list with the sorted books.
     */

    public List<Book> sortBooksByYearOfPublication(boolean ascending) {
        List<Book> sortedBooksByYearOfPublication = new ArrayList<>(List.copyOf(bookRepository.findAll()));
        sortedBooksByYearOfPublication.sort((Book b1, Book b2) -> (int) (b1.getYearOfPublication() - b2.getYearOfPublication()));
        if(!ascending) {
            Collections.reverse(sortedBooksByYearOfPublication);
        }
        return sortedBooksByYearOfPublication;
    }

    /**
     * This method filters the books from the book repository based on the year of publication.
     * @param yearOfPublication This is the year of publication by which the filtering is done.
     * @return A list of the books of a given year of publication.
     */

    public List<Book> filterBooksByYearOfPublication(int yearOfPublication) {
        return new ArrayList<>(List.copyOf(bookRepository.findAll())).
                stream().filter((Book b) -> b.getYearOfPublication() == (yearOfPublication)).toList();
    }

    /**
     * This method filters the librarians from the librarian repository based on the logout date.
     * @param logoutdate This is the logout date by which the filtering is done.
     * @return A list of the librarians of a given logout date.
     */

    public List<Librarian> filterLibrariansByLogoutdate(String logoutdate) {
        return new ArrayList<>(List.copyOf(librarianRepository.findAll())).
                stream().filter((Librarian l) -> l.getLogoutDate().equals(logoutdate)).toList();
    }

    /**
     * add data to the repository lists & database
     */
    public void populate() {
        List<Reader> emp = new ArrayList<>();
        Librarian librarian1 = new Librarian("Ana", "Pop", "parola", "AnaPop", "20-11-2022 10:03", "20-11-2022 18:00", 400, emp);
        Librarian librarian2 = new Librarian("Raul", "Ilona", "rio123", "raulilona", "10-11-2022 11:47", "10-11-2022 20:20", 370, emp);
        Librarian librarian3 = new Librarian("Denisa", "Monea", "dmn56", "moneadenisa", "13-10-2022 12:34", "13-10-2022 14:00", 290,emp);
        librarian1.setUserId(1);
        librarian2.setUserId(2);
        librarian3.setUserId(3);

        List<Book> emptylist = new ArrayList<>();
        List<String> date = new ArrayList<>();
        Reader reader1 = new Reader("Maria", "Straton","amama", "pass", emptylist, date, 0, emptylist);
        Reader reader2 = new Reader("Radu", "Ionescu", "rionescu", "raduu7364", emptylist, date, 3, emptylist);
        Reader reader3 = new Reader("Anda", "Popescu", "andapop", "andl34", emptylist, date, 1, emptylist);
        reader1.setUserId(1);
        reader2.setUserId(2);
        reader3.setUserId(3);

        Author author1 = new Author("Ion","Creanga", emptylist);
        Author author2 = new Author("Camil", "Petrescu", emptylist);
        author1.setAuthorId(1);
        author2.setAuthorId(2);

        List<Reader> readersFirstBook = new ArrayList<>();
        readersFirstBook.add(reader1);

        List<Reader> readersSecondBook = new ArrayList<>();

        List<Reader> readersThirdBook = new ArrayList<>();
        readersThirdBook.add(reader1);
        readersThirdBook.add(reader2);

        Book book1 = new Book("Povestea lui Harap-Alb", author1, 1877, readersFirstBook, 7, 1);
        Book book2 = new Book("Patul lui Procust", author2, 1933, readersSecondBook, 4, 3);
        Book book3 = new Book("Ultima noapte de dragoste, intaia noapte de razboi",author2, 1930, readersThirdBook, 10,2);
        book1.setBookId(1);
        book2.setBookId(2);
        book3.setBookId(3);


        List<Book> b00ks = new ArrayList<>();
        b00ks.add(book3);
        reader2.setBorrowedBooks(b00ks);

        List<String> date1 = new ArrayList<>();
        date1.add("01-12-2022 12:06");
        reader2.setDateForBooks(date1);

        List<Book> BooksFirstAuthor = new ArrayList<>();
        BooksFirstAuthor.add(book1);
        author1.setBooks(BooksFirstAuthor);

        List<Book> BooksSecondAuthor = new ArrayList<>();
        BooksSecondAuthor.add(book2);
        BooksSecondAuthor.add(book3);
        author2.setBooks(BooksSecondAuthor);

        List<Reader> FirstLibInteractions = new ArrayList<>();
        FirstLibInteractions.add(reader3);
        librarian1.setReadersList(FirstLibInteractions);

        readerRepository.add(reader1);
        readerRepository.add(reader2);
        readerRepository.add(reader3);

        authorRepository.add(author1);
        authorRepository.add(author2);

        librarianRepository.add(librarian1);
        librarianRepository.add(librarian2);
        librarianRepository.add(librarian3);

        bookRepository.add(book1);
        bookRepository.add(book2);
        bookRepository.add(book3);

    }
}
