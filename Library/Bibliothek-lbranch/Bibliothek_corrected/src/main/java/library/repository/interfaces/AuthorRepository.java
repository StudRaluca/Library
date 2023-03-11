package library.repository.interfaces;

import library.model.Author;

public interface AuthorRepository extends ICrudRepository<Integer, Author>{
    Author findByName(String name);
}
