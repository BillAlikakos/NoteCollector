package com.mygdx.notecollector.Multiplayer;

import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.notecollector.screens.menu.LoadingScreen;
import com.mygdx.notecollector.screens.menu.Multiplayer.MultiplayerWaitingScreen;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ClientClass extends Listener
{
    static Client client;//Client obj

    static int tcpPort=54555;
    static int udpPort=54777;
    static boolean received=false;

    public ClientClass()
    {
        System.out.println("Connecting to Server");
        //client=new Client();
        client=new Client(1500000,1500000);
        client.getKryo().register(SomeRequest.class);//Register objects
        client.getKryo().register(SomeResponse.class);
        client.getKryo().register(GameParamObject.class);
        client.getKryo().register(fileLoaded.class);
        client.getKryo().register(scoreObj.class);
        client.getKryo().register(gameOver.class);
        client.getKryo().register(byte[].class);
        System.setProperty("java.net.preferIPv4Stack" , "true");
        new Thread(client).start();
        //client.start();



    }
    public void connect(InetAddress address)
    {
        try
        {
            System.out.println("Connecting to server");
            //client.connect(5000,ip,tcpPort,udpPort);
            client.connect(60000,address,tcpPort,udpPort);
            SomeRequest request = new SomeRequest();
            request.text = "Here is the request";
            client.sendTCP(request);
            System.out.println("The client is waiting for a packet");
            client.addListener(new Listener() {
                public void received (Connection connection, Object object) {
                    connection.setKeepAliveTCP(8000);
                    connection.setTimeout(0);
                   // client.setKeepAliveUDP(1000);
                    if (object instanceof SomeResponse) {
                        SomeResponse response = (SomeResponse)object;
                        System.out.println("Received from host : "+response.text);
                    }
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }



    public Client getClient()
    {
        return client;
    }

    public boolean checkConn()
    {
        return client.isConnected();
    }


    public ArrayList<InetAddress> discoverServers()
    {
        ArrayList<InetAddress> addresses=new ArrayList<>();
        //addresses= (ArrayList<InetAddress>) client.discoverHosts(udpPort,2000);
        addresses= (ArrayList<InetAddress>) client.discoverHosts(udpPort,500);
        return  addresses;
    }

    public void sendLoadedMsg()//Notifies server that the client has loaded the game files
    {
        fileLoaded request = new fileLoaded();
        request.loaded = "Loaded";
        client.sendTCP(request);

    }

    public void sendScore(String score)//Used to send the client's score over the course of the game
    {
        scoreObj msg=new scoreObj();
        msg.score=score;
      //  System.out.println("Sending score: "+score);
        client.sendTCP(msg);
    }

    public void sendGameOver(String score)//Used to notify the server that the client has lost
    {
        gameOver msg=new gameOver();
        msg.score=score;
        System.out.println("Sending game over: "+score);
        client.sendTCP(msg);
    }

    /*
Static classes for communication. All classes must contain zero argument constructors for proper serialization
(according to KryoNet docs)
*/
    public static  class fileLoaded
    {
        public String loaded;
        public fileLoaded()
        {}

    }

    public static class SomeRequest
    {
        public String text;

        public SomeRequest()
        {
            this.text = "";
        }

    }

    public static class SomeResponse
    {
        public String text;

        public SomeResponse()
        {
            this.text = " ";
        }
    }

    public static class GameParamObject
    {
        /*public byte[] file;
        public int speed;
        public long delay;
        public boolean multiTrack;
        public boolean mode;*/
        public byte[] file;
        public String difficulty;
        public boolean multiTrack;
        public boolean mode;
        public GameParamObject()
        {
        }
    }

    public static class scoreObj
    {
        public String score;

        public scoreObj()
        {
        }
    }

    public static class gameOver
    {
        public String score;

        public gameOver()
        {
        }
    }
}
