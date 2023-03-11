import library.controller.ReaderController;
import library.exceptions.DatabaseObjectException;
import library.exceptions.InvalidActionException;
import library.repository.databaseRepo.*;
import library.repository.interfaces.*;
import library.repository.memoryRepo.*;
import library.view.View;
import library.controller.LibrarianController;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println("Please choose from the following options:\n\t1 - Database functions\n\t2 - In-Memory functions");
        Scanner in = new Scanner(System.in);
        int option = in.nextInt();
        switch (option) {
            case 1 -> DatabaseFunctions();
            case 2 -> InMemoryFunctions();
        }
    }

    private static void InMemoryFunctions() throws InvalidActionException, DatabaseObjectException {
        AuthorRepository authorRepository = new InMemoryAuthorRepository();
        BookRepository bookRepository = new InMemoryBookRepository();
        LibrarianRepository librarianRepository = new InMemoryLibrarianRepository();
        ReaderRepository readerRepository = new InMemoryReaderRepository();

        LibrarianController librarianController = new LibrarianController(librarianRepository, readerRepository, authorRepository, bookRepository);
        ReaderController readerController = new ReaderController(readerRepository, bookRepository);
        View view = new View(librarianController, readerController);

        librarianController.populate();
        view.startMenuUI();
    }

    private static void DatabaseFunctions() throws InvalidActionException, DatabaseObjectException {
        AuthorRepository authorRepository = new DatabaseAuthorRepository();
        BookRepository bookRepository = new DatabaseBookRepository();
        LibrarianRepository librarianRepository = new DatabaseLibrarianRepository();
        ReaderRepository readerRepository = new DatabaseReaderRepository();

        LibrarianController librarianController = new LibrarianController(librarianRepository, readerRepository, authorRepository, bookRepository);
        ReaderController readerController = new ReaderController(readerRepository, bookRepository);
        View view = new View(librarianController, readerController);

        view.startMenuUI();
    }
}