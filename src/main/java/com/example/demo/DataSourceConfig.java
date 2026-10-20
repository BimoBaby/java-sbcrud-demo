package com.example.demo;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

	@Bean
	DataSource dataSource(
			@Value("${MYSQLHOST:localhost}") String host,
			@Value("${MYSQLPORT:3306}") String port,
			@Value("${MYSQLDATABASE:demo}") String database,
			@Value("${MYSQLUSER:root}") String username,
			@Value("${MYSQLPASSWORD:}") String password) {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl("jdbc:mysql://" + oneLine(host) + ":" + oneLine(port) + "/" + oneLine(database)
				+ "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
		dataSource.setUsername(oneLine(username));
		dataSource.setPassword(password.replaceAll("[\\r\\n\\t]", "").trim());
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		return dataSource;
	}

	private static String oneLine(String value) {
		return value.replaceAll("\\s+", "");
	}

}
