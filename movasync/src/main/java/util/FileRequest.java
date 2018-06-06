package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public class FileRequest extends AbstractRequest {

    @Override
    public InputStream openStream(String uri) {
        String path = uri
                .replace('?', '-')
                .replace('&', '-')
                .replace('=', '-')
                .replace(',', '-')
                .replace(':', '-')
                .replace('/', '-')
                .concat(".json");
        try {
            return ClassLoader.getSystemResource(path).openStream();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}