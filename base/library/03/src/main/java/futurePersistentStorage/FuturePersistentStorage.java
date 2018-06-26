package futurePersistentStorage;

import com.google.inject.Inject;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabaseFactory;
import libClasses.CancellableOrder;
import libClasses.Movie;

import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import static java.util.stream.Collectors.groupingBy;

public class FuturePersistentStorage {
    public static final String MOVIE_DB = "movieDb";
    public static final String ORDER_DB = "orderDb";
    public static final String MOVIE_ORDERS_DB = "movieOrdersDb";
    public static final String USER_ORDERS_DB = "userOrdersDb";
    private final CompletableFuture<FutureSecureDatabase> movieDb;
    private final CompletableFuture<FutureSecureDatabase> orderDb;
    private final CompletableFuture<FutureSecureDatabase> movieOrdersDb;
    private final CompletableFuture<FutureSecureDatabase> userOrdersDb;
    private FutureSecureDatabaseFactory fsdbf;
//    private FutureSecureDatabase completeMoviesFDB;
//    private FutureSecureDatabase completeOrdersFDB;
//    private FutureSecureDatabase completeUserOrdersFDB;
//    private FutureSecureDatabase completeMovieOrdersFDB;


    // TODO: 12-Jun-18 remove todo's from all the useless places, like this one

    @Inject
    public FuturePersistentStorage(FutureSecureDatabaseFactory fsdbf) {
        this.fsdbf = fsdbf;
        this.movieDb = fsdbf.open(MOVIE_DB);
        this.orderDb = fsdbf.open(ORDER_DB);
        this.movieOrdersDb = fsdbf.open(MOVIE_ORDERS_DB);
        this.userOrdersDb = fsdbf.open(USER_ORDERS_DB);
    }

    public CompletableFuture<Void> saveMovies(HashMap<String, Movie> movieMap) {
        return movieDb.thenAccept(fdb -> {
//            return fdb.addEntry();
//            movieMap.forEach((movieName, movie) -> {
//                try {
//                    addMovieToDb(fdb, movieName, movie);
//                } catch (DataFormatException e) {
//                    System.out.println("failed to save movies DB, got exception - " + e.getMessage());
//                }
            try {
                for (String movieName : movieMap.keySet()) {
                    Movie movie = movieMap.get(movieName);
                    addMovieToDb(fdb, movieName, movie); // TODO: 12-Jun-18 make keys unique
                }
//                completeMoviesFDB = fdb;
            } catch (InterruptedException | ExecutionException | DataFormatException e) {
                System.out.println("failed to save movies DB, got exception - " + e.getMessage());
            }
//        }).exceptionally(throwable -> {
//            System.out.println("failed to save movies DB, got exception - " + throwable.getMessage());
//            return null;    // TODO: 12-Jun-18 don't know what to return here...
        });
    }

    public CompletableFuture<Void> saveOrders(HashMap<String, CancellableOrder> orderMap) {
        return orderDb.thenAccept(fdb -> {
//            return fdb.addEntry();
//            movieMap.forEach((movieName, movie) -> {
//                try {
//                    addMovieToDb(fdb, movieName, movie);
//                } catch (DataFormatException e) {
//                    System.out.println("failed to save movies DB, got exception - " + e.getMessage());
//                }
            try {
                for (String orderName : orderMap.keySet()) {
                    CancellableOrder order = orderMap.get(orderName);
                    addOrderToDb(fdb, orderName, order); // TODO: 12-Jun-18 make keys unique
                }
//                completeOrdersFDB = fdb;
            } catch (InterruptedException | ExecutionException | DataFormatException e) {
                System.out.println("failed to save movies DB, got exception - " + e.getMessage());
            }
//        }).exceptionally(throwable -> {
//            System.out.println("failed to save movies DB, got exception - " + throwable.getMessage());
//            return null;    // TODO: 12-Jun-18 don't know what to return here...
        });
    }

    private void addOrderToDb(FutureSecureDatabase fdb, String orderName, CancellableOrder order)
            throws DataFormatException, ExecutionException, InterruptedException {
        ArrayList<byte[]> movieBytes = getOrderBytes(order);
        // TODO: 12-Jun-18 lowercase?
        fdb.addEntry(orderName.getBytes(), movieBytes.get(0)).get(); // same as other comment. need to block?
        for (int i = 1; i < movieBytes.size(); i++) {
            fdb.addEntry((orderName + '_' + String.valueOf(i)).getBytes(), movieBytes.get(i)).get();   //unique keys? need get? have to wait for previous one to get back anyway i think.
        }
    }

