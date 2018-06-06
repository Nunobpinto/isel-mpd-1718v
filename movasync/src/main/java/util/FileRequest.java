package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;

public class FileRequest implements IRequest {

//    @Override
//    public InputStream openStream(String uri) {
//        String path = uri
//                .replace('?', '-')
//                .replace('&', '-')
//                .replace('=', '-')
//                .replace(',', '-')
//                .replace(':', '-')
//                .replace('/', '-')
//                .concat(".json");
//        try {
//            return ClassLoader.getSystemResource(path).openStream();
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }
//    }

    @Override
    public CompletableFuture<String> getBody(String path) {
        return null;
    }
}