public class Client extends User {

    public Client(String name, String login, String password) {
        super(name, login, password);
    }

    public void takeResult(Request request) {
        System.out.println(this.getName() + " получил работу, которую хотел, у работодателя " + request.getRequester().getName());
    }
}