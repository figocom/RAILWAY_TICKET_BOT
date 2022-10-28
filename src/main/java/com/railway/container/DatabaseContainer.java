package com.railway.container;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseContainer {
    static final String user = "ugglrykujkcmkx";
    static final String password = "c7442184d300c7c7087dec64e9c43de6ffae98a60ab9147a648d51a3f3a223d5";
    static final String url = "jdbc:postgresql://ec2-54-229-217-195.eu-west-1.compute.amazonaws.com:5432/dblelog7e5b41u";

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
