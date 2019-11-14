import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class LabourSystem {
    private static ArrayList<User> users = new ArrayList<User>();
    private static ArrayList<Request> workerRequests = new ArrayList<Request>();
    private static ArrayList<Request> employeeRequests = new ArrayList<Request>();
    private static User currentUser;

    public static void main(String[] args) {
        load();
        for (Request request : employeeRequests) {
            System.out.println(request.getRequester().getName());
        }
        int operationNumber = 0;
        String login;
        String name;
        String password;
        do {
            System.out.println(
                    "1. Вход в систему\n" +
                            "2. Регистрация\n" +
                            "3. Выход из программы\n"
            );
            System.out.println("Введите номер операции: ");
            Scanner sc = new Scanner(System.in);
            operationNumber = sc.nextInt();
            if (operationNumber >= 1 && operationNumber <= 3) {
                switch (operationNumber) {
                    case (1):
                        System.out.println("Введите логин");
                        login = sc.next();
                        System.out.println("Введите пароль");
                        password = sc.next();
                        currentUser = findUser(login, password);
                        if (currentUser == null) {
                            continue;
                        } else {
                            System.out.println("Здраствуйте, " + currentUser.getName());
                            do {
                                System.out.println(
                                        "1. Регистрация заявки\n" +
                                                "2. Распределение заявок\n" +
                                                "3. Выход"
                                );
                                System.out.println("Введите номер операции: ");
                                operationNumber = sc.nextInt();
                                if (operationNumber >= 1 && operationNumber <= 3) {
                                    switch (operationNumber) {
                                        case (1):
                                            System.out.println("Если вы ищете работу, то введите 1. Если вы работодатель, то введите 2: ");
                                            int type = sc.nextInt();
                                            System.out.println("Введите название работы: ");
                                            String job = sc.next();
                                            System.out.println("Введите заработную плату: ");
                                            int payment = sc.nextInt();
                                            System.out.println("Введите количество часов работы в неделю: ");
                                            int hours = sc.nextInt();
                                            Request request = new Request(new Client(currentUser.getName(),
                                                    currentUser.getLogin(), currentUser.getPassword()), job, payment, hours, type);
                                            if (type == 1) {
                                                employeeRequests.add(request);
                                            } else {
                                                workerRequests.add(request);
                                            }
                                            break;
                                        case (2):
                                            processRequests();
                                            break;
                                        case (3):
                                            save();
                                            System.exit(0);
                                            break;
                                    }
                                }
                            } while (true);
                        }
                    case (2):
                        System.out.println("Введите имя");
                        name = sc.next();
                        System.out.println("Введите логин");
                        do {
                            boolean exists = false;
                            login = sc.next();
                            for (User user : users) {
                                if (user.getLogin().equals(login)) {
                                    System.out.println("Данный логин уже существует");
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                break;
                            }
                        } while (true);
                        System.out.println("Введите пароль");
                        password = sc.next();
                        User user = new User(name, login, password);
                        addUser(user);
                        System.out.println("Теперь вы можете зайти под своим логином и паролем");
                        break;
                    case (3):
                        save();
                        System.exit(0);
                        break;
                }
            }
        } while (true);
    }

    public static void addUser(User user) {
        users.add(user);
    }

    public static User findUser(String login, String password) {
        for (User user : users) {
            if (user.enter(login, password)) {
                return user;
            }
        }
        System.out.println("Неверный логин или пароль");
        return null;
    }

    public static void save() {
        try (
                FileOutputStream usersFile = new FileOutputStream("users.dat", false);
                ObjectOutputStream oosUser = new ObjectOutputStream(usersFile)) {
            oosUser.writeObject(users);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try (
                FileOutputStream workersRequestsFile = new FileOutputStream("worker_requests.dat", false);
                ObjectOutputStream oosWorkerRequest = new ObjectOutputStream(workersRequestsFile)) {
            oosWorkerRequest.writeObject(workerRequests);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        try (
                FileOutputStream employeeRequestsFile = new FileOutputStream("employee_requests.dat", false);
                ObjectOutputStream oosEmployeeRequest = new ObjectOutputStream(employeeRequestsFile)) {
            oosEmployeeRequest.writeObject(employeeRequests);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static void load() {
        try (ObjectInputStream oisUsers = new ObjectInputStream(new FileInputStream("users.dat"))) {
            users = (ArrayList<User>) oisUsers.readObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try (ObjectInputStream oisWorkerRequests = new ObjectInputStream(new FileInputStream("worker_requests.dat"))) {
            workerRequests = (ArrayList<Request>) oisWorkerRequests.readObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try (ObjectInputStream oisEmployeeRequests = new ObjectInputStream(new FileInputStream("employee_requests.dat"))) {
            employeeRequests = (ArrayList<Request>) oisEmployeeRequests.readObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void clearWorkerRequests(Client employee) {
        for (int i = employeeRequests.size()-1; i >= 0; i--) {
            if (employeeRequests.get(i).getRequester().equals(employee)) {
                employeeRequests.remove(employeeRequests.get(i));
            }
        }
    }

    public static void processRequests() {
        boolean found = false;
        for (int i = employeeRequests.size()-1; i >= 0; i--) {
            for (Request workerRequest : workerRequests) {
                if (
                        employeeRequests.get(i).getHoursinWeek() == workerRequest.getHoursinWeek() &&
                                employeeRequests.get(i).getJob().equals(workerRequest.getJob()) &&
                                employeeRequests.get(i).getPayment() == workerRequest.getPayment()
                ) {
                    found = true;
                    employeeRequests.get(i).getRequester().takeResult(workerRequest);
                    workerRequests.remove(workerRequest);
                    break;
                }

            }
            if (found) {
                clearWorkerRequests(employeeRequests.get(i).getRequester());
            } else {
                System.out.println(employeeRequests.get(i).getRequester().getName() + ", для вас работа не найдена");
            }
        }
    }
}
