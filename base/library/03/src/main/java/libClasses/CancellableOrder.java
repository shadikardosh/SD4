package libClasses;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class CancellableOrder extends Order {

    private boolean isCancelled = false;

    public CancellableOrder(String id, String user, String movieName, Integer seatNumber) {
        super(id, user, movieName, seatNumber);
    }

    public CancellableOrder(String orderId, String userId, String movieId, ArrayList<Integer> seats, Boolean cancelled) {
        super(orderId, userId, movieId);
        this.seatHistory = new ArrayDeque<>(seats);
        this.isCancelled = cancelled;
    }

//    public CancellableOrder() {
//
//    }

    @Override
    public void apply() {
//        isCancelled = false;
        if (!isCancelled) {
            super.apply();
        }
    }

    @Override
    public void undo() {
        if (!isCancelled) {
            super.undo();
        }
    }

    public void cancel() {
        if (isApplied) {
            this.undo();
        }
        isApplied = false;
        isCancelled = true;
    }

    public void uncancel() {
        this.isCancelled = false;
//        if (isApplied) {
//            this.apply();
//        }
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void updateSeat(Integer newSeat) {
        this.isCancelled = false;
        super.updateSeat(newSeat);
    }
}
