package ru.lapikov.dao.impl;

import ru.lapikov.dao.api.PersonDao;
import ru.lapikov.database.ConnectionManager;
import ru.lapikov.models.Gender;
import ru.lapikov.models.Person;
import ru.lapikov.models.PersonFilter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PersonDaoImpl implements PersonDao {

    private static final String SELECT_ALL_PERSON = """
            SELECT id, first_name, last_name, middle_name, date_of_birth, gender
            FROM app.person
            """;

    private static final String INSERT_PERSON = """
            INSERT INTO app.person (first_name, last_name, middle_name, date_of_birth, gender)
            VALUES ( ?, ?, ?, ?, ?)
            RETURNING id;
            """ ;

    @Override
    public List<Person> getAll() throws SQLException {

        List<Person> persons = new ArrayList<>();

        try(Connection connection = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PERSON);
            ResultSet resultSet = preparedStatement.executeQuery())
        {
            while(resultSet.next())
            {
                long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String middleName = resultSet.getString("middle_name");
                LocalDate dateOfBirth = resultSet.getDate("date_of_birth").toLocalDate();
                Gender gender = Gender.valueOf(resultSet.getString("gender"));

                persons.add(new Person(id, firstName, lastName, middleName, dateOfBirth, gender));
            }
        }
        return persons;
    }

    @Override
    public long addPerson(Person person) throws SQLException {

        try(Connection connection = ConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PERSON))
        {
            preparedStatement.setString(1, person.getFirstName());
            preparedStatement.setString(2, person.getLastName());
            preparedStatement.setString(3, person.getMiddleName());
            preparedStatement.setDate(4, java.sql.Date.valueOf(person.getDateOfBirth()));
            preparedStatement.setString(5, person.getGender().name());

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getLong("id");
        }
    }

    @Override
    public List<Person> getPersonByFilter(PersonFilter filter) throws SQLException {

        StringBuilder sql = new StringBuilder("""
            SELECT id, first_name, last_name, middle_name, date_of_birth, gender
            FROM app.person
            WHERE 1=1
            """);

        List<Object> params = new ArrayList<>();

        if (filter.getFirstName() != null) {
            sql.append(" AND first_name = ?");
            params.add(filter.getFirstName());
        }

        if (filter.getLastName() != null) {

            if (filter.getLastName().length() == 1) {
                sql.append(" AND last_name LIKE '")
                        .append(filter.getLastName())
                        .append("%'");
            } else {
                sql.append(" AND last_name = ?");
                params.add(filter.getLastName());
            }
        }

        if (filter.getMiddleName() != null) {
            sql.append(" AND middle_name = ?");
            params.add(filter.getMiddleName());
        }

        if (filter.getDateOfBirth() != null) {
            sql.append(" AND date_of_birth = ?");
            params.add(java.sql.Date.valueOf(filter.getDateOfBirth()));
        }

        if (filter.getGender() != null) {
            sql.append(" AND gender = ?");
            params.add(filter.getGender().name());
        }

        sql.append(" ORDER BY last_name, first_name, middle_name");

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            List<Person> result = new ArrayList<>();
            while (rs.next()) {
                result.add(toPerson(rs));
            }
            return result;
        }
    }

    public int countMaleWithPrefix(String prefix) throws SQLException {
        String sql = """
        SELECT count(*)
        FROM app.person
        WHERE gender = 'MALE'
          AND last_name LIKE ?
    """;

        try(Connection c = ConnectionManager.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, prefix + "%");
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }


    @Override
    public void addBatch(List<Person> persons) throws SQLException {

        String sql = """
       INSERT INTO app.person (first_name, last_name, middle_name, date_of_birth, gender)
       VALUES (?, ?, ?, ?, ?)
    """;

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (int i = 0; i < persons.size(); i++) {
                Person p = persons.get(i);

                ps.setString(1, p.getFirstName());
                ps.setString(2, p.getLastName());
                ps.setString(3, p.getMiddleName());
                ps.setDate(4, java.sql.Date.valueOf(p.getDateOfBirth()));
                ps.setString(5, p.getGender().name());

                ps.addBatch();

                if (i % 10_000 == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            conn.commit();
        }
    }

    private Person toPerson(ResultSet rs) throws SQLException {
        return new Person(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("middle_name"),
                rs.getDate("date_of_birth").toLocalDate(),
                Gender.valueOf(rs.getString("gender")));
    }

}