    private void addMovieToDb(FutureSecureDatabase db, String movieName, Movie movie)
            throws DataFormatException, ExecutionException, InterruptedException {
        ArrayList<byte[]> movieBytes = getMovieBytes(movie);
        // TODO: 12-Jun-18 lowercase?
        db.addEntry(movieName.getBytes(), movieBytes.get(0)).get(); // same as other comment. need to block?
        for (int i = 1; i < movieBytes.size(); i++) {
            db.addEntry((movieName + '_' + String.valueOf(i)).getBytes(), movieBytes.get(i)).get();   //unique keys? need get? have to wait for previous one to get back anyway i think.
        }
    }

    public CompletableFuture<Void> saveUserOrders(HashMap<String, List<String>> userOrders) {
        CompletableFuture<Void> res = saveOrderIdList(userOrders, userOrdersDb);
//        try {
//            completeUserOrdersFDB = userOrdersDb.get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();    // TODO: 12-Jun-18 handle exceptions
//        }
        return res;
    }

    public CompletableFuture<Void> saveMovieOrders(HashMap<String, List<String>> movieOrders) {
        CompletableFuture<Void> res = saveOrderIdList(movieOrders, movieOrdersDb);
//        try {
//            completeMovieOrdersFDB = movieOrdersDb.get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();    // TODO: 12-Jun-18 handle exceptions
//        }
        return res;
    }

