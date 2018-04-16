package util;

import util.iterator.InputStreamLineIterator;

import java.io.InputStream;

public abstract class AbstractRequest implements IRequest {

    @Override
    public Iterable<String> getBody(String path) {
        return () -> new InputStreamLineIterator(() -> openStream(path));
    }

    abstract InputStream openStream(String path);
}
