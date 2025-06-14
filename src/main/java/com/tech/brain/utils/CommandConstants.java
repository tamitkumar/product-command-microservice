package com.tech.brain.utils;

public interface CommandConstants {
    String COLON = ":";
    String HYPHEN = "-";
    String DB_HOST = "localhost";
    String DB_PORT = "3306/";
    String DB_NAME = "cqrs?serverTimezone=UTC";
    String DB_URL_PREFIX = "jdbc:mysql://";
    String DB_USER_NAME = "root";
    String DB_PASSWORD = "0130Ec071007";
    String DB_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    String DIALECT_KEY = "spring.jpa.properties.hibernate.dialect";
    String DIALECT_VALUE = "org.hibernate.dialect.MySQL8Dialect";
    String SHOW_SQL_KEY = "spring.jpa.show-sql";
    boolean SHOW_SQL_VALUE = true;
    String FORMAT_SQL_KEY = "spring.jpa.properties.hibernate.format_sql";
    boolean FORMAT_SQL_VALUE = true;
    String HTTP_CODE_500 = "500";
    String HTTP_MSG_500 = "Error when processing the request.";
}
