package libClasses;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class Order extends Modifier<Movie> {

    private String id;
    private String user;
    private String movieName;
    //    Integer seatNumber;
    protected Deque<Integer> seatHistory;
    protected boolean isApplied = false;

    public Order(String id, String user, String movieName, Integer seatNumber) {
        this.id = id;
        this.user = user;
        this.movieName = movieName;
//        this.seatNumber = seatNumber;
        this.seatHistory = new ArrayDeque<>();
        this.seatHistory.add(seatNumber);
    }

    public Order(String id, String user, String movieName) {
        this.id = id;
        this.user = user;
        this.movieName = movieName;
//        this.seatHistory = new ArrayDeque<>();
    }

    @Override
    public void apply() {
        if (target == null) {
            throw new NotImplementedException();
        }
//        if (isApplied) {
//            throw new InvalidStateException();
//        }
        assert !isApplied;  // TODO: 11-Jun-18 this is danger!!! welcome to the DANGER ZONE!
        target.takenSeats.add(currentSeat());
        isApplied = true;
    }

    public ArrayList<Integer> getSeatHistory() {
        return new ArrayList<>(seatHistory);
    }

    @Override
    public void undo() {
        if (target == null) {
            throw new NotImplementedException();
        }
        if (!target.takenSeats.contains(currentSeat())) {
            throw new NotImplementedException();
        }
        if (!isApplied) {

            throw new NotImplementedException();
        }
        target.takenSeats.remove(currentSeat());
        isApplied = false;
    }

    private Integer currentSeat() {
        return this.seatHistory.peek();
    }

    public boolean isModified() {
        return seatHistory.size() > 1;
    }

    public void updateSeat(Integer newSeat) {
        if (isApplied) {
            this.undo();
        }
        this.seatHistory.add(newSeat);
        if (isApplied) {
            this.apply();
        }
    }

    public Integer getSeat() {
        return seatHistory.peek();
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getMovieName() {
        return movieName;
    }

    public boolean applied() {
        return isApplied;
    }
}
