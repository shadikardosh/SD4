package libClasses;

// TODO: Maybe Modifier<T> then have Modifier<Movie> as Order and Modifier<Modifier<Movie>> as Cancel/Change

import java.util.ArrayList;

public class Movie {
    private String name;
    private Integer length;
    ArrayList<Integer> takenSeats = new ArrayList<>();   //each integer is in 1-100 range

    public Movie(String name, Integer length) {
        // TODO: 10-Jun-18 verify input?
        this.name = name;
        this.length = length;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public Integer getOrderNum() {
        return takenSeats.size();
    }

    public ArrayList<Integer> getTakenSeats() {
        return takenSeats;
    }

    public void updateTakenSeats(ArrayList<Integer> seats) {
        takenSeats.addAll(seats);
    }
}
