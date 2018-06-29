package com.mygdx.notecollector.Multiplayer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.notecollector.screens.menu.Multiplayer.MultiplayerWaitingScreen;

import java.io.IOException;

public class ServerClass
{
    static int tcpPort=54555;
    static int udpPort=54777;
    static Server server;
    static boolean connected;
    static Connection c;

    public ServerClass()
    {
        //server = new Server();//Init Kryo server
        server = new Server(1500000,150000);//Init Kryo server
        server.start();
        connected=false;
        try
        {
            server.bind(tcpPort, udpPort);//Bind TCP and UDP ports
            Kryo kryo=server.getKryo();
            kryo.register(SomeRequest.class);
            kryo.register(SomeResponse.class);
            kryo.register(GameParamObject.class);
            kryo.register(fileLoaded.class);
            kryo.register(scoreObj.class);
            kryo.register(gameOver.class);
            kryo.register(byte[].class);
            System.setProperty("java.net.preferIPv4Stack" , "true");
            server.addListener(new Listener()
            {
                public void received (Connection connection, Object object) {
                    if (object instanceof SomeRequest) {
                        c=connection;
                        SomeRequest request = (SomeRequest)object;
                        System.out.println(request.text);
                        connected=true;
                        connection.setKeepAliveTCP(8000);
                        SomeResponse response = new SomeResponse();
                        response.text = "Thanks";
                        connection.sendTCP(response);

                    }
                }
            });

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public Server getServer()
    {
        return server;
    }

    public void sendGameObj(final byte[] file, final String difficulty,final boolean multiTrack,final boolean mode)
    {
        /*GameParamObject response = new GameParamObject();
        response.file=file;
        response.speed=speed;
        response.delay=delay;
        response.multiTrack=multiTrack;
        response.mode=mode;
        System.out.println(speed+" "+delay);
        c.sendTCP(response);*/
        GameParamObject response = new GameParamObject();
        response.file=file;
        response.difficulty=difficulty;
        response.multiTrack=multiTrack;
        response.mode=mode;
        c.sendTCP(response);
    }

    public void sendLoadedMsg()
    {
        fileLoaded request = new fileLoaded();
        request.loaded = "Loaded";
        //System.out.println("Host Sending");
        //c.setKeepAliveTCP(60000);
        c.sendTCP(request);
    }

    public void sendScore(String score)
    {
        scoreObj msg=new scoreObj();
        msg.score=score;
        ///c.setKeepAliveTCP(60000);
        c.sendTCP(msg);
    }




    public Connection getConnection()
    {
        return c;
    }

    public void connected(Connection c)
    {
        System.out.println("Connection received from "+c.getRemoteAddressTCP().getHostName());
        SomeResponse res=new SomeResponse();
        res.text="TEST";
        c.sendTCP(res);
    }

    public boolean getConnectionStatus()
    {
        return connected;
    }

    public void sendGameOver(String score)//Used to notify the client that the host has lost
    {
        gameOver msg=new gameOver();
        msg.score=score;
        System.out.println("Sending game over");
        c.sendTCP(msg);
    }

/*
Static classes for communication. All classes must contain zero argument constructors for proper serialization
(according to KryoNet docs)
*/

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
            this.text = "";
        }
    }

    public static  class fileLoaded
    {
        public String loaded;
        public fileLoaded()
        {}

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
