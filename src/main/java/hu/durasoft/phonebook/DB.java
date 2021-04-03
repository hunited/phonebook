package hu.durasoft.phonebook;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DB {

    private static final String URL = "jdbc:derby:PHONEBOOK;create=true";
    private static final String USERNAME = "phonebook";
    private static final String PASSWORD = "phonebook";
    private static final String QUERY = "select * from contacts";

    private Connection connection = null;
    private Statement statement = null;

    public DB() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, "PHONEBOOK", "CONTACTS", null);
            if (!resultSet.next()) {
                statement.execute("create table contacts(id int not null primary key generated always as identity(start with 1, increment by 1), lastname varchar(50), firstname varchar(50), email varchar(50))");
            }
        } catch (SQLException se) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, "Something went wrong", se);
        }
    }

    public void addContact(Person person) {
        String statementQuery = "insert into contacts(lastname, firstname, email) values (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statementQuery)) {
            preparedStatement.setString(1, person.getLastName());
            preparedStatement.setString(2, person.getFirstName());
            preparedStatement.setString(3, person.getEmail());
            preparedStatement.execute();
        } catch (SQLException se) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, "Contact can not added", se);
        }
    }

    public void updateContact(Person person) {
        String statementQuery = "update contacts set lastname = ?, firstname = ?, email = ? where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statementQuery)) {
            preparedStatement.setString(1, person.getLastName());
            preparedStatement.setString(2, person.getFirstName());
            preparedStatement.setString(3, person.getEmail());
            preparedStatement.setInt(4, person.getPersonId());
            preparedStatement.execute();
        } catch (SQLException se) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, "Contact can not updated", se);
        }
    }

    public void removeContact(Person person) {
        String statementQuery = "delete from contacts where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statementQuery)) {
            preparedStatement.setInt(1, person.getPersonId());
            preparedStatement.execute();
        } catch (SQLException se) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, "Contact can not delete", se);
        }
    }

    public List<Person> getAllContacts() {
        List<Person> result = new ArrayList<>();
        try (ResultSet resultSet = statement.executeQuery(QUERY)) {
            while (resultSet.next()) {
                result.add(new Person(
                        resultSet.getInt("id"),
                        resultSet.getString("lastname"),
                        resultSet.getString("firstname"),
                        resultSet.getString("email")
                ));
            }
        } catch (SQLException se) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, "Person can not selected", se);
        }
        return result;
    }

}
