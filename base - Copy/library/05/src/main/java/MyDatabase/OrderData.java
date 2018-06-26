package MyDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderData {
    private String orderID;
    private String userID;
    private String movieName;
    private List<String> seats;
    private Boolean isCanceled;

    public OrderData(String userID, String movieName, List<String> seats, String orderID) {
        this.orderID = orderID;
        this.userID = userID;
        this.movieName = movieName;
        this.seats = new ArrayList<>(seats);
        this.isCanceled = false;
    }

    public OrderData(OrderData data) {
        this.orderID = data.getOrderID();
        this.userID = data.getUserID();
        this.movieName = data.getmovieName();
        this.seats = new ArrayList<>(data.getSeats());
        this.isCanceled = data.getIsCanceled();
    }

    public String getOrderID() { return orderID; }
    public String getUserID() { return userID; }
    public String getmovieName() { return movieName; }
    public List<String> getSeats() { return seats; }
    public Boolean getIsCanceled() { return isCanceled; }

    public void cancelOrder() {
        this.isCanceled = true;
    }
    public void addSeat(String seat) {
        seats.add(seat);
    }

    public void uncancelOrder() {
        this.isCanceled = false;
    }
    public Boolean isModified() {
        if (seats.size() > 1)
            return true;
        return false;
    }
}
