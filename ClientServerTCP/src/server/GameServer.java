package server;

import java.io.*;
import java.net.*;

public class GameServer {

    private ServerSocket ss;
    private int numPlayers;
    private int maxPlayers;
    private Socket p1Socket;
    private Socket p2Socket;
    private ReadFromClient p1ReadRunnable;
    private ReadFromClient p2ReadRunnable;
    private WriteToClient p1WriteRunnable;
    private WriteToClient p2WriteRunnable;
    private double p1X, p1Y, p2X, p2Y;


    public GameServer() {
        System.out.println("======GAME SERVER======");
        numPlayers = 0;
        maxPlayers = 2;

        p1X = 100;
        p1Y = 400;
        p2X = 490;
        p2Y = 400;

        try {
            ss = new ServerSocket(5090);
        }
        catch (IOException e) {
            System.out.println("IO Exception from GameServer constructor");
        }
    }

    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections...");

            while (numPlayers < maxPlayers) {
                Socket s = ss.accept();
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                numPlayers++;
                out.writeInt(numPlayers);
                System.out.println("Player " + numPlayers + " connected.");

                ReadFromClient rfc = new ReadFromClient(numPlayers, in);
                WriteToClient wtc = new WriteToClient(numPlayers, out);

                if (numPlayers == 1) {
                    p1Socket = s;
                    p1ReadRunnable = rfc;
                    p1WriteRunnable = wtc;
                } else if (numPlayers == 2) {
                    p2Socket = s;
                    p2ReadRunnable = rfc;
                    p2WriteRunnable = wtc;
                    p1WriteRunnable.sendStartMsg();
                    p2WriteRunnable.sendStartMsg();
                    Thread readThread1 = new Thread(p1ReadRunnable);
                    Thread readThread2 = new Thread(p2ReadRunnable);
                    readThread1.start();
                    readThread2.start();
                    Thread writeThread1 = new Thread(p1WriteRunnable);
                    Thread writeThread2 = new Thread(p2WriteRunnable);
                    writeThread1.start();
                    writeThread2.start();
                }
            }
            System.out.println("Maximum number of players reached.");
        } catch (IOException e) {
            System.out.println("IO Exception from acceptConnections()");
        }
    }
    private class ReadFromClient implements Runnable {

            private int playerID;
            private DataInputStream dataIn;

            public ReadFromClient(int pid, DataInputStream in) {
                playerID = pid;
                dataIn = in;
                System.out.println("RFC" + playerID + "Runnable created");
            }
            public void run(){
                try {
                    while (true) {
                        if (playerID == 1) {
                            p1X = dataIn.readDouble();
                            p1Y = dataIn.readDouble();
                        } else if (playerID == 2) {
                            p2X = dataIn.readDouble();
                            p2Y = dataIn.readDouble();
                        }
                    }
                }
                catch (IOException ex) {
                    System.out.println("IO Exception from ReadFromClient.run()");
                }

            }
    }
    private class WriteToClient implements Runnable {

        private int playerID;
        private DataOutputStream dataOut;

        public WriteToClient(int pid, DataOutputStream out) {
            playerID = pid;
            dataOut = out;
            System.out.println("WTC" + playerID + "Runnable created");
        }
        public void run(){
            try {
                while (true) {
                    if (playerID == 1) {
                        dataOut.writeDouble(p2X);
                        dataOut.writeDouble(p2Y);
                    } else if (playerID == 2) {
                        dataOut.writeDouble(p1X);
                        dataOut.writeDouble(p1Y);
                    }
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException ex) {
                        System.out.println("Interrupted Exception from WriteToClient.run()");
                    }
                }
            }
            catch (IOException ex) {
                System.out.println("IO Exception from WriteToClient.run()");
            }

        }
        public void sendStartMsg() {
            try {
                dataOut.writeUTF("We have 2 players. Game will start now.");
            } catch (IOException ex) {
                System.out.println("IO Exception from sendStartMsg()");
            }
        }
    }

        public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.acceptConnections();
        }
    }

