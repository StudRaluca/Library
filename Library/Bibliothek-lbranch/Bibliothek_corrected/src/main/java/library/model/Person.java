package library.model;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Person {
    public String firstName;
    public String lastName;

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public Person() {}

    @Override
    public String toString() {
        return firstName + " " + lastName;
    } // for printing the name of the persons in the output

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
