package util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class WriteFileDecorator implements IRequest {
    private IRequest request;

    public WriteFileDecorator(IRequest request) {
        this.request = request;
    }

    @Override
    public Supplier<Stream<String>> getBody(String path) {
        Supplier<Stream<String>> body = request.getBody(path);
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(createPath(path)))) {
            writer.write(body.get().reduce("", (prev, curr) -> prev + curr));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }

    private String createPath(String path) {
        String res = path
                .replace('?', '-')
                .replace('&', '-')
                .replace('=', '-')
                .replace(',', '-')
                .replace(':', '-')
                .replace('/', '-')
                .concat(".json");
        return "D:\\Joao\\ISEL\\8semestre\\MPD\\mpd1718v\\movlazy\\src\\test\\resources\\" + res;
    }
}
