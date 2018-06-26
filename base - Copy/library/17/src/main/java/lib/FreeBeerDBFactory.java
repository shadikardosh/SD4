package lib;

import java.util.concurrent.CompletableFuture;

public interface FreeBeerDBFactory {
    CompletableFuture<FreeBeerDB> open(String s);
}
