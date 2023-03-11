package library.model;

import javax.persistence.*;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "readerId")
public class Reader extends User {
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "reservedBook_reader",
            joinColumns = @JoinColumn(name = "reader_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
    public List<Book> reservedBooks;

    @ManyToMany(mappedBy = "readersList")
    public List<Librarian> librarianList;

    @ManyToMany(mappedBy = "borrowedByReaders")
    public List<Book> borrowedBooks;

    @ElementCollection
    public List<String> dateForBooks;

    // the book in the borrowedBooks list is borrowed in the date found by
    // searching the same index in the dateForBooks list
    public int totalBooks;

    public Reader(String firstName, String lastName, String username, String password, List<Book> borrowedBooks, List<String> dateForBooks, int totalBooks, List<Book> reservedBooks) {
        super(firstName, lastName, password, username);
        this.borrowedBooks = borrowedBooks;
        this.dateForBooks = dateForBooks;
        this.totalBooks = totalBooks;
        this.reservedBooks = reservedBooks;
    }

    public Reader() {}

    public void addBook(Book book){
        this.getBorrowedBooks().add(book);
        this.setTotalBooks(this.getTotalBooks()+1);
        book.setNrOfActualExemplars(book.getNrOfActualExemplars()-1);
        book.getBorrowedByReaders().add(this);
    }

    public List<Book> getReservedBooks() {
        return reservedBooks;
    }

    public void setReservedBooks(List<Book> reservedBooks) {
        this.reservedBooks = reservedBooks;
    }

    public List<String> getDateForBooks() {
        return dateForBooks;
    }

    public void setDateForBooks(List<String> dateForBooks) {
        this.dateForBooks = dateForBooks;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(List<Book> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

    public int getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(int totalBooks) {
        this.totalBooks = totalBooks;
    }
}