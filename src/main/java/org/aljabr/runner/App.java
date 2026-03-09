package org.aljabr.runner;

import java.sql.Connection;
import java.sql.SQLException;

import org.postgresql.ds.PGSimpleDataSource;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");

		try (Connection connection = getConnection()) {
			if (connection == null)
			{
				System.out.println("Failed to connect to the database.");
				return;
			}
			System.out.println("Connected to the database!");

			try (// request all tables from the public schema
			var rs = connection.createStatement()
					.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'"))
			{
				while (rs.next()) {
					System.out.println("Table: " + rs.getString("table_name"));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		String postgresUrl = System.getenv("DATABASE_URL");
		String postgresUser = System.getenv("DATABASE_USER");
		String postgresPassword = System.getenv("DATABASE_PASSWORD");

		PGSimpleDataSource dataSource = new PGSimpleDataSource();
		String url = postgresUrl + "?user=" + postgresUser + "&password=" + postgresPassword;
		dataSource.setUrl(url);

		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
