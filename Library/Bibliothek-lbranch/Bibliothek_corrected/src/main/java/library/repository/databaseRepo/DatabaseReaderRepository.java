package library.repository.databaseRepo;

import library.model.Book;
import library.model.Reader;
import library.repository.interfaces.ReaderRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

public class DatabaseReaderRepository implements ReaderRepository {

    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

    public DatabaseReaderRepository() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("default");
        this.entityManager = this.entityManagerFactory.createEntityManager();
    }

    @Override
    public void add(Reader reader) {
        entityManager.getTransaction().begin();
        entityManager.persist(reader);
        entityManager.getTransaction().commit();
    }

    @Override
    public void delete(Integer id) {
        Reader reader = entityManager.find(Reader.class, id);
        entityManager.getTransaction().begin();
        entityManager.remove(reader);
        entityManager.getTransaction().commit();
    }

    @Override
    public void update(Integer id, Reader reader) {
        entityManager.getTransaction().begin();
        Reader oldReader = entityManager.find(Reader.class, id);
        oldReader.setFirstName(reader.getFirstName());
        oldReader.setLastName(reader.getLastName());
        oldReader.setUsername(reader.getUsername());
        oldReader.setPassword(reader.getPassword());
        oldReader.setTotalBooks(reader.getTotalBooks());
        oldReader.setDateForBooks(reader.getDateForBooks());
        Book book = reader.getBorrowedBooks().get(reader.getBorrowedBooks().size() - 1);

        entityManager.getTransaction().commit();
    }

    @Override
    public Reader findbyID(Integer id) {
        return entityManager.find(Reader.class, id);

    }

    @Override
    public List<Reader> findAll() {
        Query query = entityManager.createNativeQuery("select * from Reader", Reader.class);
        return (List<Reader>) query.getResultList();
    }

    @Override
    public Reader findByUsername(String username) {
        Query query = entityManager.createNativeQuery("select r.userId from Reader r where r.username=:username");
        query.setParameter("username", username);
        List<Integer> list_of_ids = query.getResultList();
        if(list_of_ids.size() != 0) return findbyID(list_of_ids.get(0));
        else return null;
    }

    public void close() {
        this.entityManager.close();
        this.entityManagerFactory.close();
    }
}
