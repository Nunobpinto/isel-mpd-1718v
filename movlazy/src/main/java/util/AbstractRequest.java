package util;

import util.iterator.InputStreamLineIterator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class AbstractRequest implements IRequest {

    @Override
    public Supplier<Stream<String>> getBody(String path) {
        return () -> {
            InputStream data = openStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(data));
            return reader.lines();
        };
    }

    abstract InputStream openStream(String path);
}
