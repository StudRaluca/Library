package library.repository.databaseRepo;

import library.model.Author;
import library.repository.interfaces.AuthorRepository;

import javax.persistence.*;
import java.util.List;

public class DatabaseAuthorRepository implements AuthorRepository { // ADD SI DELETE FUNCTIONEAZA
    private EntityManager entityManager;
    private EntityManagerFactory managerFactory;

    public DatabaseAuthorRepository() {
        this.managerFactory = Persistence.createEntityManagerFactory("default");
        this.entityManager = this.managerFactory.createEntityManager();
    }

    @Override
    public Author findByName(String name) {
        int index = name.lastIndexOf(' ');
        String firstName1 = name.substring(0, index);
        String lastName1 = name.substring(index + 1);
        Query query = entityManager.createNativeQuery("select a.authorId from Author a where a.firstName=:firstname and a.lastName=:lastname");
        query.setParameter("firstname", firstName1);
        query.setParameter("lastname", lastName1);
        List<Integer> list_of_ids = query.getResultList();
        if(list_of_ids.size() != 0) return findbyID(list_of_ids.get(0));
        else return null;
    }

    @Override
    public void add(Author author) {
        entityManager.getTransaction().begin();
        entityManager.persist(author);
        entityManager.getTransaction().commit();
    }

    @Override
    public void delete(Integer id) {
        Author author = entityManager.find(Author.class, id);
        entityManager.getTransaction().begin();
        entityManager.remove(author);
        entityManager.getTransaction().commit();
    }

    @Override
//    @Modifying
//    @Query(name = "updateAuthor",
//                query = "update Author a set a.firstName=:fistname, a.lastName=:lastname where a.authorId=:id")
    public void update(Integer id, Author author) {
        entityManager.getTransaction().begin();
        Author oldAuthor = entityManager.find(Author.class, id);
        System.out.println(oldAuthor);
        oldAuthor.setLastName(author.getLastName());
        oldAuthor.setFirstName(author.getFirstName());
        entityManager.getTransaction().commit();
    }

    @Override
    public List<Author> findAll() {
        Query query = entityManager.createNativeQuery("select * from Author", Author.class);
        return (List<Author>) query.getResultList();
    }

    @Override
    public Author findbyID(Integer id) {
        return entityManager.find(Author.class, id);
    }

    public void close(){
        this.entityManager.close();
        this.managerFactory.close();
    }
}