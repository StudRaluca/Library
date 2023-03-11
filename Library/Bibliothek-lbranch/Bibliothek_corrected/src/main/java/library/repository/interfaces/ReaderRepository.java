package library.repository.interfaces;

import library.model.Reader;
import java.util.List;

public interface ReaderRepository extends ICrudRepository<Integer, Reader> {
    List<Reader> findAll(); // the customers who have library card

    Reader findByUsername(String username);
}
