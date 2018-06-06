package util.iterator;

import java.util.Iterator;
import java.util.function.Function;

public class FlatMapIterator<T,R> implements Iterator<R> {
    private final Function<T, Iterable<R>> mapper;
    private final Iterator<T> src;
    private Iterator<R> innerIt;
    private R curr;

    public FlatMapIterator(Function<T, Iterable<R>> mapper, Iterable<T> src) {
        this.src = src.iterator();
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        if( (innerIt == null || !innerIt.hasNext()) && src.hasNext())
            innerIt = mapper.apply(src.next()).iterator();
        return innerIt.hasNext();
    }

    @Override
    public R next() {
        if( innerIt == null )
            innerIt = mapper.apply(src.next()).iterator();
        return innerIt.next();
    }
}
