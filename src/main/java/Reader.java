import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.admin.ReaderGroupManager;
import io.pravega.client.stream.*;
import io.pravega.client.stream.impl.JavaSerializer;
import io.pravega.client.stream.impl.UTF8StringSerializer;
import org.bouncycastle.jce.ECKeyUtil;

import java.net.URI;

public class Reader {
    /**
     * @brief createReaderGroup method will create a reader group in a particular stream.
     *
     * @param scope: A String defines the name of a scope
     * @param stream: A String defines the name of stream
     * @param url: A String defines the connection URL
     * @param groupName: A String defines reader group name
     * @return ReaderGroupManager: A configured ReaderGroupManager
     * */
    public static ReaderGroupManager createReaderGroup(String scope,String stream, String url,String groupName)throws Exception{
        URI uri=URI.create(url);
        ReaderGroupManager readerGroupManager = ReaderGroupManager.withScope(scope,uri);
        ReaderGroupConfig readerGroupConfig = ReaderGroupConfig.builder()
                .stream(scope+"/"+stream)
                .build();
        readerGroupManager.createReaderGroup(groupName,readerGroupConfig);
        return readerGroupManager;
    }
    /**
     * @brief createReader method will create a reader in a particular reader group.
     *
     * @param url: A String defines the connection URL
     * @param scope: A String defines the name of a scope
     * @param readerID: A String defines the reader name
     * @param groupName: A String defines reader group name
     * @return EventStreamReader<String>: A configured EventStreamReader
     * */
    public static EventStreamReader<String> createReader(String url,String scope,String readerID,String groupName)throws Exception{
        URI uri=URI.create(url);
        ClientConfig clientConfig=ClientConfig.builder().controllerURI(uri).build();
        EventStreamClientFactory streamClientFactory=EventStreamClientFactory.withScope(scope,clientConfig);
        ReaderConfig readerConfig = ReaderConfig.builder().build();
        EventStreamReader<String> reader=streamClientFactory.createReader(readerID
                ,groupName
                ,new UTF8StringSerializer()
                ,readerConfig);
        return reader;
    }
    /**
     * @brief readData method will read all the events from a particular stream and print in system out.
     *
     * @param reader: A String event stream reader
     * */
    public static void readData(EventStreamReader<String> reader) throws Exception{
            while (true) {
                String event = reader.readNextEvent(1000).getEvent();
                if (event == null) {
                    //System.out.println("no data");
                    continue;
                }
                System.out.println(event);
            }
    }
//
//    public static void checkOffline(ReaderGroupManager readerGroupManager,String readerGroupName,String readerID){
//        ReaderGroup readerGroup = readerGroupManager.getReaderGroup(readerGroupName);
//        readerGroup.readerOffline(readerID, null);
//    }
}
