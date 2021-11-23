import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.*;
import io.pravega.client.stream.impl.JavaSerializer;
import io.pravega.client.stream.impl.UTF8StringSerializer;

import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;


public class Writer {
    /**
     * @brief writeData method will write string message to a particular stream.
     *
     * @param writer: A EventStreamWriter manages where to push your String to.
     * @param message: A String you want to push to stream
     * */
    public static void writeData(EventStreamWriter<String> writer,String message) throws Exception {
        CompletableFuture<Void> future = writer.writeEvent(message);
        future.get();
    }
    /**
     * @brief getWriter method will create a configured string event stream writer,
     *        which manages writing data to a particular stream.
     *
     * @param url: A String defines the connection URL
     * @param scope: A String defines the name of a scope
     * @param stream: A String defines the name of stream
     * @return EventStreamWriter: A string event writer manages data writing.
     * */
    public static EventStreamWriter<String> getWriter(String url,String scope,String stream)throws Exception{
        URI uri=URI.create(url);
        ClientConfig build = ClientConfig.builder().controllerURI(uri).build();
        EventStreamClientFactory eventStreamClientFactory = EventStreamClientFactory.withScope(scope,build);
        EventWriterConfig writeConfig = EventWriterConfig.builder().build();
        return eventStreamClientFactory.createEventWriter(stream
                ,new UTF8StringSerializer()
                ,writeConfig);
    }
    /**
     * @brief createStream method will create a new stream in a particular scope
     *
     * @param url: A String defines the connection URL
     * @param scope: A String defines the name of a scope
     * @param stream: A String defines the name of stream
     * */
    public static void createStream(String url,String scope,String stream) throws Exception{
        URI uri=URI.create(url);
        StreamManager streamManager = StreamManager.create(uri);
        StreamConfiguration streamConfiguration = StreamConfiguration.builder().scalingPolicy(ScalingPolicy.fixed(1)).build();
        streamManager.createScope(scope);
        streamManager.createStream(scope,stream,streamConfiguration);
        //streamManager.checkStreamExists(scope,stream);
    }
}

