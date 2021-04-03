package hu.durasoft.phonebook;

public class Person {

    private int personId;
    private String lastName;
    private String firstName;
    private String email;

    public Person(String lastName, String firstName, String email) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
    }

    public Person(int personId, String lastName, String firstName, String email) {
        this(lastName, firstName, email);
        this.personId = personId;
    }

    public int getPersonId() {
        return personId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
