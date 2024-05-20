package org.univ.workers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Controller {
    @FXML
    private TextField firstNameInput;
    @FXML
    private TextField lastNameInput;
    @FXML
    private TextField salaryInput;

    @FXML
    private TableView<Worker> tableView;
    @FXML
    private TableColumn<Worker, UUID> idColumn;
    @FXML
    private TableColumn<Worker, String> firstNameColumn;
    @FXML
    private TableColumn<Worker, String> lastNameColumn;
    @FXML
    private TableColumn<Worker, Integer> salaryColumn;

    DataManager dataManager;
    ObservableList<Worker> workers;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameColumn.setOnEditCommit(event -> {
            boolean result = false;
            Worker newWorker = event.getRowValue();
            String oldValue = event.getOldValue();

            if (isValidFirstLastName(event.getNewValue())) {
                newWorker.setFirstName(event.getNewValue());
                result = dataManager.updateWorker(newWorker);
            }

            if (!result) {
                new Alert(Alert.AlertType.ERROR, "Update error!").showAndWait();
                newWorker.setFirstName(oldValue);
            }

            tableView.refresh();
        });

        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameColumn.setOnEditCommit(event -> {
            boolean result = false;
            Worker newWorker = event.getRowValue();
            String oldValue = event.getOldValue();

            if (isValidFirstLastName(event.getNewValue())) {
                newWorker.setLastName(event.getNewValue());
                result = dataManager.updateWorker(newWorker);
            }

            if (!result) {
                new Alert(Alert.AlertType.ERROR, "Update error!").showAndWait();
                newWorker.setLastName(oldValue);
            }

            tableView.refresh();
        });

        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        salaryColumn.setCellFactory(TextFieldTableCell.forTableColumn(new WorkersIntegerStringConverter()));
        salaryColumn.setOnEditCommit(event -> {
            boolean result = false;
            Worker newWorker = event.getRowValue();
            Integer oldValue = event.getOldValue();

            if (isValidSalaryValue(event.getNewValue().toString())) {
                newWorker.setSalary(event.getNewValue());
                result = dataManager.updateWorker(newWorker);
            }

            if (!result) {
                new Alert(Alert.AlertType.ERROR, "Update error!").showAndWait();
                newWorker.setSalary(oldValue);
            }

            tableView.refresh();
        });

        workers = FXCollections.observableArrayList();
        tableView.setItems(workers);

        dataManager = new DataManager("jdbc:postgresql://localhost:5432/workersdb", "Zenon567", "postgres");
        updateWorkersList();
    }

    @FXML
    protected void addWorkerEvent(ActionEvent event) {
        if (isWorkerDataValid()) {
            if (
                    dataManager.addWorker(firstNameInput.getText(), lastNameInput.getText(), Integer.parseInt(this.salaryInput.getText()))
            ) {
                updateWorkersList();
                new Alert(Alert.AlertType.INFORMATION, "Worker was added!").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Error!").showAndWait();
            }
        }
    }

    @FXML
    protected void deleteWorkerEvent(ActionEvent event) {
        int ind = tableView.getSelectionModel().getSelectedIndex();

        if (ind >= 0) {
            if (dataManager.deleteWorker(tableView.getSelectionModel().getSelectedItem().getId())) {
                tableView.getItems().remove(ind);
                tableView.refresh();

                new Alert(Alert.AlertType.INFORMATION, "Worker was deleted!").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Error!").showAndWait();
            }
        }
    }

    private boolean isWorkerDataValid() {
        if (!isValidSalaryValue(this.salaryInput.getText())) {
            new Alert(Alert.AlertType.ERROR, "Invalid Salary!").showAndWait();
            return false;
        }

        if (!isValidFirstLastName(this.firstNameColumn.getText())) {
            new Alert(Alert.AlertType.ERROR, "Invalid Firstname!").showAndWait();
            return false;
        }

        if (!isValidFirstLastName(this.lastNameInput.getText())) {
            new Alert(Alert.AlertType.ERROR, "Invalid Lastname!").showAndWait();
            return false;
        }
        return true;
    }

    private void updateWorkersList() {

        ResultSet resultSet = dataManager.getWorkers();
        workers.clear();
        try {
            while (resultSet.next()) {
                workers.add(
                        new Worker(
                                UUID.fromString(resultSet.getString("id")),
                                resultSet.getString("firstName"),
                                resultSet.getString("lastName"),
                                resultSet.getInt("salary")
                        )
                );
            }
            tableView.refresh();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean isValidFirstLastName(String value) {
        return value.matches("[A-Za-z0-9]+");
    }

    private boolean isValidSalaryValue(String value) {
        try {
            int number = Integer.parseInt(value);
            return number >= 0;
        } catch (Exception e) {
            return false;
        }
    }
}