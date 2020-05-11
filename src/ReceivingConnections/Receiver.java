package ReceivingConnections;

import AnswerSender.CommandToObjectServer;
import AnswerSender.ServerAnswer;
import ClientAnswer.Authorization;
import ClientAnswer.ComplicatedObject;
import CommandProcessing.Control;
import SpaceMarineData.SpaceMarine;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Receiver implements Runnable {
    //  private static String URL = "jdbc:postgresql://pg:5432/studs";
   public static final String URL = "jdbc:postgresql://localhost:5433/studs";
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "******";
    public static SocketChannel socketChannel = null;
    public static String[] sarray;
    public static SpaceMarine spaceMarine;
    public static int g;
    public static Long j;
    public static int p;
    public static String historyR;
    public static String s;
    public static String login;
    public String password;
    public String ScriptPassword;
    public static ArrayDeque<String> logins ;
    Thread thread;
    public Receiver()  {
        thread=new Thread(this, "Поток сервера с чтением запросов");
        thread.start(); //запускаем поток
    }
    @Override
    public  void run() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(55665));
            while (true) {
                try {
                    socketChannel = serverSocketChannel.accept();
                    ObjectInputStream is = new ObjectInputStream(socketChannel.socket().getInputStream());
                    try {
                        while (socketChannel.isConnected()) {
                            s = "";
                            Object object = is.readObject();
                            if (object instanceof Authorization) {
                                login = ((Authorization) object).getLogin();
                                password = ((Authorization) object).getPassword();
                                ScriptPassword = encryptThisString(password);
                                work();
                            } else {
                                g = ((ComplicatedObject) object).getId();
                                j = ((ComplicatedObject) object).getParam();
                                spaceMarine = ((ComplicatedObject) object).getSpaceMarine();
                                p = ((ComplicatedObject) object).getP();
                                historyR = ((ComplicatedObject) object).getHistory();
                                s = ((ComplicatedObject) object).getCommand();
                                ExecutorService service = Executors.newFixedThreadPool(5);
                                for (int i = 0; i < 666; i++) {
                                    service.execute(new Control());
                                }
                            }
                        }
                    } catch (EOFException e) {
                        socketChannel.close();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {}
            }
        }catch (IOException e){}
    }
    public void work() throws IOException {
        String answer = "";
        String query = "SELECT * from users;";
        String query1 = "INSERT INTO users (login, password) VALUES (?,?)";
        boolean checker = false;
        try(Connection connection = DriverManager.getConnection(URL,USERNAME,PASSWORD)){
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString("login").equals(login)) {
                    checker = true;
                    if (resultSet.getString("password").equals(ScriptPassword)) {
                        answer = "Вы успешно вошли в учётную запись. ";
                        CommandToObjectServer commandToObjectServer = new CommandToObjectServer(answer);
                        ServerAnswer.commandToObjectServers.addLast(commandToObjectServer);
                        new ServerAnswer();
                    }else{
                        answer = "00010010";
                        CommandToObjectServer commandToObjectServer = new CommandToObjectServer(answer);
                        ServerAnswer.commandToObjectServers.addLast(commandToObjectServer);
                        new ServerAnswer();
                        socketChannel.close();
                        new Receiver();
                    }
                }
            }
            if (!checker) {
                PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
                preparedStatement1.setString(1,login);
                preparedStatement1.setString(2, ScriptPassword);
                preparedStatement1.executeUpdate();
                answer = "Пользователь успешно авторизовался.\n";
                CommandToObjectServer commandToObjectServer = new CommandToObjectServer(answer);
                ServerAnswer.commandToObjectServers.addLast(commandToObjectServer);
                new ServerAnswer();
            }
            logins=new ArrayDeque<>();
            logins.addFirst(login);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static String encryptThisString(String input) throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance("MD2");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
    }
}
