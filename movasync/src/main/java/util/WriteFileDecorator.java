package util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;


public class WriteFileDecorator implements IRequest {
    private IRequest request;

    public WriteFileDecorator(IRequest request) {
        this.request = request;
    }

    @Override
    public CompletableFuture<String> getBody(String path) {
        CompletableFuture<String> body = request.getBody(path);
        return body
                .whenComplete((result, throwable) -> {
                    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(createPath(path)))) {
                        writer.write(result);
                        System.out.println("FILE WRITTEN --> " + path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
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
        return "C:\\Users\\nunob\\Documents\\Projetos\\LI41D-G14\\movasync\\src\\test\\resources\\" + res;
    }
}
