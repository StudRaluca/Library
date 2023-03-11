package library.repository.databaseRepo;

import library.model.Librarian;
import library.repository.interfaces.LibrarianRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

public class DatabaseLibrarianRepository implements LibrarianRepository {
    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

    public DatabaseLibrarianRepository() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("default");
        this.entityManager = this.entityManagerFactory.createEntityManager();
    }

    @Override
    public void add(Librarian librarian) {
        entityManager.getTransaction().begin();
        entityManager.persist(librarian);
        entityManager.getTransaction().commit();
    }

    @Override
    public void delete(Integer id) {
        Librarian librarian = entityManager.find(Librarian.class, id);
        entityManager.getTransaction().begin();
        entityManager.remove(librarian);
        entityManager.getTransaction().commit();
    }

    @Override
    public void update(Integer id, Librarian librarian) {
        entityManager.getTransaction().begin();
        Librarian oldLibrarian = entityManager.find(Librarian.class, id);
        System.out.println(oldLibrarian);
        oldLibrarian.setFirstName(librarian.getFirstName());
        oldLibrarian.setLastName(librarian.getLastName());
        oldLibrarian.setPassword(librarian.getPassword());
        oldLibrarian.setUsername(librarian.getUsername());
        oldLibrarian.setLoginDate(librarian.getLoginDate());
        oldLibrarian.setLogoutDate(librarian.getLogoutDate());
        oldLibrarian.setSalary(librarian.getSalary());
        //  oldLibrarian.setReadersList(librarian.getReadersList());
        entityManager.getTransaction().commit();
    }

    @Override
    public Librarian findbyID(Integer id) {
        return entityManager.find(Librarian.class, id);
    }

    @Override
    public List<Librarian> findAll() {
        Query query = entityManager.createNativeQuery("select * from Librarian", Librarian.class);
        return (List<Librarian>) query.getResultList();
    }

    @Override
    public Librarian findByUsername(String username) {
        Query query = entityManager.createNativeQuery("select l.userId from Librarian l where l.username=:username");
        query.setParameter("username", username);
        List<Integer> list_of_ids = query.getResultList();
        if(list_of_ids.size() != 0) return findbyID(list_of_ids.get(0));
        else return null;
    }

    public void close(){
        this.entityManager.close();
        this.entityManagerFactory.close();
    }
}
