package com.railway.container;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseContainer {
    static final String user = "postgres";
    static final String password = "manguberdi66";
    static final String url = "jdbc:postgresql://localhost:5432/Railway_bot";

    public static   Connection getConnection(){
        try {
            Connection connection = DriverManager.getConnection(url,user,password);

            Class.forName("org.postgresql.Driver");

            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}
