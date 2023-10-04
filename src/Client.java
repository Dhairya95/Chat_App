import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String name;

    public Client(Socket socket, String name)
    {
        try
        {
            this.socket = socket;
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.name = name;
        }
        catch (Exception e)
        {
            closeAll(socket, dataOutputStream, dataInputStream);
        }
    }

    public void sendMessage()
    {
        try{
            dataOutputStream.writeUTF(name);
            dataOutputStream.flush();
            Scanner sc = new Scanner(System.in);

            while(socket.isConnected())
            {
                String messageToSend = sc.nextLine();
                dataOutputStream.writeUTF(name + ": " + messageToSend);
                dataOutputStream.flush();
            }
        }
        catch(IOException e)
        {
            closeAll(socket, dataOutputStream, dataInputStream);

        }
    }


    @Override
    public void run()
    {
        String messageFromChat;

        while(socket.isConnected())
        {
            try
            {
                messageFromChat =  dataInputStream.readUTF();
                System.out.println(messageFromChat);
            }
            catch (IOException e)
            {
                closeAll(socket, dataOutputStream, dataInputStream);
            }

        }

    }

    public void readMessage(Client client)
    {
        Runnable runnable = client;
        Thread thread = new Thread(runnable);
        thread.start();
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

    // main method
    public static void main(String[] args) throws IOException
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your name");
        String name = sc.nextLine();
        Socket socket = new Socket("localhost", 9999);
        Client client = new Client(socket, name);
        client.readMessage(client);
        client.sendMessage();
    }

}