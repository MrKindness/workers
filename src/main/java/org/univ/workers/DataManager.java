package org.univ.workers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.UUID;

public class DataManager {
    private Statement statement;

    public DataManager(String connectionString, String password, String username) {
        try {
            Connection dbConnection = DriverManager.getConnection(connectionString, username, password);
            statement = dbConnection.createStatement();
        } catch (Exception ex) {
            System.out.print("Connection to db failed!");
        }
    }

    public boolean addWorker(String firstName, String lastName, Integer salary) {
        try {
            statement.executeUpdate(
                    String.format(
                            "INSERT INTO sworkers.workers(firstName, lastName, salary) VALUES ('%s', '%s', '%d');", firstName, lastName, salary
                    )
            );

            return true;
        } catch (SQLException e) {
            System.out.print(e.getMessage());
            return false;
        }
    }

    public boolean deleteWorker(UUID id) {
        try {
            statement.executeUpdate(
                    String.format("DELETE FROM sworkers.workers WHERE id = '%s'", id.toString())
            );
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public ResultSet getWorkers() {
        try {
            return statement.executeQuery("SELECT * FROM sworkers.workers");
        } catch (SQLException e) {
            System.out.print(e.getMessage());
            return null;
        }
    }

    public boolean updateWorker(Worker newWorker) {
        if (Objects.nonNull(newWorker.getId())) {
            try {
                statement.executeUpdate(
                        String.format(
                                "UPDATE sworkers.workers SET firstName ='%s', lastName = '%s', salary ='%s' WHERE id = '%s'",
                                newWorker.getFirstName(), newWorker.getLastName(), newWorker.getSalary(), newWorker.getId()
                        )
                );
                return true;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        return false;
    }
}
