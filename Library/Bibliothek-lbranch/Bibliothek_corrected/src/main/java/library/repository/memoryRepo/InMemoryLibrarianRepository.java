package library.repository.memoryRepo;

import library.model.Librarian;
import library.repository.interfaces.LibrarianRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryLibrarianRepository implements LibrarianRepository {

    private List<Librarian> allLibrarians;

    public InMemoryLibrarianRepository() {
        this.allLibrarians = new ArrayList<>();
    }

    @Override
    public void add(Librarian librarian) {
        for(Librarian l: allLibrarians) {
            if(l.getUserId() == librarian.getUserId()) {
            }
        }
        this.allLibrarians.add(librarian);
    }

    @Override
    public void delete(Integer librarianId) {
        Librarian librarian = this.findbyID(librarianId);
        this.allLibrarians.remove(librarian);
    }

    @Override
    public void update(Integer librarianId, Librarian new_librarian) {
        Librarian librarian = findbyID(librarianId);
        if(librarian!=null) {
            int position = allLibrarians.indexOf(librarian);
            allLibrarians.set(position, new_librarian);
        }
    }

    @Override
    public Librarian findbyID(Integer librarianId) {
        for(Librarian l:allLibrarians) {
            if(l.getUserId() == librarianId) {
                return l;
            }
        }
        return null;
    }

    @Override
    public Librarian findByUsername(String username){
        for(Librarian l: allLibrarians){
            if(l.getUsername().equals(username)){
                return l;
            }
        }
        return null;
    }

    @Override
    public List<Librarian> findAll() {
        return this.allLibrarians;
    }
}


