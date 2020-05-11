package AnswerSender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;
public class ServerAnswer implements Runnable {
    public static LinkedList<CommandToObjectServer> commandToObjectServers  =new LinkedList<>();;
    Thread answerThread;
    public ServerAnswer() throws IOException {
        answerThread=new Thread(this, "Поток сервера с отправкой ответов");
        answerThread.start(); //запускаем поток
    }
    @Override
    public void run() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            if (commandToObjectServers.getLast().answer=="exit"){
            }else {
                objectOutputStream.writeObject(commandToObjectServers.getLast().answer);
                objectOutputStream.flush();
                byte[] bytes = new byte[65535];
                bytes = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.flush();
                DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("localhost"), 55665);
                DatagramSocket datagramSocket = new DatagramSocket();
                datagramSocket.send(datagramPacket);
            }
        }catch(IOException| NoSuchElementException e){
            e.printStackTrace();
        }
    }
}
