package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;

public class FileRequest implements IRequest {

    @Override
    public CompletableFuture<String> getBody(String path) {
        String uri = path
                .replace('?', '-')
                .replace('&', '-')
                .replace('=', '-')
                .replace(',', '-')
                .replace(':', '-')
                .replace('/', '-')
                .concat(".json");
        CompletableFuture<String> cf = new CompletableFuture<>();
        try {
            System.out.println(uri);
            URL url = ClassLoader.getSystemResource(uri);
            Path src = Paths.get(url.toURI());
            AsynchronousFileChannel fileChannel =
                    AsynchronousFileChannel.open(src, StandardOpenOption.READ);

            ByteBuffer buffer = ByteBuffer.allocate(20384);

            fileChannel.read(buffer, 0L, cf, new CompletionHandler<Integer, CompletableFuture<String>>() {
                @Override
                public void completed(Integer result, CompletableFuture<String> attachment) {
                    System.out.println("result = " + result);

                    buffer.flip();
                    byte[] data = new byte[buffer.limit()];
                    buffer.get(data);
                    String res = new String(data);
                    System.out.println(res);
                    buffer.clear();
                    attachment.complete(res);
                }

                @Override
                public void failed(Throwable exc, CompletableFuture<String> attachment) {
                    attachment.completeExceptionally(exc);
                }
            });

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return cf;
    }
}