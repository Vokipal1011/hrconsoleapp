package ru.lapikov.dao.api;

import ru.lapikov.models.Person;
import ru.lapikov.models.PersonFilter;

import java.sql.SQLException;
import java.util.List;

public interface PersonDao {

    public List<Person> getAll() throws SQLException;

    public long addPerson(Person person) throws SQLException;

    public List<Person> getPersonByFilter(PersonFilter filter) throws SQLException;

    void addBatch(List<Person> persons) throws SQLException;

    public int countMaleWithPrefix(String prefix) throws SQLException;

}
