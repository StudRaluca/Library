package library.view;

import library.controller.LibrarianController;
import library.controller.ReaderController;
import library.exceptions.DatabaseObjectException;
import library.exceptions.InvalidActionException;
import library.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class View {

    final private Scanner in = new Scanner(System.in);
    private final LibrarianController librarianController;
    private final ReaderController readerController;

    public View(LibrarianController librarianController, ReaderController readerController) {
        this.librarianController = librarianController;
        this.readerController = readerController;
    }

    /**
     *  Functions used for menu
     */
    public void startMenuUI() throws InvalidActionException, DatabaseObjectException {

        System.out.println("\n Hello! Today is " + new Date());
        boolean finished = false;
        while (!finished) {
            System.out.println(" User: \n\t1 - Librarian\t2 - Reader \n\t0 - exit\n");
            int option = in.nextInt();
            switch (option) {
                case 1 -> {
                    System.out.println("Username: ");
                    String username = in.next();
                    Librarian librarian = LibrarianController.checkUsernameLibrarian(username);
                    System.out.println("Password: ");
                    String pass = in.next();
                    if(!librarian.getPassword().equals(pass)) throw new InvalidActionException("The password is not correct");
                    librarianMenuUI();
                }
                case 2 -> {
                    System.out.println("Username: ");
                    String username = in.next();
                    Reader reader = ReaderController.checkUsernameReader(username);
                    System.out.println("Password: ");
                    String pass = in.next();
                    if(!reader.getPassword().equals(pass)) throw new InvalidActionException("The password is not correct");
                    readerMenuUI();
                }
                case 0 -> finished = true;
            }
        }
    }

    public void librarianMenuUI() throws DatabaseObjectException, InvalidActionException {
        boolean finished = false;
        while(!finished) {
            System.out.println("""
                    Menu\s
                    \t1 - borrow book
                    \t2 - return book
                    \t3 - add book
                    \t4 - show all books
                    \t5 - show all readers ordered by lastname
                    \t6 - show all books borrowed by readers
                    \t7 - show the books which need to be returned today
                    \t8 - further options for books
                    \t9 - extra: see all the reader-librarian interactions
                    \t0 - exit
                    >>>\s""");
            int option = in.nextInt();
            switch (option) {
                case 1 -> borrowBookUI();
                case 2 -> returnBookUI();
                case 3 -> addBookUI();
                case 4 -> allBooksUI();
                case 5 -> allReadersUI();
                case 6 -> allBorrowedBooksUI();
                case 7 -> booksTodayUI();
                case 8 -> optionsUI();
                case 9 -> interactionsUI();
                case 0 -> finished = true;
            }
        }
    }

    public void readerMenuUI() throws DatabaseObjectException, InvalidActionException {
        boolean finished = false;
        while(!finished) {
            System.out.println("Menu \n\t1 - show all books\n\t2 - show all books/ filter books by different options\n\t3 - reserve a book\n\t0 - exit");
            int option = in.nextInt();
            switch (option) {
                case 1 -> allBooksUI();
                case 2 -> optionsUI();
                case 3 -> reserveBookUI();
                case 0 -> finished = true;
            }
        }
    }

    public void optionsUI() throws DatabaseObjectException {
        boolean finished = false;
        while(!finished) {
            System.out.println("""
                    This menu is made for displaying the books by choosing an option from the list:
                    \t1 - all books sorted by title
                    \t2 - all books sorted by year of publication
                    \t3 - filter books by year of publication
                    \t4 - all books written by a given author
                    \t0 - exit menu""");
            int option = in.nextInt();
            switch (option) {
                case 1 -> allBooksByTitleUI();
                case 2 -> allBooksByYearUI();
                case 3 -> filterBooksByYearUI();
                case 4 -> allBooksByAuthorUI();
                case 0 -> finished=true;
            }
        }
    }

    /**
     *  Functions used in the interactions with users
     */

    public void borrowBookUI() throws DatabaseObjectException, InvalidActionException {
        System.out.println("Please enter the id of the reader: ");
        String readerId = in.next();
        Reader reader = librarianController.checkReader(Integer.parseInt(readerId));

        boolean finished = false;
        while(!finished) {
            Author author = null;
            System.out.println("Please enter the name of the author: ");
            try {
                String authorName = in.next();
                authorName += in.nextLine();
                author = librarianController.checkAuthor(authorName);
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println("You have to write the first name and the last name of the author");
            }

            System.out.println("Please enter the title of the book that the reader wants to borrow or '0' for the menu with more options regarding the books");
            String book = in.nextLine();
            if(book.equals("0")) {
                optionsUI(); // more options
                System.out.println("Please enter the title of the wanted book");
                String book1 = in.next();
                librarianController.checkBook(book1);
                System.out.println(librarianController.borrowBook(reader, book1, author));
            }
            else {
                librarianController.checkBook(book); // it will check if the book is in the database
                System.out.println(librarianController.borrowBook(reader, book, author));
            }

            System.out.println("Is there any other book the reader wants to borrow? (y/n)");
            String ans = in.nextLine();
            if (ans.equals("n")) {
                finished = true;
            }
        }
    }

    public void returnBookUI() throws DatabaseObjectException, InvalidActionException {
        System.out.println("Please enter the id of the reader: ");
        String readerId = in.next();
        int readerIdInt = Integer.parseInt(readerId);
        Reader reader = librarianController.checkReader(readerIdInt);

        System.out.println("Please enter the title of the book that the reader wants to return: ");
        String title = in.next();
        title += in.nextLine();
        Book b00k = librarianController.checkBook(title);
        System.out.println(librarianController.returnBook(reader, b00k));
    }

    public void addBookUI() throws InvalidActionException {
        System.out.println("Title: ");
        String title = in.next();
        title += in.nextLine();
        System.out.println("Name of the author: ");
        String nameAuthor = in.next();
        nameAuthor += in.nextLine();
        System.out.println("Year of publication: ");
        int yearOfPublication = in.nextInt();
        System.out.println("Number of initial exemplars: ");
        String number = in.next();
        System.out.println(librarianController.addBook(title, nameAuthor, yearOfPublication, number));
    }

    public void allBooksUI() throws InvalidActionException {librarianController.allBooks();}
    public void allReadersUI() throws InvalidActionException {librarianController.allReaders();}

    public void allBorrowedBooksUI(){ librarianController.allBorrowedBooks();}

    public void booksTodayUI(){
        LocalDateTime now = LocalDateTime.of(2022,1, 18,12, 1);
        // LocalDateTime now = LocalDateTime.now(); it was supposed to be today
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String formatDateTime = now.format(format);
        librarianController.booksToday(formatDateTime);
    }

    public void allBooksByTitleUI(){
        System.out.println("In which order do you want the books to be sorted?\n\t1 - ascending order\n\t2 - descending order");
        List<Book> orderList = new ArrayList<>();
        int option = in.nextInt();
        switch (option) {
            case 1 -> orderList = librarianController.sortBooksByTitle(true);
            case 2 -> orderList = librarianController.sortBooksByTitle(false);
        }
        for(Book b : orderList){
            System.out.println(b.toString());
        }
    }

    public void allBooksByYearUI(){
        System.out.println("In which order do you want the books to be sorted?\n\t1 - ascending order\n\t2 - descending order");
        List<Book> orderList = new ArrayList<>();
        int option = in.nextInt();
        switch (option) {
            case 1 -> orderList = librarianController.sortBooksByYearOfPublication(true);
            case 2 -> orderList = librarianController.sortBooksByYearOfPublication(false);
        }
        for(Book b : orderList){
            System.out.println(b.toString() + ' '+ b.getYearOfPublication());
        }
    }

    public void filterBooksByYearUI(){
        System.out.println("Please enter the year of publication by which will be the filtration done: ");
        int year = in.nextInt();
        List<Book> orderList = librarianController.filterBooksByYearOfPublication(year);
        for(Book b : orderList){
            System.out.println(b.toString());
        }
    }

    public void allBooksByAuthorUI() throws DatabaseObjectException {
        System.out.println("Name of the author: ");
        String nameAuthor = in.next();
        nameAuthor += in.nextLine();
        Author author = librarianController.checkAuthor(nameAuthor);
        librarianController.listOfBooksByAuthor(author);
    }

    public void interactionsUI(){
        System.out.println("All the reader-librarian interactions: ");
        librarianController.interactions();
    }

    public void reserveBookUI() throws DatabaseObjectException, IllegalArgumentException {
        System.out.println("Please enter your readerId: ");
        int readerId = Integer.parseInt(in.next());
        Reader reader = librarianController.checkReader(readerId);

        boolean finished = false;
        while (!finished) {
            System.out.println("Title of the book: ");
            String title = in.next();
            title += in.nextLine();
            Book book = librarianController.checkBook(title);
            System.out.println(readerController.reserveBook(book, reader));

            System.out.println("Do you want to reserve another book? (y/n)");
            String ans = in.next();
            if (ans.equals("n")) {
                finished = true;
            } else if (!ans.equals("y"))
                throw new IllegalArgumentException("You can't give another answer, only 'y' or 'n'");
        }
    }
}
