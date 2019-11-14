import java.io.Serializable;

public class Request implements Serializable {
    private Client requester;
    private String job;
    private int payment;
    private int hoursinWeek;
    private int type;

    public Request(Client requester, String job, int payment, int hours, int type) {
        this.requester = requester;
        this.job = job;
        this.payment = payment;
        this.hoursinWeek = hours;
        this.type = type;
    }

    public Client getRequester() {
        return this.requester;
    }

    public String getJob() {
        return this.job;
    }

    public int getPayment() {
        return this.payment;
    }

    public int getHoursinWeek() {
        return this.hoursinWeek;
    }

    public int getType() {
        return this.type;
    }
}
