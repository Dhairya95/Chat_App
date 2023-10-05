
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {
    public ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private ServerSocket serverSocket;
    private Socket socket;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    public void closeAll(Socket socket, DataOutputStream dataOutputStream, DataInputStream dataInputStream)
    {
        try
        {
            if(dataOutputStream!= null)
            {
                dataOutputStream.close();
            }
            if(dataInputStream != null)
            {
                dataInputStream.close();
            }
            if(socket != null)
            {
                socket.close();
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
    public void serverStart(){

        try{

            while(!serverSocket.isClosed())
            {
                this.socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket,clientHandlers);
                this.clientHandlers.add(clientHandler);
                System.out.println("User "+clientHandler.getUserName() + " Connected");
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e){

        }
    }

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(9999);
        Server server = new Server(serverSocket);
        server.serverStart();
    }

}