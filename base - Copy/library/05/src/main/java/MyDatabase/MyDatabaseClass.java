package MyDatabase;

import com.google.inject.Inject;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabaseFactory;
import org.json.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.NoSuchElementException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Benjamin Belay on 25/04/2018.
 */
public class MyDatabaseClass implements MyDatabase {
    private final FutureSecureDatabaseFactory sf;
    private String Separator = "\007";

    @Inject
    public MyDatabaseClass(FutureSecureDatabaseFactory sf) {
        this.sf = sf;
    }

    public void setupXml(String xmlData) {
        Map<String, String> Movies = new HashMap<>();
        Map<String, OrderData> Orders = new HashMap<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource xml = new InputSource(new StringReader(xmlData));
            Document doc = builder.parse(xml);
            NodeList roots = doc.getElementsByTagName("Root");
            for(int i = 0; i < roots.getLength(); i++) {
                Node r = roots.item(i);
                if(r.getNodeType() == Node.ELEMENT_NODE) {
                    Element root = (Element) r;
                    NodeList elements = root.getChildNodes();
                    for(int j = 0; j < elements.getLength(); j++) {
                        Node e = elements.item(j);
                        if(e.getNodeType() == Node.ELEMENT_NODE) {
                            switch(e.getNodeName()) {
                                case "Movie":
                                    handleMovie((Element) e, Movies);
                                    break;
                                case "Order":
                                    handleOrder((Element) e, Orders);
                                    break;
                                case "ModifyOrder":
                                    handleModifyOrder((Element) e, Orders);
                                    break;
                                case "CancelOrder":
                                    handleCancelOrder((Element) e, Orders);
                                    break;
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setup(Movies, Orders);
    }

    private static String getValue(Element element, String tagName) {
        Node node = element.getElementsByTagName(tagName).item(0).getChildNodes().item(0);
        return node.getNodeValue();
    }

    private void handleMovie(Element e, Map<String, String> Movies) {
        String Id = getValue(e, "id");
        String Length = getValue(e, "length");
        Movies.put(Id, Length);
    }

    private void handleOrder(Element e, Map<String, OrderData> Orders) {
        String watcherId = getValue(e, "watcher-id");
        String orderId = getValue(e, "order-id");
        String movieId = getValue(e, "movie-id");
        String seat = getValue(e, "seat");
        Orders.put(orderId, new OrderData(watcherId, movieId, (new ArrayList<>(Arrays.asList(seat))), orderId));
    }

    private void handleModifyOrder(Element e, Map<String, OrderData> Orders) {
        String orderId = getValue(e, "order-id");
        String seat = getValue(e, "seat");
        if(Orders.containsKey(orderId)) {
            Orders.get(orderId).addSeat(seat);
            Orders.get(orderId).uncancelOrder();
        }
    }

    private void handleCancelOrder(Element e, Map<String, OrderData> Orders) {
        String orderId = getValue(e, "order-id");
        if(Orders.containsKey(orderId)) {
            Orders.get(orderId).cancelOrder();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setupJson(String jsonData) {
        Map<String, String> Movies = new HashMap<>();
        Map<String, OrderData> Orders = new HashMap<String, OrderData>();

        int j = jsonData.indexOf("{");
        int k = jsonData.lastIndexOf("}");
        jsonData = jsonData.substring(j, k);
        jsonData = jsonData.trim();
        String[] ss = jsonData.split("},");

        for (int i=0; i<ss.length; i++) {
            ss[i] += "}";

            JSONObject obj = new JSONObject(ss[i]);
            String Type = obj.getString("type");
            switch (Type) {
                case "order": {
                    String orderID = obj.getString("order-id");
                    String userID = obj.getString("watcher-id");
                    String movieID = obj.getString("movie-id");
                    String seat = String.valueOf(obj.getInt("seat"));
                    Orders.put(orderID, new OrderData(userID, movieID, new ArrayList<>(Arrays.asList(seat)), orderID));
                    break;
                }
                case "movie": {
                    String id = obj.getString("id");
                    String length = String.valueOf(obj.getInt("length"));
                    Movies.put(id, length);
                    break;
                }
                case "modify-order": {
                    String orderID = obj.getString("order-id");
                    String seat = String.valueOf(obj.getInt("seat"));
                    if (Orders.containsKey(orderID)) {
                        Orders.get(orderID).addSeat(seat);
                        Orders.get(orderID).uncancelOrder();
                    }
                    break;
                }
                case "cancel-order": {
                    String orderID = obj.getString("order-id");
                    if (Orders.containsKey(orderID)) {
                        Orders.get(orderID).cancelOrder();
                    }
                    break;
                }
            }
        }

        setup(Movies, Orders);
    }

    public void setup(Map<String, String> MoviesInput, Map<String, OrderData> OrdersInput) {
        Iterator<String> iter = OrdersInput.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();

            if(!MoviesInput.containsKey(OrdersInput.get(key).getmovieName())) {
                iter.remove();
            }
        }

        Map<String, String> Orders = new HashMap<>();
        Map<Integer, String> Seats = new HashMap<>();
        Integer i = 1;
        for (String key : OrdersInput.keySet()) {
            OrderData orderdata = OrdersInput.get(key);
            String data = boolToString(orderdata.getIsCanceled()) + Separator + boolToString(orderdata.isModified())
                    + Separator + i.toString() + Separator + String.valueOf(orderdata.getSeats().size());
            Orders.put(key, data);
            for (String seat : orderdata.getSeats()) {
                Seats.put(i, seat);
                i++;
            }
        }

        Map<String, List<OrderData>> tempWatchers = new HashMap<>();
        Map<String, String> Watchers = new HashMap<>();
        Map<Integer, String> watchersOrders = new HashMap<>();
        for (String key : OrdersInput.keySet()) {
            OrderData orderdata = OrdersInput.get(key);
            String userID = orderdata.getUserID();
            String movieID = orderdata.getmovieName();
            List<String> seats = new ArrayList<>(orderdata.getSeats());
            Boolean isCanceled = orderdata.getIsCanceled();
            if (tempWatchers.containsKey(userID)) {
                tempWatchers.get(userID).add(new OrderData(orderdata));
            } else {
                tempWatchers.put(userID, new ArrayList<>(Arrays.asList(orderdata)));
            }

        }

        i = 1;
        for (String key : tempWatchers.keySet()) {
            String data = i.toString() + Separator + String.valueOf(tempWatchers.get(key).size());
            Watchers.put(key, data);
            for (OrderData orderdata : tempWatchers.get(key)) {
                String order_data = orderdata.getOrderID() + Separator + boolToString(orderdata.getIsCanceled())
                        + Separator + MoviesInput.get(orderdata.getmovieName()) + Separator + boolToString(orderdata.isModified())
                        + Separator + orderdata.getmovieName() + Separator + orderdata.getSeats().get(orderdata.getSeats().size() - 1);
                watchersOrders.put(i, order_data);
                i++;
            }
        }

        Map<String, List<OrderData>> tempMovies = new HashMap<>();
        Map<String, String> Movies = new HashMap<>();
        Map<Integer, String> moviesOrders = new HashMap<>();

        for (String key : OrdersInput.keySet()) {
            OrderData orderdata = OrdersInput.get(key);
            String userID = orderdata.getUserID();
            String movieID = orderdata.getmovieName();
            List<String> seats = new ArrayList<>(orderdata.getSeats());
            Boolean isCanceled = orderdata.getIsCanceled();
            if (tempMovies.containsKey(movieID)) {
                tempMovies.get(movieID).add(new OrderData(orderdata));
            } else {
                tempMovies.put(movieID, new ArrayList<>(Arrays.asList(orderdata)));
            }
        }

        i = 1;
        for (String key : tempMovies.keySet()) {
            String data = i.toString() + Separator + String.valueOf(tempMovies.get(key).size());
            Movies.put(key, data);
            for (OrderData orderdata : tempMovies.get(key)) {
                String order_data = orderdata.getUserID() + Separator + orderdata.getOrderID() + Separator +
                        boolToString(orderdata.getIsCanceled()) + Separator + orderdata.getSeats().get(orderdata.getSeats().size() - 1);
                moviesOrders.put(i, order_data);
                i++;
            }
        }

        for (String key: MoviesInput.keySet()) {
            if(!Movies.containsKey(key))
                Movies.put(key, "0" + Separator + "0");
        }

        CompletableFuture<FutureSecureDatabase> OrdersDB = sf.open("Orders");
        CompletableFuture<FutureSecureDatabase> WatchersDB = sf.open("Watchers");
        CompletableFuture<FutureSecureDatabase> MoviesDB = sf.open("Movies");
        CompletableFuture<FutureSecureDatabase> SeatsDB = sf.open("Seats");
        CompletableFuture<FutureSecureDatabase> MoviesOrderDB = sf.open("MoviesOrders");
        CompletableFuture<FutureSecureDatabase> WatchersOrdersDB = sf.open("WatchersOrders");

        for (String key : Orders.keySet()) {
            FutureSecureDatabase currDB = OrdersDB.join();

            CompletableFuture<?> completableFuture
                    = CompletableFuture.supplyAsync(() -> {
                        try {
                            return currDB.addEntry(key.getBytes(), Orders.get(key).getBytes());
                        } catch (DataFormatException e) {
                            e.printStackTrace();
                            return CompletableFuture.completedFuture(null);
                        }
                    }
            );

            completableFuture.join();
        }

        for (String key : Watchers.keySet()) {
            FutureSecureDatabase currDB =  WatchersDB.join();

            CompletableFuture<?> completableFuture
                    = CompletableFuture.supplyAsync(() -> {
                        try {
                            return currDB.addEntry(key.getBytes(), Watchers.get(key).getBytes());
                        } catch (DataFormatException e) {
                            e.printStackTrace();
                            return CompletableFuture.completedFuture(null);
                        }
                    }
            );

            completableFuture.join();
        }

        for (String key : Movies.keySet()) {
            FutureSecureDatabase currDB = MoviesDB.join();

            CompletableFuture<?> completableFuture
                    = CompletableFuture.supplyAsync(() -> {
                        try {
                            return currDB.addEntry(key.getBytes(), Movies.get(key).getBytes());
                        } catch (DataFormatException e) {
                            e.printStackTrace();
                            return CompletableFuture.completedFuture(null);
                        }
                    }
            );

            completableFuture.join();
        }

        for (Integer key : Seats.keySet()) {
            FutureSecureDatabase currDB = SeatsDB.join();

            CompletableFuture<?> completableFuture
                    = CompletableFuture.supplyAsync(() -> {
                        try {
                            return currDB.addEntry(key.toString().getBytes(), Seats.get(key).getBytes());
                        } catch (DataFormatException e) {
                            e.printStackTrace();
                            return CompletableFuture.completedFuture(null);
                        }
                    }
            );

            completableFuture.join();
        }

        for (Integer key : moviesOrders.keySet()) {
            FutureSecureDatabase currDB = MoviesOrderDB.join();

            CompletableFuture<?> completableFuture
                    = CompletableFuture.supplyAsync(() -> {
                        try {
                            return currDB.addEntry(key.toString().getBytes(), moviesOrders.get(key).getBytes());
                        } catch (DataFormatException e) {
                            e.printStackTrace();
                            return CompletableFuture.completedFuture(null);
                        }
                    }
            );

            completableFuture.join();
        }

        for (Integer key : watchersOrders.keySet()) {
            FutureSecureDatabase currDB = WatchersOrdersDB.join();

            CompletableFuture<?> completableFuture
                    = CompletableFuture.supplyAsync(() -> {
                        try {
                            return currDB.addEntry(key.toString().getBytes(), watchersOrders.get(key).getBytes());
                        } catch (DataFormatException e) {
                            e.printStackTrace();
                            return CompletableFuture.completedFuture(null);
                        }
                    }
            );

            completableFuture.join();
        }
    }

    private String boolToString(Boolean b) {
        int val = b ? 1 : 0;
        return String.valueOf(val);
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    public CompletableFuture<Boolean> isValidOrderId(String orderId) {
        FutureSecureDatabase OrdersDB = sf.open("Orders").join();
        CompletableFuture<Boolean> res = CompletableFuture.supplyAsync(() -> {
           // System.out.println(orderId);
            OrdersDB.get((orderId.getBytes()));
            return Boolean.valueOf(true);
        }).exceptionally(e-> {
            return Boolean.valueOf(false);
        });

        res.join();
        return res;
    }

    public CompletableFuture<Boolean> isModifiedOrder(String orderId) {
        FutureSecureDatabase OrdersDB = sf.open("Orders").join();

        CompletableFuture<Boolean> res =

        CompletableFuture.supplyAsync(() -> {
            String data = new String(OrdersDB.get((orderId.getBytes())).join(), StandardCharsets.UTF_8);
            if (data.split(Separator)[1].equals("1"))
                return Boolean.valueOf(true);
            else
                return Boolean.valueOf(false);
        }).exceptionally(e-> {
            return Boolean.valueOf(false);
        });
        res.join();
        return res;
    }

    public CompletableFuture<Boolean> isCanceledOrder(String orderId) {
        FutureSecureDatabase OrdersDB = sf.open("Orders").join();
        CompletableFuture<Boolean> res = CompletableFuture.supplyAsync(() -> {
            String data = new String(OrdersDB.get((orderId.getBytes())).join(), StandardCharsets.UTF_8);
            if (data.split(Separator)[0].equals("1"))
                return Boolean.valueOf(true);
            else
                return Boolean.valueOf(false);
        }).exceptionally(e-> {
            return Boolean.valueOf(false);
        });
        res.join();
        return res;
    }

    public CompletableFuture<OptionalInt> getSeatOrdered(String orderId) {
        FutureSecureDatabase OrdersDB = sf.open("Orders").join();
        FutureSecureDatabase SeatsDB = sf.open("Seats").join();
        CompletableFuture<OptionalInt> res = CompletableFuture.supplyAsync(() -> {
            String data = new String(OrdersDB.get((orderId.getBytes())).join(), StandardCharsets.UTF_8);
            Integer start_index = Integer.valueOf(data.split(Separator)[2]);
            Integer size = Integer.valueOf(data.split(Separator)[3]);
            Integer index = start_index + size - 1;
            int lastSeat = Integer.parseInt(new String(SeatsDB.get(index.toString().getBytes()).join(), StandardCharsets.UTF_8));
            if (data.split(Separator)[0].equals("1"))
                return OptionalInt.of(-lastSeat);
            else
                return OptionalInt.of(lastSeat);
        }).exceptionally(e-> {
            return OptionalInt.empty();
        });
        res.join();
        return res;
    }

    public CompletableFuture<List<String>> getUsersThatWatched(String movieId) {
        FutureSecureDatabase MoviesDB = sf.open("Movies").join();
        FutureSecureDatabase MoviesOrdersDB = sf.open("MoviesOrders").join();
        CompletableFuture result = CompletableFuture.supplyAsync(() -> {
            String data = new String(MoviesDB.get((movieId.getBytes())).join(), StandardCharsets.UTF_8);
            Integer start_index = Integer.valueOf(data.split(Separator)[0]);
            Integer last_index = Integer.valueOf(data.split(Separator)[1]) + start_index - 1;
            List<String> res = new ArrayList<>();
            for (Integer i=start_index; i<=last_index; i++) {
                String order_data = new String(MoviesOrdersDB.get(i.toString().getBytes()).join(), StandardCharsets.UTF_8);
                if (order_data.split(Separator)[2].equals("0"))
                    res.add(order_data.split(Separator)[0]);
            }

            Set<String> resSet = new HashSet<>(res);
            res = new ArrayList<>(resSet);
            Collections.sort(res);
            return res;
        }).exceptionally(e-> {
            return new ArrayList<>();
        });

        result.join();
        return result;
    }

    public CompletableFuture<OptionalDouble> getModifyRatioForUser(String userId) {
        FutureSecureDatabase WatchersDB = sf.open("Watchers").join();
        FutureSecureDatabase WatchersOrdersDB = sf.open("WatchersOrders").join();
        CompletableFuture<OptionalDouble> result = CompletableFuture.supplyAsync(() -> {
            String data = new String(WatchersDB.get((userId.getBytes())).join(), StandardCharsets.UTF_8);
            Integer start_index = Integer.valueOf(data.split(Separator)[0]);
            Integer size = Integer.valueOf(data.split(Separator)[1]);
            Integer last_index = size + start_index - 1;
            int countModified = 0;
            for (Integer i=start_index; i<=last_index; i++) {
                String order_data = new String(WatchersOrdersDB.get(i.toString().getBytes()).join(), StandardCharsets.UTF_8);
                if (order_data.split(Separator)[3].equals("1"))
                    countModified++;
            }

            if(size.equals(Integer.valueOf(0)))
                return OptionalDouble.of(0);

            double res = ((double)countModified)/((double)Integer.parseInt(size.toString()));
            return OptionalDouble.of(res);
        }).exceptionally(e-> {
            return OptionalDouble.empty();
        });
        result.join();
        return result;
    }

    public CompletableFuture<Map<String, List<Long>>> getTakenSeats(String movieId) {
        FutureSecureDatabase MoviesDB = sf.open("Movies").join();
        FutureSecureDatabase MoviesOrdersDB = sf.open("MoviesOrders").join();
        CompletableFuture<Map<String, List<Long>>> result = CompletableFuture.supplyAsync(() -> {
            String data = new String(MoviesDB.get((movieId.getBytes())).join(), StandardCharsets.UTF_8);
            Integer start_index = Integer.valueOf(data.split(Separator)[0]);
            Integer last_index = Integer.valueOf(data.split(Separator)[1]) + start_index - 1;
            Map<String, List<Long>> res = new HashMap<>();
            for (Integer i=start_index; i<=last_index; i++) {
                String order_data = new String(MoviesOrdersDB.get(i.toString().getBytes()).join(), StandardCharsets.UTF_8);
                String userID = order_data.split(Separator)[0];
                Long seat = Long.valueOf(order_data.split(Separator)[3]).longValue();
                if (order_data.split(Separator)[2].equals("0")) {
                    if (res.containsKey(userID)) {
                        if (!res.get(userID).contains(seat)) {
                            res.get(userID).add(seat);
                        }
                    } else {
                        res.put(userID, new ArrayList<>(Arrays.asList(seat)));
                    }
                }

            }

            for(String key: res.keySet())
                Collections.sort(res.get(key));

            return res;
        }).exceptionally(e-> {
            return new HashMap<>();
        });
        result.join();
        return result;
    }

    public CompletableFuture<List<Integer>> getHistoryOfOrder(String orderId) {
        FutureSecureDatabase Orders = sf.open("Orders").join();
        FutureSecureDatabase Seats = sf.open("Seats").join();

        CompletableFuture<List<Integer>> result = CompletableFuture.supplyAsync(() -> {
            List<Integer> res = new ArrayList<>();
            String s = new String(Orders.get(orderId.getBytes()).join(), StandardCharsets.UTF_8);
            String isCanceled = s.split(Separator)[0];
            Integer startIndex = Integer.parseInt(s.split(Separator)[2]);
            Integer numOfSeats = Integer.parseInt(s.split(Separator)[3]);

            for (Integer i = startIndex; i < startIndex + numOfSeats; i++) {
                s = new String(Seats.get(i.toString().getBytes()).join(), StandardCharsets.UTF_8);
                res.add(Integer.parseInt(s));
            }

            if(isCanceled.equals("1")) {
                res.add(Integer.valueOf(-1));
            }

            return res;
        }).exceptionally(e-> {
            return new ArrayList<>();
        });
        result.join();
        return result;
    }

    public CompletableFuture<List<String>> getOrderIdsForUser(String userId) {
        FutureSecureDatabase Watchers = sf.open("Watchers").join();
        FutureSecureDatabase WatchersOrders = sf.open("WatchersOrders").join();

        CompletableFuture<List<String>> result = CompletableFuture.supplyAsync(() -> {
            List<String> res = new ArrayList<>();

            String s = new String(Watchers.get(userId.getBytes()).join(), StandardCharsets.UTF_8);
            Integer startIndex = Integer.parseInt(s.split(Separator)[0]);
            Integer numOfOrders = Integer.parseInt(s.split(Separator)[1]);

            for (Integer i = startIndex; i < startIndex + numOfOrders; i++) {
                s = new String(WatchersOrders.get(i.toString().getBytes()).join(), StandardCharsets.UTF_8);
                String OrderId = s.split(Separator)[0];

                res.add(OrderId);
            }

            return res.stream().sorted().collect(Collectors.toList());
        }).exceptionally(e-> {
            return new ArrayList<>();
        });

        result.join();
        return result;
    }

    public CompletableFuture<Long> getTotalTimeSpentByUser(String userId) {
        FutureSecureDatabase Watchers = sf.open("Watchers").join();
        FutureSecureDatabase WatchersOrders = sf.open("WatchersOrders").join();

        CompletableFuture<Long> result = CompletableFuture.supplyAsync(() -> {
            Long res = Long.valueOf(0);
            Map<String, Long> moviesLengths = new HashMap<>();

            String s = new String(Watchers.get(userId.getBytes()).join(), StandardCharsets.UTF_8);
            Integer startIndex = Integer.parseInt(s.split(Separator)[0]);
            Integer numOfOrders = Integer.parseInt(s.split(Separator)[1]);

            for (Integer i = startIndex; i < startIndex + numOfOrders; i++) {
                s = new String(WatchersOrders.get(i.toString().getBytes()).join(), StandardCharsets.UTF_8);
                String Length = s.split(Separator)[2];
                String movieId = s.split(Separator)[4];
                String isCanceled = s.split(Separator)[1];

                if(isCanceled.equals("0"))
                    moviesLengths.put(movieId, Long.parseLong(Length));
            }

            for(String movieId : moviesLengths.keySet())
                res += moviesLengths.get(movieId);
            return res;
        }).exceptionally(e -> Long.valueOf(0));

        result.join();
        return result;
    }

    public CompletableFuture<List<String>> getOrderIdsThatPurchased(String movieId) {
        FutureSecureDatabase Movies = sf.open("Movies").join();
        FutureSecureDatabase MoviesOrders = sf.open("MoviesOrders").join();

        CompletableFuture<List<String>> result = CompletableFuture.supplyAsync(() -> {
            List<String> res = new ArrayList<>();

            String s = new String(Movies.get(movieId.getBytes()).join(), StandardCharsets.UTF_8);
            Integer startIndex = Integer.parseInt(s.split(Separator)[0]);
            Integer numOfOrders = Integer.parseInt(s.split(Separator)[1]);

            for (Integer i = startIndex; i < startIndex + numOfOrders; i++) {
                s = new String(MoviesOrders.get(i.toString().getBytes()).join(), StandardCharsets.UTF_8);
                String OrderId = s.split(Separator)[1];

                res.add(OrderId);
            }

            return res.stream().sorted().collect(Collectors.toList());
        }).exceptionally(e -> new ArrayList<>());
        result.join();
        return result;
    }

    public CompletableFuture<OptionalLong> getTotalNumberOfTicketsPurchased(String movieId) {
        FutureSecureDatabase Movies = sf.open("Movies").join();
        FutureSecureDatabase MoviesOrders = sf.open("MoviesOrders").join();

        CompletableFuture<OptionalLong> result = CompletableFuture.supplyAsync(() -> {
            OptionalLong res = OptionalLong.of(0);

            String s = new String(Movies.get(movieId.getBytes()).join(), StandardCharsets.UTF_8);
            Integer startIndex = Integer.parseInt(s.split(Separator)[0]);
            Integer numOfOrders = Integer.parseInt(s.split(Separator)[1]);

            for (Integer i = startIndex; i < startIndex + numOfOrders; i++) {
                s = new String(MoviesOrders.get(i.toString().getBytes()).join(), StandardCharsets.UTF_8);
                String isCanceled = s.split(Separator)[2];
                if(isCanceled.equals("0"))
                    res = OptionalLong.of(res.getAsLong()+1);
            }
            return res;
        }).exceptionally(e -> OptionalLong.empty());
        result.join();
        return result;
    }

    public CompletableFuture<OptionalDouble> getCancelRatioForMovie(String movieId) {
        FutureSecureDatabase Movies = sf.open("Movies").join();
        FutureSecureDatabase MoviesOrders = sf.open("MoviesOrders").join();

        CompletableFuture<OptionalDouble> result = CompletableFuture.supplyAsync(() -> {
            String s = new String(Movies.get(movieId.getBytes()).join(), StandardCharsets.UTF_8);
            Integer startIndex = Integer.parseInt(s.split(Separator)[0]);
            Integer numOfOrders = Integer.parseInt(s.split(Separator)[1]);
            Integer numOfCanceledOrders = Integer.valueOf(0);

            for (Integer i = startIndex; i < startIndex + numOfOrders; i++) {
                s = new String(MoviesOrders.get(i.toString().getBytes()).join(), StandardCharsets.UTF_8);
                String isCanceled = s.split(Separator)[2];
                if(isCanceled.equals("1"))
                    numOfCanceledOrders++;
            }

            if(numOfOrders.equals(Integer.valueOf(0)))
                return OptionalDouble.of(0);

            return OptionalDouble.of(numOfCanceledOrders.doubleValue()/numOfOrders.doubleValue());
        }).exceptionally(e -> OptionalDouble.empty());
        result.join();
        return result;
    }

    public CompletableFuture<Map<String, List<Long>>> getAllMoviesSeen(String userId) {
        FutureSecureDatabase Watchers = sf.open("Watchers").join();
        FutureSecureDatabase WatchersOrders = sf.open("WatchersOrders").join();

        CompletableFuture<Map<String, List<Long>>> result = CompletableFuture.supplyAsync(() -> {
            Map<String, List<Long>> res = new HashMap<>();

            String s = new String(Watchers.get(userId.getBytes()).join(), StandardCharsets.UTF_8);
            Integer startIndex = Integer.parseInt(s.split(Separator)[0]);
            Integer numOfOrders = Integer.parseInt(s.split(Separator)[1]);

            for (Integer i = startIndex; i < startIndex + numOfOrders; i++) {
                s = new String(WatchersOrders.get(i.toString().getBytes()).join(), StandardCharsets.UTF_8);
                String isCanceled = s.split(Separator)[1];
                String movieId = s.split(Separator)[4];
                Long lastSeat = Long.parseLong(s.split(Separator)[5]);
                if(isCanceled.equals("0")) {
                    if (res.containsKey(movieId)) {
                        if (!res.get(movieId).contains(lastSeat)) {
                            res.get(movieId).add(lastSeat);
                        }
                    } else {
                        res.put(movieId, new ArrayList<>(Arrays.asList(lastSeat)));
                    }
                }
            }

            for (String key : res.keySet()) {
                Collections.sort(res.get(key));
            }
            return res;
        }).exceptionally(e -> new HashMap<>());
        result.join();
        return result;
    }

    public void addEntry(byte[] key, byte[] value) {
       // SecureDatabase.addEntry(key, value);
    }
    public byte[] get(byte[] key) throws InterruptedException {
       // return SecureDatabase.get(key);
        return null;
    }
}
