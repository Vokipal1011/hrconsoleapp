package ru.lapikov.services.impl;

import ru.lapikov.dao.api.PersonDao;
import ru.lapikov.models.Gender;
import ru.lapikov.models.Person;
import ru.lapikov.models.PersonFilter;
import ru.lapikov.services.api.PersonService;


import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class PersonServiceImpl implements PersonService {

    private final PersonDao personDao;

    public PersonServiceImpl(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Override
    public long createPerson(Person person) {
        try {
            return personDao.addPerson(person);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании сотрудника", e);
        }
    }

    @Override
    public List<Person> getAllUniqueSortedByFio() {
        try {
            List<Person> all = personDao.getAll();

            Map<String, Person> uniqueByFioAndDate = new LinkedHashMap<>();
            for (Person p : all) {
                String key = p.getLastName() + "|" + p.getFirstName() + "|" +
                        p.getMiddleName() + "|" + p.getDateOfBirth();
                uniqueByFioAndDate.putIfAbsent(key, p);
            }

            List<Person> uniqueList = new ArrayList<>(uniqueByFioAndDate.values());


            uniqueList.sort(
                    Comparator.comparing(Person::getLastName)
                            .thenComparing(Person::getFirstName)
                            .thenComparing(Person::getMiddleName)
            );

            return uniqueList;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка сотрудников", e);
        }
    }

    @Override
    public void saveAll(List<Person> persons) {
        try {
//            for (Person person : persons) {
//                personDao.addPerson(person);
//            }
            personDao.addBatch(persons);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при пакетном сохранении сотрудников", e);
        }
    }

    @Override
    public List<Person> find(char initial) {
        try {

            PersonFilter filter = new PersonFilter();
            filter.setGender(Gender.MALE);

            List<Person> males = personDao.getPersonByFilter(filter);
            String prefix = String.valueOf(initial);


            return males.stream()
                    .filter(p -> p.getLastName() != null &&
                            p.getLastName().startsWith(prefix))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при выборке сотрудников по условию", e);
        }
    }

    @Override
    public int countMaleWithPrefix(char initial) {
        try {
            String prefix = String.valueOf(initial);
            return personDao.countMaleWithPrefix(prefix);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при подсчёте сотрудников", e);
        }
    }

}
