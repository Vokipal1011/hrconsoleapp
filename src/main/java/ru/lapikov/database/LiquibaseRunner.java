package ru.lapikov.database;


import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class LiquibaseRunner {

    public static void runMigration() throws Exception{

        Properties properties = new Properties();

        try(InputStream is = LiquibaseRunner.class.getClassLoader()
                        .getResourceAsStream("db.properties"))
        {
            if (is == null) {
                throw new RuntimeException("db.properties не найден!");
            }
            properties.load(is);
        }

        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        try(Connection connection = DriverManager.getConnection(url,username,password))
        {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(
                            new JdbcConnection(connection));
        database.setDefaultSchemaName(properties.getProperty("db.defaultSchemaName"));
        database.setLiquibaseSchemaName(properties.getProperty("db.liquibaseSchemaName"));

        Liquibase liquibase = new Liquibase(
                "db/changelog/master.xml",
                new ClassLoaderResourceAccessor(),
                database
        );

        liquibase.update();
        }

        System.out.println("Liquibase миграции успешно выполнены!");
    }

}
