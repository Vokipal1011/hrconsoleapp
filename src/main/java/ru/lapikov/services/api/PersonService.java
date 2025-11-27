package ru.lapikov.services.api;

import ru.lapikov.models.Person;

import java.util.List;

public interface PersonService {

    long createPerson(Person person);

    List<Person> getAllUniqueSortedByFio();

    void saveAll(List<Person> persons);

    List<Person> find(char initial);

    int countMaleWithPrefix(char initial);
}
