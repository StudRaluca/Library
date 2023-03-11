package library.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Author extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int authorId;

    @OneToMany(cascade = CascadeType.ALL)
    List<Book> books;

    public Author(String firstName, String lastName, List<Book> books) {
        super(firstName, lastName);
        this.books = books;
    }

    public Author() {
        super();
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int id) {
        this.authorId = id;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

}