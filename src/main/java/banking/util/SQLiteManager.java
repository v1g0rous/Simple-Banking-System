package banking.util;

import org.sqlite.SQLiteDataSource;
import banking.entity.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteManager implements DatabaseManager {
    static String databaseName;
    private static Connection connection;
    public static final String CREATE_CARD_TABLE_SQL = "CREATE TABLE IF NOT EXISTS card(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "number TEXT," + "pin TEXT," + "balance INTEGER DEFAULT 0);";


    public SQLiteManager(String[] args) {
        this.databaseName = getDatabaseName(args);
        initDatabaseWithTable();
    }

    private void initDatabaseWithTable() {
        executeUpdate(CREATE_CARD_TABLE_SQL);
    }

    public static Connection getConnectionInstance() {
        return getConnectionInstance(databaseName);
    }

    public static Connection getConnectionInstance(String fileName) {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            } else {
                String url = "jdbc:sqlite:" + fileName;

                SQLiteDataSource dataSource = new SQLiteDataSource();
                dataSource.setUrl(url);
                connection = dataSource.getConnection();
            }

        } catch (SQLException e) {
            new Log("Can't get connection to DB -" + fileName, e);
        }

        return connection;
    }


    // regular JDBC statement
    public static int executeUpdate(String sql) {
        Connection connection = getConnectionInstance(databaseName);
        int updatedRecords = 0;

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            updatedRecords = statement.executeUpdate();
        } catch (Exception e) {
            new Log("Error executing sql statement: " + sql, e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                new Log("Error closing connection to DB" + databaseName, e);
            }
        }

        return updatedRecords;
    }

    public static int executeUpdateAsTransaction(String sql, Connection connection) {
        int updatedRecords = 0;

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            updatedRecords = statement.executeUpdate();
        } catch (Exception e) {
            new Log("Error executing sql statement: " + sql, e);
        }
        return updatedRecords;
    }


    public static int executeUpdateAsTransaction(List<String> sqls) {
        Connection connection = getConnectionInstance();
        int updatedRecords = 0;


        try {
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();

            try {
                for (String sql : sqls) {
                   updatedRecords += SQLiteManager.executeUpdateAsTransaction(sql, connection);
                }
                connection.commit();

            } catch (Exception e) {
                new Log("Failed to execute sql statement" , e);
                try {
                    connection.rollback(savepoint);
                } catch (SQLException ex) {
                    new Log("Failed to rollback" , e);
                }
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    new Log("Error closing connection to DB" + databaseName, e);
                }
            }
        } catch (SQLException e) {
            new Log("Failed to executeUpdateAsTransaction", e);
        }

        return updatedRecords;
    }


    public static List<Map<String, Object>> executeQuery(String sql) {
        List<Map<String, Object>> records = new ArrayList<>();

        try (Connection connection = getConnectionInstance(databaseName);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet recordsSet = statement.executeQuery();
        ) {
            ResultSetMetaData metaData = recordsSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (recordsSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), recordsSet.getObject(i));
                }
                records.add(row);
            }

        } catch (Exception e) {
            new Log("Error executing sql statement: " + sql, e);
            throw new RuntimeException(e);
        }

        return records;
    }


    public static String getDatabaseName(String[] args) {
        String name = "";
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-fileName")) {
                name = args[i + 1];
            }
        }
        return name;
    }
}
