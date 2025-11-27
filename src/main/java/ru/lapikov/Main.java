package ru.lapikov;

import ru.lapikov.dao.impl.PersonDaoImpl;
import ru.lapikov.database.LiquibaseRunner;
import ru.lapikov.models.Gender;
import ru.lapikov.models.Person;
import ru.lapikov.services.api.PersonService;
import ru.lapikov.services.impl.PersonServiceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Не указан режим работы!");
            return;
        }

        int mode = Integer.parseInt(args[0]);

        PersonService personService = new PersonServiceImpl(new PersonDaoImpl());

        switch (mode) {

            case 1 -> {
                System.out.println("Все таблицы создаются автоматически Liquibase.");
                LiquibaseRunner.runMigration();
            }

            case 2 -> {
                if (args.length < 4) {
                    System.out.println("Формат: 2 \"Фамилия Имя Отчество\" yyyy-mm-dd Male/Female");
                    return;
                }

                String[] fio = args[1].split(" ");

                if (fio.length != 3) {
                    System.out.println("ФИО должно содержать ровно 3 части");
                    return;
                }

                String last   = fio[0];
                String first  = fio[1];
                String middle = fio[2];

                LocalDate dob = LocalDate.parse(args[2]);
                Gender gender = Gender.valueOf(args[3].toUpperCase());

                Person p = new Person(0, first, last, middle, dob, gender);

                personService.createPerson(p);
            }

            case 3 -> {

                List<Person> persons = personService.getAllUniqueSortedByFio();

                for (Person p : persons) {
                    System.out.printf("%s %s %s %s %s %d лет %n",
                            p.getLastName(),
                            p.getFirstName(),
                            p.getMiddleName(),
                            p.getDateOfBirth(),
                            p.getGender(),
                            p.getAge()
                    );
                }
            }

            case 4 -> {

                long startGenerate = System.currentTimeMillis();
                List<Person> generated = generatePeople();
                long endGenerate = System.currentTimeMillis();

                System.out.println("Время на генерацию пользователей : " + (endGenerate - startGenerate) + " мс");

                long startSavePersons = System.currentTimeMillis();
                personService.saveAll(generated);
                long endSavePersons = System.currentTimeMillis();

                System.out.println("Время на сохранение всех пользователей: " + (endSavePersons - startSavePersons) + " мс");
            }

            case 5 -> {

                long start = System.currentTimeMillis();
//                List<Person> result = personService.find('F');
                int count = personService.countMaleWithPrefix('F');
                long end = System.currentTimeMillis();

                System.out.println("Время выполнения сортировки: " + (end - start) + " мс");
            }

            default -> System.out.println("Неизвестный режим: " + mode);
        }
    }

    private static List<Person> generatePeople() {

        List<Person> people = new ArrayList<>(1_000_100);

        String[] firstNames = {"John", "Alex", "Mike", "Robert", "David"};
        String[] lastNames  = {"Smith", "Brown", "Taylor", "Wilson", "Clark"};
        String[] middle     = {"A", "B", "C", "D", "E"};

        for (int i = 0; i < 1_000_000; i++) {
            Person p = new Person(
                    0,
                    firstNames[i % firstNames.length],
                    lastNames[i % lastNames.length],
                    middle[i % middle.length],
                    LocalDate.of(1980 + (i % 30), 1 + (i % 12), 1 + (i % 28)),
                    i % 2 == 0 ? Gender.MALE : Gender.FEMALE
            );
            people.add(p);
        }


        for (int i = 0; i < 100; i++) {
            people.add(new Person(
                    0,
                    "John",
                    "F" + i,
                    "A",
                    LocalDate.of(1990, 1, 1),
                    Gender.MALE
            ));
        }
        return people;
    }

    }

