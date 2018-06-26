package MyDatabase;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Benjamin Belay on 25/04/2018.
 */
public interface MyDatabase {
    public void setupXml(String xmlData);
    public void setupJson(String jsonData);
    public CompletableFuture<Boolean> isValidOrderId(String orderId);
    CompletableFuture<Boolean> isCanceledOrder(String orderId);
    CompletableFuture<Boolean> isModifiedOrder(String orderId);
    CompletableFuture<OptionalInt> getSeatOrdered(String orderId);
    CompletableFuture<List<Integer>> getHistoryOfOrder(String orderId);
    CompletableFuture<List<String>> getOrderIdsForUser(String userId);
    CompletableFuture<Long> getTotalTimeSpentByUser(String userId);
    CompletableFuture<List<String>> getUsersThatWatched(String movieId);
    CompletableFuture<List<String>> getOrderIdsThatPurchased(String movieId);
    CompletableFuture<OptionalLong> getTotalNumberOfTicketsPurchased(String movieId);
    CompletableFuture<OptionalDouble> getCancelRatioForMovie(String movieId);
    CompletableFuture<OptionalDouble> getModifyRatioForUser(String userId);
    CompletableFuture<Map<String, List<Long>>> getAllMoviesSeen(String userId);
    CompletableFuture<Map<String, List<Long>>> getTakenSeats(String movieId);

    public void addEntry(byte[] key, byte[] value);
    public byte[] get(byte[] key) throws InterruptedException;
}
