package library.repository.memoryRepo;

import library.model.Author;
import library.repository.interfaces.AuthorRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthorRepository implements AuthorRepository {

    private List<Author> allAuthors;

    public InMemoryAuthorRepository() {
        this.allAuthors = new ArrayList<>();
    }

    @Override
    public void add(Author author) {
        for(Author a:allAuthors){
            if(a.getAuthorId() == author.getAuthorId()){
                return;
            }
        }
        allAuthors.add(author);
    }

    @Override
    public void delete(Integer id) {
        Author author = this.findbyID(id);
        this.allAuthors.remove(author);
    }

    @Override
    public void update(Integer id, Author new_author) {
        Author author = this.findbyID(id);
        if(author != null){
            int position = allAuthors.indexOf(author);
            new_author.setAuthorId(id);
            allAuthors.set(position, new_author);
        }
    }

    @Override
    public Author findbyID(Integer id) {
        for(Author a:allAuthors) {
            if (a.getAuthorId() == id) {
                return a;
            }
        }
        return null;
    }

    @Override
    public Author findByName(String name){
        int index = name.lastIndexOf(' ');
        String firstName = name.substring(0, index);
        String lastName = name.substring(index + 1);
        for (Author a : allAuthors) {
            if (a.getFirstName().equals(firstName) && a.getLastName().equals(lastName)) {
                return a;
            }
        }
        return null;
    }

    @Override
    public List<Author> findAll() {
        return this.allAuthors;
    }
}