    private CompletableFuture<Void> saveOrderIdList(HashMap<String, List<String>> ordersMap, CompletableFuture<FutureSecureDatabase> db) {
        Map<byte[], List<byte[]>> ordersBytes = ordersMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey().getBytes(), e -> e.getValue()
                        .stream()
                        .map(String::getBytes)
                        .collect(Collectors.toList())));

        Map<byte[], byte[]> ordersConcat = new HashMap<>();

        ordersBytes.forEach((k, v) -> {
            StringBuilder sb = new StringBuilder();
            for (byte[] bytes : v) {
                sb.append(Arrays.toString(bytes)).append('\0');
            }
            ordersConcat.put(k, sb.toString().getBytes());
        });

        Map<byte[], byte[]> lengths = new HashMap<>();
        Map<byte[], byte[]> subOrders = new HashMap<>();

        Integer chunk = 100; // chunk size to divide

        ordersConcat.forEach((k, v) -> {
            Integer chunkNum = 1;
            for (int i = 0; i < v.length; i += chunk) {
                byte[] value = Arrays.copyOfRange(v, i, Math.min(v.length, i + chunk));
                byte[] key = (Arrays.toString(k) + '_' + chunkNum.toString()).getBytes();
                subOrders.put(key, value);
                chunkNum++;
            }
            lengths.put(k, chunkNum.toString().getBytes());
        });

        return db.thenAccept(fdb -> {
//            List<CompletableFuture<Void>> adds = new ArrayList<>();
            lengths.forEach((k, v) -> {
                try {
//                    adds.add(fdb.addEntry(k, v));
                    fdb.addEntry(k, v).get(); // TODO: 12-Jun-18 What do we do in case of exceptions?
                } catch (DataFormatException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
            subOrders.forEach((k, v) -> {
                try {
//                    adds.add(fdb.addEntry(k, v));
                    fdb.addEntry(k, v).get();
                } catch (DataFormatException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });

        });
    }

//    private Function<Map.Entry<byte[], List<byte[]>>, Boolean> listTooLong() {
//        return entry -> entry.getValue().stream().mapToInt(bytes -> bytes.length).sum() >= 100;
//    }

//    private Function<Map.Entry<byte[], byte[]>, Boolean> listTooLong() {
//        return entry -> entry.getValue().length >= 100;
//    }

//    private boolean listTooLong(List<byte[]> list) {
//        return list.stream().mapToInt(bytes -> bytes.length).sum() > 100;
//    }

//    public void saveMovieOrders(HashMap<String, List<String>> movieOrders) {
//
//    }

    public CompletableFuture<Movie> readMovie(String movieId) {
        // TODO: 12-Jun-18 should maybe do if (movieDb.done()) access complete fdb, else compose (does this messup the original future?)
//        if (!movieDb.isDone()){
//            movieDb.join();
//        }
        try {
            return movieDb.get().get(movieId.getBytes())    //if fdb is complete, can you call thenCompose many times and it returns immediately?
                    //        if (movieDb.isDone())
                    //        return completeMoviesFDB.get(movieId.getBytes())    //if fdb is complete, can you call thenCompose many times and it returns immediately?
                    .thenApply(bytes -> {
                        String[] strings = Arrays.toString(bytes).split("\0");
                        assert (strings.length == 2);
                        Integer dataSize = Integer.valueOf(strings[1]);
                        ArrayList<CompletableFuture<byte[]>> completableFutures = new ArrayList<>();
                        for (int i = 1; i <= dataSize; i++) {
                            byte[] key = (movieId + "_" + String.valueOf(i)).getBytes();
                            completableFutures.add(i - 1, movieDb.join().get(key));    // TODO: 12-Jun-18 remember that -1 shit
                        }
                        completableFutures.add(CompletableFuture.completedFuture(strings[0].getBytes()));
                        return completableFutures.stream().map(CompletableFuture::join).collect(Collectors.toList());
                    })
                    .thenApply(listOfBytes -> {
                        StringBuilder builder = new StringBuilder();
                        Integer length = Integer.valueOf(Arrays.toString(listOfBytes.remove(listOfBytes.size() - 1)));
                        ArrayList<Integer> seats = new ArrayList<>();
                        for (byte[] bytes : listOfBytes) { // TODO: 12-Jun-18 WTF are we doing here Shai
                            // TODO: 12-Jun-18 THIS IS WRONG
                            seats.add(Integer.valueOf(Arrays.toString(bytes)));
                            //                        builder.append(Arrays.toString(bytes)).append("\0");
                        }
                        //                    String[] strings = builder.toString().split("\0");
                        //                    for (String string : strings) {
                        //                        seats.add(Integer.valueOf(string));
                        //                    }
                        Movie movie = new Movie(movieId, length);
                        movie.updateTakenSeats(seats);
                        return movie;
                    });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CompletableFuture<CancellableOrder> readOrder(String orderId) {
//        if (!orderDb.isDone()){
//            orderDb.join();
//        }
        try {
            return orderDb.get().get(orderId.getBytes())
                    .thenApply(bytes -> {
                        String[] strings = Arrays.toString(bytes).split("\0");
                        assert (strings.length == 4);
                        Integer dataSize = Integer.valueOf(strings[3]);
                        ArrayList<CompletableFuture<byte[]>> completableFutures = new ArrayList<>();
                        for (int i = 1; i <= dataSize; ++i) {
                            byte[] key = (orderId + "_" + String.valueOf(i)).getBytes();
                            completableFutures.add(i - 1, orderDb.join().get(key));
                        }
                        // adding movie name
                        completableFutures.add(CompletableFuture.completedFuture(strings[0].getBytes()));
                        // adding user name
                        completableFutures.add(CompletableFuture.completedFuture(strings[1].getBytes()));
                        // adding modified
                        //                    completableFutures.add(CompletableFuture.completedFuture(strings[2].getBytes()));
                        // adding cancelled
                        completableFutures.add(CompletableFuture.completedFuture(strings[2].getBytes()));
                        return completableFutures.stream().map(CompletableFuture::join).collect(Collectors.toList());
                    })
                    .thenApply(listOfBytes -> {
                        Boolean cancelled = Boolean.valueOf(Arrays.toString(listOfBytes.remove(listOfBytes.size() - 1)));
                        String userId = Arrays.toString(listOfBytes.remove(listOfBytes.size() - 1));
                        String movieId = Arrays.toString(listOfBytes.remove(listOfBytes.size() - 1));
                        ArrayList<Integer> seats = new ArrayList<>();
                        for (byte[] bytes : listOfBytes) {
                            seats.add(Integer.valueOf(Arrays.toString(bytes)));
                            //                        builder.append(Arrays.toString(bytes)).append("\0");
                        }
                        //                    String[] strings = builder.toString().split("\0");
                        //                    for (String string : strings) {
                        //                        seats.add(Integer.valueOf(string));
                        //                    }
                        CancellableOrder order = new CancellableOrder(orderId, userId, movieId, seats, cancelled);
                        return order;
                    });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Separates the movie data to byte arrays small enough to store with addEntry. they are separated in each array
     * by 0 bytes (char '\0').
     * The first entry is the movie length, '\0', then the amount of remaining elements in the list.
     */
    private ArrayList<byte[]> getMovieBytes(Movie movie) {
        ArrayList<byte[]> res = new ArrayList<>();

        ArrayList<Integer> takenSeats = movie.getTakenSeats();
//        int i = 1;
//        for (Integer seatNum : takenSeats) {
//            if (builder.length() + seatNum.toString().length() + 1 > 100) {    // assuming one char is a byte.
//                res.add(i, builder.toString().getBytes());
//                builder = new StringBuilder();
//                i++;
//            }
//            builder.append(seatNum).append('\0');
//        }
//        if (builder.length() > 0) {
//            res.add(i, builder.toString().getBytes());
//        }
        int i = fillSeats(res, takenSeats);

        String string = movie.getLength().toString() +
                '\0' +
                String.valueOf(i);
        res.add(0, string.getBytes());
        return res;
    }

    private ArrayList<byte[]> getOrderBytes(CancellableOrder order) {
        ArrayList<byte[]> res = new ArrayList<>();

        ArrayList<Integer> seatHistory = order.getSeatHistory();
        int numOfEntries = fillSeats(res, seatHistory);

        String string = order.getMovieName() +    // note the order. movieName -> user -> modified -> canceled -> numEntries
                '\0' +
                order.getUser() +
                '\0' +
//                .append(order.isModified())    // TODO: 12-Jun-18 size of boolean in string builder?
//                .append('\0')
                order.isCancelled() +
                '\0' +
                String.valueOf(numOfEntries);
        res.add(0, string.getBytes());

        return res;
    }

    private int fillSeats(ArrayList<byte[]> res, ArrayList<Integer> seats) {
        StringBuilder builder = new StringBuilder();
        int i = 1;
        for (Integer seatNum : seats) {
            if (builder.length() + seatNum.toString().length() + 1 > 100) {    // assuming one char is a byte.
                res.add(i, builder.toString().getBytes());
                builder = new StringBuilder();
                i++;
            }
            builder.append(seatNum).append('\0');
        }
        if (builder.length() > 0) {
            res.add(i, builder.toString().getBytes());
        }

        return i;
    }

    // TODO: 12-Jun-18 return empty stuff when not found
    public CompletableFuture<ArrayList<String>> getMovieOrders(String movieId) {
        if (!movieOrdersDb.isDone()) {
            movieOrdersDb.join();
        }
        return readFullOrderLists(movieId, movieOrdersDb);
    }

    // TODO: 12-Jun-18 return empty stuff when not found

    public CompletableFuture<ArrayList<String>> getUserOrders(String userId) {
//        if (!userOrdersDb.isDone()) {
//            userOrdersDb.join();
//        }
        return readFullOrderLists(userId, userOrdersDb);
    }

    private CompletableFuture<ArrayList<String>> readFullOrderLists(String id, CompletableFuture<FutureSecureDatabase> db) {
        try {
            return db.get().get(id.getBytes())
                    .thenApply(bytes -> {
                        Integer numOfChunks = Integer.valueOf(Arrays.toString(bytes));
                        ArrayList<CompletableFuture<byte[]>> futureChunkList = new ArrayList<>();
                        for (int i = 1; i <= numOfChunks; i++) {
                            CompletableFuture<byte[]> currentIdChunk = db.join().get((id + "_" + String.valueOf(i)).getBytes());
                            futureChunkList.add(currentIdChunk);
                        }
                        return futureChunkList.stream().map(CompletableFuture::join)
                                .collect(Collectors.toCollection(ArrayList::new));
                    }).thenApply(byteList -> {
                        StringBuilder builder = new StringBuilder();
                        for (byte[] bytes : byteList) {
                            builder.append(Arrays.toString(bytes));
                        }
                        return new ArrayList<>(Arrays.asList(builder.toString().split("\0")));  // TODO: 12-Jun-18 hope this is what we want
                    });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
