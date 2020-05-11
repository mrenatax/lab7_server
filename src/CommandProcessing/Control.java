package CommandProcessing;

import AnswerSender.CommandToObjectServer;
import AnswerSender.ServerAnswer;
import java.io.IOException;
import java.sql.SQLException;
import static CommandProcessing.Commands.*;
import static CommandProcessing.Commands.executeScript;
import static ReceivingConnections.Receiver.*;

public class Control extends Thread {
    Thread thread;
    public Control(){
        thread = new Thread(this,"");
        thread.start();
        }
    public void run() {
        try{
            sarray = s.trim().split(" ", 2);
            switch (sarray[0]) {
                case "help":
                    CommandToObjectServer help = new CommandToObjectServer("help", help());
                    ServerAnswer.commandToObjectServers.addLast(help);
                    new ServerAnswer();
                    break;
                case "info":
                    CommandToObjectServer info = new CommandToObjectServer("info", info());
                    ServerAnswer.commandToObjectServers.addLast(info);
                    new ServerAnswer();
                    break;
                case "show":
                    CommandToObjectServer show = new CommandToObjectServer("show", show());
                    ServerAnswer.commandToObjectServers.addLast(show);
                    new ServerAnswer();
                    break;
                case "head":
                    CommandToObjectServer head = new CommandToObjectServer("head", head());
                    ServerAnswer.commandToObjectServers.addLast(head);
                    new ServerAnswer();
                    break;
                case "sum_of_height":
                    CommandToObjectServer sumOfHeight = new CommandToObjectServer("sum_of_height", SumOfHeight());
                    ServerAnswer.commandToObjectServers.addLast(sumOfHeight);
                    new ServerAnswer();
                    break;
                case "max_by_name":
                    CommandToObjectServer maxByName = new CommandToObjectServer("max_by_name", maxByName());
                    ServerAnswer.commandToObjectServers.addLast(maxByName);
                    new ServerAnswer();
                    break;
                case "filter_greater_than_height":
                    CommandToObjectServer filterGr = new CommandToObjectServer("filter_greater_than_height", filterGreater(j));
                    ServerAnswer.commandToObjectServers.addLast(filterGr);
                    new ServerAnswer();
                    break;
                case "update_id":
                    CommandToObjectServer updateId = new CommandToObjectServer("update_id", (updateId(g, spaceMarine)));
                    ServerAnswer.commandToObjectServers.addLast(updateId);
                    new ServerAnswer();
                    break;
                case "add":
                    CommandToObjectServer add = new CommandToObjectServer("add",add(spaceMarine));
                    ServerAnswer.commandToObjectServers.addLast(add);
                    new ServerAnswer();
                    break;
                case "remove_by_id":
                    CommandToObjectServer remove = new CommandToObjectServer("remove_by_id", removeById(p));
                    ServerAnswer.commandToObjectServers.addLast(remove);
                    new ServerAnswer();
                    break;
                case "remove_greater":
                    CommandToObjectServer removeGr = new CommandToObjectServer("remove_greater", removeGreater(p));
                    ServerAnswer.commandToObjectServers.addLast(removeGr);
                    new ServerAnswer();
                    break;
                case "clear":
                    CommandToObjectServer clear = new CommandToObjectServer("clear", clear());
                    ServerAnswer.commandToObjectServers.addLast(clear);
                    new ServerAnswer();
                    break;
                case "history":
                    CommandToObjectServer hist = new CommandToObjectServer("history", historyR);
                    ServerAnswer.commandToObjectServers.addLast(hist);
                    new ServerAnswer();
                    break;
                case "execute_script":
                    CommandToObjectServer script = new CommandToObjectServer("execute_script", executeScript().toString());
                    ServerAnswer.commandToObjectServers.addLast(script);
                    new ServerAnswer();
                    break;
                case "exit":
                    CommandToObjectServer exit = new CommandToObjectServer("exit");
                    ServerAnswer.commandToObjectServers.addLast(exit);
                    socketChannel.close();
                    break;
            }
        }catch (IOException | SQLException e){
            e.printStackTrace();
        }
        }
}
