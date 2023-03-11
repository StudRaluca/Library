package library.repository.memoryRepo;

import library.model.Reader;
import library.repository.interfaces.ReaderRepository;
import java.util.ArrayList;
import java.util.List;

public class InMemoryReaderRepository implements ReaderRepository {
    private List<Reader> allReaders;

    public InMemoryReaderRepository() {
        this.allReaders = new ArrayList<>();
    }

    @Override
    public void add(Reader reader) {
        for(Reader r: allReaders) {
            if(r.getUserId() == reader.getUserId()) {
            }
        }
        this.allReaders.add(reader);
    }

    @Override
    public void delete(Integer readerId) {
        Reader reader = this.findbyID(readerId);
        this.allReaders.remove(reader);
    }

    @Override
    public void update(Integer readerId, Reader newReader) {
        Reader reader = findbyID(readerId);
        if(reader!=null) {
            int position = allReaders.indexOf(reader);
            allReaders.set(position, newReader);
        }
    }

    @Override
    public Reader findbyID(Integer readerId) {
        for(Reader r: allReaders) {
            if(r.getUserId() == readerId) {
                return r;
            }
        }
        return null;
    }

    @Override
    public Reader findByUsername(String username) {
        for(Reader r:allReaders){
            if(r.getUsername().equals(username)){
                return r;
            }
        }
        return null;
    }

    @Override
    public List<Reader> findAll() {
        return this.allReaders;
    }
}

