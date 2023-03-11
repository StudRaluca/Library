package library.model;


import javax.persistence.*;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "librarianId")
public class Librarian extends User {

    public int salary;
    public String loginDate;
    public String  logoutDate;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "InteractionLibrarian_reader",
            joinColumns = @JoinColumn(name = "librarian_id"),
            inverseJoinColumns = @JoinColumn(name = "reader_id"))
    public List<Reader> readersList;


    public Librarian(String firstName, String lastName, String password, String username, String loginDate, String logoutDate,int salary, List<Reader> readersList) {
        super(firstName,lastName, password, username);
        this.loginDate = loginDate;
        this.logoutDate = logoutDate;
        this.readersList = readersList;
        this.salary = salary;
    }

    public Librarian() {}

    @Override
    public String toString() {
        return "Librarian{" +
                "salary=" + salary +
                ", loginDate='" + loginDate + '\'' +
                ", logoutDate='" + logoutDate + '\'' +
                '}';
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    public String getLogoutDate() {
        return logoutDate;
    }

    public void setLogoutDate(String logoutDate) {
        this.logoutDate = logoutDate;
    }

    public List<Reader> getReadersList() {
        return readersList;
    }

    public void setReadersList(List<Reader> readersList) {
        this.readersList = readersList;
    }
}
