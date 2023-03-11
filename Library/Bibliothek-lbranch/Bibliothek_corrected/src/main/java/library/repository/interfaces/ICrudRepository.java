package library.repository.interfaces;

import java.util.List;

public interface ICrudRepository<ID, E> {
    void add(E e);  // new item
    void delete(ID id); // delete by ID
    void update(ID id, E e); // item with a new ID

    List<E> findAll(); // returns all entities

    E findbyID(ID id);
}
