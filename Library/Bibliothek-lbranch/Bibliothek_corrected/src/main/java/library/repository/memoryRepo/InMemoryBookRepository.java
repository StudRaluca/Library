package library.repository.memoryRepo;

import library.model.Book;
import library.repository.interfaces.BookRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryBookRepository implements BookRepository {

    private List<Book> allBooks;

    public InMemoryBookRepository() {
        this.allBooks = new ArrayList<>();
    }

    @Override
    public List<Book> findAll() {
        return this.allBooks;
    }

    @Override
    public int count() {
        return this.allBooks.size();
    }

    @Override
    public List<Book> findByAuthor(String firstName, String lastName) {
        for(Book b:allBooks){
            if(b.author.firstName.equals(firstName) && b.author.lastName.equals(lastName)){
                return b.author.getBooks();
            }
        }
        return null;
    }

    @Override
    public void add(Book book) {
        for(Book b:allBooks){
            if(b.getBookId() == book.getBookId()){
            }
        }
        allBooks.add(book);
    }

    @Override
    public void delete(Integer id) {
        Book book = this.findbyID(id);
        this.allBooks.remove(book);
    }

    @Override
    public void update(Integer id, Book new_book) {
        Book book = this.findbyID(id);
        if(book != null){
            int position = allBooks.indexOf(book);
            new_book.setBookId(id);
            allBooks.set(position, new_book);
        }
    }

    @Override
    public Book findbyID(Integer id) {
        for(Book b:allBooks){
            if(b.getBookId() == id) {
                return b;
            }
        }
        return null;
    }

    @Override
    public Book findByTitle(String title) {
        for(Book b:allBooks){
            if(b.getTitle().equals(title)){
                return b;
            }
        }
        return null;
    }

}
