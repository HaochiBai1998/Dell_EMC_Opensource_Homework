import io.pravega.client.admin.ReaderGroupManager;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.EventStreamReader;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.ScalingPolicy;
import io.pravega.client.stream.StreamConfiguration;

import java.net.URI;
import java.util.Scanner;

public class GroupChat implements Runnable {
    protected static String SCOPE_NAME = "ChatGroupScope";
    protected static String URL = "tcp://127.0.0.1:9090";
    private Thread ReadThread;
    boolean WriteTerminated;
    protected String clientID;
    protected String chatRoomID;
    protected EventStreamReader<String> clientReader;
    protected EventStreamWriter<String> clientWriter;
    protected ReaderGroupManager clientReaderGroupManager;

    /**
     * @brief closeRead method will close all the created readers & reader groups,
     *        then close reader group manager.
     * */

    void closeRead(){
        clientReader.close();
        clientReaderGroupManager.deleteReaderGroup(clientID+"Group");
        clientReaderGroupManager.close();
    }

    /**
     * @brief closeWrite method will close the created writer.
     * */

    void closeWrite(){
        clientWriter.close();
    }
    /**
     * @brief connectStream method will check whether a scope/stream exist,
     *        if not exist, create a new stream for file transformation.
     *
     * @param url: A String defines the connection URL
     * @param scope: A String defines the name of a scope
     * @param stream: A String defines the name of a stream
     * */

    public static void connectStream(String url,String scope,String stream)throws Exception{
        URI uri=URI.create(url);
        StreamManager streamManager=StreamManager.create(uri);
        if(!streamManager.checkStreamExists(scope,stream)){
            StreamConfiguration streamConfiguration = StreamConfiguration.builder()
                    .scalingPolicy(ScalingPolicy.fixed(1))
                    .build();
            if(!streamManager.checkScopeExists(scope)) {
                streamManager.createScope(scope);
            }
            streamManager.createStream(scope,stream,streamConfiguration);
            return;
        }
        streamManager.getStreamInfo(scope,stream);
    }
    /**
     * @brief init method will initialize all the parameters of
     *        group chat, user need to input client name, chat
     *        room ID.
     * */
    public void init()throws Exception{
        WriteTerminated=false;
        Scanner stdin= new Scanner(System.in);
        System.out.println("Please input client name:");
        clientID = stdin.nextLine();
        System.out.println("Please input Chat Room ID:");
        String streamName = stdin.nextLine();
        chatRoomID=streamName;
        connectStream(URL,SCOPE_NAME,streamName);
        clientWriter = Writer.getWriter(URL,SCOPE_NAME,streamName);
        clientReaderGroupManager=Reader.createReaderGroup(SCOPE_NAME,streamName,URL,clientID+"Group");
        clientReader=Reader.createReader(URL,SCOPE_NAME,clientID,clientID+"Group");
        System.out.println("------------------chat room ID:"+ chatRoomID+"------------------");
    }
    /**
     * @brief GroupChat constructor will call init() method to initialize
     *        all the parameters of group chat.
     * */

    /*****************************************************************************/
    //methods for reading thread, a client of Group chat will generate a thread for
    //reading task. This thread will constantly listen to stream data update.

    /**
     * @brief run method will constantly read data from stream if reading thread is
     *        still alive.
     * */
    @Override
    public void run(){
        try {
            Reader.readData(clientReader);
        }
        catch(Exception e){
            closeWrite();
            closeRead();
        }
    }
    /**
     * @brief terminateReadThread method will terminate reading thread and throw
     *        an interrupt.
     * */
    public void terminateReadThread(){
        ReadThread.interrupt();
    }
    /**
     * @brief startReadThread method will initialize a reading thread and start it.
     * */
    public void startReadThread(){
        if(ReadThread==null){
            ReadThread =new Thread(this,clientID);
            ReadThread.start();
        }
    }
    /*****************************************************************************/

    /**
     * @brief upload method manages file upload.
     *        A client need to input receiver ID and file name before upload a file
     *        to stream, if uploaded successfully, this method will return corresponding
     *        chat message.
     * @return A string of chat message.
     * */

    public String upload()throws Exception{
        Scanner chatInput= new Scanner(System.in);
        System.out.println("Please select file transfer mode:");
        System.out.println("(1)One to one file transfer");
        System.out.println("(2)Group file transfer");
        String fileTransferMode=chatInput.nextLine();
        while(!(fileTransferMode.equals("1"))&&
              !(fileTransferMode.equals("2"))){
            System.out.println("Wrong choice! please choose again:");
            fileTransferMode=chatInput.nextLine();
        }
        String receiverID;
        if(fileTransferMode.equals("1")) {
            System.out.println("Please input receiver ID:");
            receiverID=chatInput.nextLine();
        }
        else receiverID = "";
        System.out.println("Please input file name:");
        String fileName=chatInput.nextLine();
        FileSystem fileSystem = new FileSystem(clientID, receiverID, fileName);
        fileSystem.writeFileToStream();
        if(fileTransferMode.equals("1"))
            return clientID +":Upload_"+fileName+"_to_"+receiverID;
        else
            return clientID +":Upload_"+fileName+"_to_group";
    }
    /**
     * @brief download method manages file download.
     *        A client need to input uploader ID and file name before download a file
     *        from stream. If stream exists, client can download file from that stream
     *        and return corresponding char message.
     * @return A string of chat message.
     * */
    public String download() throws Exception{
        Scanner chatInput= new Scanner(System.in);
        System.out.println("Please select file transfer mode:");
        System.out.println("(1)One to one file transfer");
        System.out.println("(2)Group file transfer");
        String fileTransferMode=chatInput.nextLine();
        while(!(fileTransferMode.equals("1"))&&
                !(fileTransferMode.equals("2"))){
            System.out.println("Wrong choice! please choose again:");
            fileTransferMode=chatInput.nextLine();
        }
        System.out.println("Please input uploader ID:");
        String uploaderID=chatInput.nextLine();
        System.out.println("Please input file name:");
        String fileName=chatInput.nextLine();
        System.out.println("Save "+fileName+" as:");
        String outputFileName=chatInput.nextLine();
        String receiverID;
        if(fileTransferMode.equals("1")){
            receiverID=clientID;
        }
        else receiverID="";
        FileSystem fileSystem=new FileSystem(uploaderID,receiverID,fileName);
        fileSystem.readFileFromStream(outputFileName);
        if(fileTransferMode.equals("1"))
            return clientID +":Download_"+fileName+"_from_"+uploaderID;
        else
            return clientID +":Download_"+fileName+"_from_group";
    }

    public GroupChat() throws Exception{
        init();
    }

    public static void main(String[] args) throws Exception{
        Scanner chatInput= new Scanner(System.in);
        //generate a GroupChat object
        GroupChat client=new GroupChat();

        client.startReadThread();
        while(true){
            String userInput=chatInput.nextLine();
            String chatMessage=userInput;
            //when user type Upload, upload data to stream
            if(userInput.equals("Upload")){
                chatMessage=client.upload();
            }
            //when user type Download, download data from stream
            else if(userInput.equals("Download")) {
                chatMessage=client.download();
            }
            //normal chat.
            else{
                chatMessage=client.clientID + ":" + userInput;
            }
            //push chat message to stream
            Writer.writeData(client.clientWriter,chatMessage);
            //when client type "Bye.", close client.
            if(chatMessage.equals(client.clientID+":Bye.")){
                client.closeWrite();
                client.terminateReadThread();
                client.closeRead();
                break;
            }
        }
    }
}
