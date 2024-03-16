package org.example.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    public void main() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            if (!tableExists(connection, "Equations")) {
                createEquationsTable(connection);
            }


            if (!tableExists(connection, "Roots")) {
                createRootsTable(connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, tableName, null);
        return resultSet.next();
    }

    private static void createEquationsTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE Equations (id SERIAL PRIMARY KEY, equation TEXT NOT NULL)");
        }
    }

    private static void createRootsTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE Roots (id SERIAL PRIMARY KEY, equation_id INT, root double, FOREIGN KEY (equation_id) REFERENCES Equations(id))");
        }
    }
    public void save(String equation){
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // Prepare SQL statement
            String sql = "INSERT INTO Equations (equation) VALUES (?)";

            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, equation);


                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Equation saved successfully.");

                    // Retrieve generated ID of the inserted equation
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int equationId = generatedKeys.getInt(1);
                        System.out.println("Inserted Equation ID: " + equationId);
                    } else {
                        System.out.println("Failed to retrieve equation ID.");
                    }
                } else {
                    System.out.println("Failed to save equation.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public int getEquationIdFromDatabase(String equation) {
        int equationId = -1;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // Запит до бази даних для отримання equation_id за значенням рівняння
            String sql = "SELECT id FROM equations WHERE equation = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, equation);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        equationId = resultSet.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equationId;
    }
    public List<String> getDataFromDatabase() {
        List<String> data = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM equations";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String equation = resultSet.getString("equation");
                        data.add(equation);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }
    public void saveRootToDatabase(String equation, double root) {
        int equationId = getEquationIdFromDatabase(equation);
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            if (saveRoot(connection, equationId, root)) {
                System.out.println("Корінь успішно збережено в базі даних для рівняння з ідентифікатором " + equationId);
            } else {
                System.out.println("Не вдалося зберегти корінь в базу даних.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private boolean saveRoot(Connection connection, int equationId, double root) throws SQLException {
        String sql = "INSERT INTO roots (equation_id, root) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, equationId);
            preparedStatement.setDouble(2, root);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }
    public List<String> getEquationsWithRoot(String root) {
        List<String> equations = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT equation FROM Equations WHERE id IN " +
                    "(SELECT equation_id FROM Roots WHERE root = ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDouble(1, Double.parseDouble(root));
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String equation = resultSet.getString("equation");
                        equations.add(equation);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return equations;
    }
    public List<String> getUniqueEquations() {
        List<String> equations = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT equation FROM Equations WHERE id IN " +
                    "(SELECT equation_id FROM Roots GROUP BY equation_id HAVING COUNT(*) = 1)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String equation = resultSet.getString("equation");
                        equations.add(equation);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return equations;
    }
}
