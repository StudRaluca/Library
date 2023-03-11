package library.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //SEQUENCE
    public int bookId;
    public String title;
    @ManyToOne
    public Author author;
    public int yearOfPublication;

    public int nrOfInitialExemplars;
    public int nrOfActualExemplars;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "borrowedBook_reader",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "reader_id"))
    public List<Reader> borrowedByReaders;

    public Book(String title, Author author, int yearOfPublication, List<Reader> borrowedByReaders, int nrOfInitialExemplars, int nrOfActualExemplars) {
        this.title = title;
        this.author = author;
        this.yearOfPublication = yearOfPublication;
        this.borrowedByReaders = borrowedByReaders;
        this.nrOfInitialExemplars = nrOfInitialExemplars;
        this.nrOfActualExemplars = nrOfActualExemplars;
    }

    public Book() {}

    @Override
    public String toString() {
        return title + " by " + author;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getNrOfInitialExemplars() {
        return nrOfInitialExemplars;
    }

    public void setNrOfInitialExemplars(int nrOfInitialExemplars) {
        this.nrOfInitialExemplars = nrOfInitialExemplars;
    }

    public int getNrOfActualExemplars() {
        return nrOfActualExemplars;
    }

    public void setNrOfActualExemplars(int nrOfActualExemplars) {
        this.nrOfActualExemplars = nrOfActualExemplars;
    }

    public int getBookId() {
        return bookId;
    }

    public void setId(int id) {
        this.bookId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public int getYearOfPublication() {
        return yearOfPublication;
    }

    public void setYearOfPublication(int yearOfPublication) {
        this.yearOfPublication = yearOfPublication;
    }

    public List<Reader> getBorrowedByReaders() {
        return borrowedByReaders;
    }

    public void setBorrowedByReaders(List<Reader> borrowedByReaders) {
        this.borrowedByReaders = borrowedByReaders;
    }

}