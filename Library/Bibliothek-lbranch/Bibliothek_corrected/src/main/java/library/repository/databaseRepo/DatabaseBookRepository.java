package library.repository.databaseRepo;

import library.model.Book;
import library.repository.interfaces.BookRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class DatabaseBookRepository implements BookRepository {

    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

    public DatabaseBookRepository() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("default");
        this.entityManager = this.entityManagerFactory.createEntityManager();
    }

    @Override
    public int count() {
        Query query = entityManager.createNativeQuery("select count(*) from Book");
        return query.getFirstResult();
    }

    @Override
    public List<Book> findByAuthor(String firstName, String lastName) {
        Query query = entityManager.createNativeQuery("select a.authorId from Author a where a.firstName=:firstname and a.lastName=:lastname");
        query.setParameter("firstname", firstName);
        query.setParameter("lastname", lastName);
        List<Integer> list_of_ids = query.getResultList();
        int authorId = list_of_ids.get(0);
        Query q1 = entityManager.createNativeQuery("select Ab.books_bookId from Author_Book Ab where Ab.Author_authorId=:Author_authorId");
        q1.setParameter("Author_authorId", authorId);
        List<Integer> listOfIds = q1.getResultList();
        List<Book> books = new ArrayList<>();
        for (Integer i : listOfIds) {
            books.add(findbyID(i));
        }
        return books;
    }

    @Override
    public Book findByTitle(String title) {
        Query query = entityManager.createNativeQuery("select b.bookId from Book b where b.title=:title");
        query.setParameter("title", title);
        List<Integer> listOfIds = query.getResultList();
        if(listOfIds.size() != 0) return findbyID(listOfIds.get(0));
        else return null;
    }

    @Override
    public void add(Book book) {
        entityManager.getTransaction().begin();
        entityManager.persist(book);
        entityManager.getTransaction().commit();
    }

    @Override
    public void delete(Integer id) {
        Book book = entityManager.find(Book.class, id);
        entityManager.getTransaction().begin();
        entityManager.remove(book);
        entityManager.getTransaction().commit();
    }

    @Override
    public void update(Integer id, Book book) {
        Book oldBook = entityManager.find(Book.class, id);
        oldBook.setTitle(book.getTitle());
        oldBook.setAuthor(book.getAuthor());
        oldBook.setYearOfPublication(book.getYearOfPublication());
        oldBook.setNrOfInitialExemplars(book.getNrOfInitialExemplars());
        oldBook.setNrOfActualExemplars(book.getNrOfActualExemplars());
    }

    @Override
    public List<Book> findAll() {
        Query query = entityManager.createNativeQuery("select * from Book", Book.class);
        return (List<Book>) query.getResultList();
    }

    @Override
    public Book findbyID(Integer id) {
//        entityManager.getTransaction().begin();
        return entityManager.find(Book.class,id);
    }

    public void close(){
        this.entityManager.close();
        this.entityManagerFactory.close();
    }
}
