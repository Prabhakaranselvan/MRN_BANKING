package com.mrn.utilshub;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	private static final ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

	private static final String DB_URL = "jdbc:mysql://localhost/MRNBankDB";
	private static final String USER = "root";
	private static final String PASS = "root";
	
	static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
	
	public static Connection getConnection() throws SQLException {
		Connection conn = threadLocal.get();
		
		if (conn == null || conn.isClosed()) {
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			conn.setAutoCommit(false);
			threadLocal.set(conn);
		}
		return conn;
	}

	public static void commit() throws SQLException {
		Connection conn = threadLocal.get();
		if (conn != null) {
			conn.commit();
		}
	}

	public static void rollback() {
		try {
			Connection conn = threadLocal.get();
			if (conn != null) {
				conn.rollback();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close() {
		try {
			Connection conn = threadLocal.get();
			if (conn != null) {
				conn.close();
				threadLocal.remove(); // To avoid memory leaks
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
