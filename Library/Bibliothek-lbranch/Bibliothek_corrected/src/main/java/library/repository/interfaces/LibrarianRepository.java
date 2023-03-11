package library.repository.interfaces;

import library.model.Librarian;

import java.util.List;

public interface LibrarianRepository extends ICrudRepository<Integer, Librarian>{

    List<Librarian> findAll(); // returns all the workers

    Librarian findByUsername(String username);
}
