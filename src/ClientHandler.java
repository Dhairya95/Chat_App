
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler  implements Runnable{

    public ArrayList<ClientHandler> clientHandlers;
    public Server server;
    public Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String name;

    public ClientHandler(Socket socket,ArrayList<ClientHandler> clientHandlers)
    {
        try
        {
            this.clientHandlers = clientHandlers;
            this.socket = socket;
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.name = dataInputStream.readUTF();
            broadcastMessage("SERVER : " + name + " has entered in the room");

        }
        catch(IOException e)
        {
            closeAll(socket, dataOutputStream, dataInputStream);
        }
    }

    public String getUserName()
    {
        return this.name;
    }

    @Override
    public void run()
    {

        String messageFromClient;

        while(socket.isConnected())
        {
            try
            {
                messageFromClient = dataInputStream.readUTF();
                broadcastMessage(messageFromClient);
            }
            catch(IOException e)
            {
                closeAll(socket, dataOutputStream, dataInputStream);
                break;
            }
        }
    }
    public void broadcastMessage(String messageToSend)
    {
        if(messageToSend.equals("stop"))
        {
            removeClientHandler();
        }
        else {
            for (ClientHandler clientHandler : clientHandlers) {
                try {
                    if (!clientHandler.name.equals(name)) {
                        clientHandler.dataOutputStream.writeUTF(messageToSend);
                        clientHandler.dataOutputStream.flush();
                    }
                } catch (IOException e) {
                    closeAll(socket, dataOutputStream, dataInputStream);

                }
            }
        }
    }

    public void removeClientHandler()
    {
        server.clientHandlers.remove(this);
        broadcastMessage("server " + name + " left");
    }

    public void closeAll(Socket socket, DataOutputStream dataOutputStream, DataInputStream dataInputStream)
    {
        try{
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
        catch (IOException e)
        {
            e.getStackTrace();
        }
    }

}