package CommandProcessing;

import ReceivingConnections.Receiver;
import SpaceMarineData.*;
import org.postgresql.util.PSQLException;

import java.io.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;


public class Commands {
    public static ArrayDeque<SpaceMarine> collection = new ArrayDeque<>();
    public static Scanner slmmsk;
    private static ArrayList al = new ArrayList();
    private static int q=0;
    public static ReentrantLock locker = new ReentrantLock(); // создаем заглушку
    public static String help(){
        String help = "Доступны следующие комманды, которые могут: \n" +
                "help : вывести справку по доступным командам\n" +
                "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.) \n" +
                "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении \n" +
                "add {element} : добавить новый элемент в коллекцию \n" +
                "update_id {element} : обновить значение элемента коллекции, id которого равен заданному \n" +
                "remove_by_id id : удалить элемент из коллекции по его id \n" +
                "clear : очистить коллекцию \n" +
                "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме. \n" +
                "exit : завершить программу (без сохранения в файл) \n" +
                "head : вывести первый элемент коллекции \n" +
                "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный по id \n" +
                "history : вывести последние 13 команд (без их аргументов) \n" +
                "sum_of_height : вывести сумму значений поля height для всех элементов коллекции \n" +
                "max_by_name : вывести любой объект из коллекции, значение поля name которого является максимальным \n" +
                "filter_greater_than_height height : вывести элементы, значение поля height которых больше заданного \n";
        return help;
    }
    public static String info(){
        String info;
        if (collection.size() != 0){
            info = "Размер коллекции: " + collection.stream().mapToInt((p)->1).sum() + "\n"
                    + "Тип коллекции: " + collection.getClass() + "\n"
                    + "Дата инициализации: " + Date.from(Instant.now()) + "\n"
                    + "Name первого элемента: " + collection.getFirst().getName();
        }
        else  info = "Коллекция пуста";
        return info;
    }
    public static void uploadData(){
        String sql = "SELECT * FROM spacemarine";
        try (Connection connection = DriverManager.getConnection(Receiver.URL,Receiver.USERNAME,Receiver.PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        int id = resultSet.getInt("id");
                        java.util.Date creationDate = resultSet.getDate("creationDate");
                        Long height = resultSet.getLong("height");
                        Long health = resultSet.getLong("health");
                        String weapon = resultSet.getString("weapon");
                        String meleeWeapon = resultSet.getString("meleeWeapon");
                        float x = resultSet.getFloat("x");
                        float y = resultSet.getFloat("y");
                        String chapter_name = resultSet.getString("chapter_name");
                        Long chapter_marinesCount = resultSet.getLong("chapter_marinesCount");
                        String login = resultSet.getString("login");
                        SpaceMarine sm = new SpaceMarine(name, id, health, height, new Chapter(chapter_name,chapter_marinesCount),new Coordinates(x,y),setMeleeWeapon(meleeWeapon), setWeaponType(weapon),login);
                        collection.add(sm);
            }
        } catch (SQLException e) {
            System.out.println("возникла ошибка");
            e.printStackTrace();
        }
    }


    public static String add(SpaceMarine spaceMarine) throws SQLException {
    PreparedStatement preparedStatement = null;
    Connection connection = null;
        String answer = "";
        String sqlStatement = "INSERT INTO spacemarine (creationDate,name,height,health,weapon,meleeWeapon,x,y,chapter_name,chapter_marinesCount,login) VALUES (?,?,?,?,?,?,?,?,?,?,?);";
            try  {
                connection = DriverManager.getConnection(Receiver.URL, Receiver.USERNAME, Receiver.PASSWORD);
                preparedStatement = connection.prepareStatement(sqlStatement);
                java.util.Date crDate = spaceMarine.getCreationDate();
                java.sql.Date sqlDate = new java.sql.Date(crDate.getTime());
                preparedStatement.setDate(1, sqlDate);
                preparedStatement.setString(2, spaceMarine.getName());
                preparedStatement.setLong(3, spaceMarine.getHeight());
                preparedStatement.setLong(4, spaceMarine.getHealth());
                preparedStatement.setString(5, String.valueOf(spaceMarine.getWeapon()));
                preparedStatement.setString(6, String.valueOf(spaceMarine.getMeleeWeapon()));
                preparedStatement.setFloat(7, spaceMarine.getCoordinates().getX());
                preparedStatement.setFloat(8, spaceMarine.getCoordinates().getY());
                preparedStatement.setString(9, spaceMarine.getChapter().getChapterName());
                preparedStatement.setLong(10, spaceMarine.getChapter().getMarinesCount());
                preparedStatement.setString(11,Receiver.login);
                preparedStatement.executeUpdate();
                uploadData();
                answer = "Элемент успешно добавлен в коллекцию.";
            } catch (SQLException e) {
                e.printStackTrace();
                answer += "Возникла ошибка.";
            } finally {
                 if (preparedStatement != null) {
                     preparedStatement.close();
                 }
                if (connection != null) {
                    connection.close();
                }
            }
        return answer;
    }
 public static String show(){
      String show;
      if (collection.size() != 0) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("[");
          collection.stream().forEach((p) -> stringBuilder.append(p.toString()+","));
          stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
          return String.valueOf(stringBuilder.append("]"));
      }
      else show = "Коллекция пуста";
      return show;
  }
    public static String clear() throws SQLException {
      String clear = "";
      try {
          Connection conn = DriverManager.getConnection(Receiver.URL, Receiver.USERNAME, Receiver.PASSWORD);
          String deleteTableSQL = "DELETE FROM spacemarine WHERE login = " + "'" + Receiver.login + "'" + ";";
          Statement statement = conn.createStatement();
          statement.execute(deleteTableSQL);
          collection.clear();
          uploadData();
          clear = "Коллекция успешно отформатирована. (Удалены все объекты, принадлежащие пользователю с логином " + Receiver.login + ")";
      }catch (PSQLException e){
          clear = "Данный пользователь "+ "(" + Receiver.login + ")" +" не может удалить элементы коллекции, т.к. они созданы другим пользователем. ";
      }
        return clear;
    }
    public static String updateId(int x, SpaceMarine spaceMarine) throws SQLException {
        locker.lock();
        java.util.Date crDate = spaceMarine.getCreationDate();
        java.sql.Date sqlDate = new java.sql.Date(crDate.getTime());
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        String answer ="";
        String condition = "SELECT * FROM spacemarine WHERE login = " + "'" + Receiver.login + "'";
        int ix = (int) (Math.random()*(1000+1));
        try  {
            connection = DriverManager.getConnection(Receiver.URL, Receiver.USERNAME, Receiver.PASSWORD);
            preparedStatement = connection.prepareStatement(condition,ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = preparedStatement.executeQuery();
            int counter = 0;
            while (resultSet.next()){
                    if((resultSet.getInt(1))==x){
                        resultSet.updateString("name",spaceMarine.getName());
                        resultSet.updateString("chapter_name",spaceMarine.getChapter().getChapterName());
                        resultSet.updateString("weapon", String.valueOf(spaceMarine.getWeapon()));
                        resultSet.updateString("meleeweapon", String.valueOf(spaceMarine.getMeleeWeapon()));
                        resultSet.updateDate("creationdate",sqlDate);
                        resultSet.updateInt("id",ix);
                        resultSet.updateLong("height",spaceMarine.getHeight());
                        resultSet.updateLong("health",spaceMarine.getHealth());
                        resultSet.updateFloat("x",spaceMarine.getCoordinates().getX());
                        resultSet.updateFloat("y",spaceMarine.getCoordinates().getY());
                        resultSet.updateLong("chapter_marinescount",spaceMarine.getChapter().getMarinesCount());
                        resultSet.updateRow();
                        counter++;
                    }
            }
            if (counter==0){
                answer = "Элемент с таким id не найден или у пользователя (" + Receiver.login  +  ") нет доступа к модификации объекта c id " + x;
            } else {
                answer = "Значения элемента успешно обновлены.";
                collection.clear();
                uploadData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            answer += "Возникла ошибка.";
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
            locker.unlock();
        }
        return answer;
    }
    public static String removeById(int x) throws SQLException {
      locker.lock();
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        String answer ="";
        String select = "DELETE FROM spacemarine WHERE id = " + x + " AND login = " + "'" + Receiver.logins.getFirst() + "'" + ";";
        try  {
            connection = DriverManager.getConnection(Receiver.URL, Receiver.USERNAME, Receiver.PASSWORD);
            preparedStatement = connection.prepareStatement(select);
            int j = preparedStatement.executeUpdate();
            if (j==1){
                answer = "Элемент успешно удалён";
                collection.clear();
                uploadData();
            } else if (j == 0){
                answer = "Элемент с таким id не найден / пользователь с логином "+ Receiver.login + " не имеет доступа к элементу с id " + x;
            }else{
                answer = "Элемент успешно удалён";
                collection.clear();
                uploadData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            locker.unlock();
        }
        return answer;
    }
    public static String head(){
        String head;
        if (collection.size() != 0) {
            head = String.valueOf(collection.getFirst());
        }
        else head = "Коллекция пуста";
        return head;
    }
    public static String SumOfHeight(){
        Long x = 0l;
        for (SpaceMarine s : collection) {
            x = x + s.getHeight();
        }
        String sumOfHeight = "Сумма значений поля height: " + x.toString();

        return sumOfHeight;
    }

    public static String maxByName() {
        String maxByName = "";
        if (collection.size() != 0) {
            if (collection.size() == 1) {
                maxByName = collection.element().getName();
            } else {
                String max = "";
                SpaceMarine sl = collection.getFirst();
                for (SpaceMarine s : collection) {
                    if(s.compareTo(sl)>0){
                        max = s.getName();
                    }
                    else {
                        max = sl.getName();
                    }

                }
                maxByName = max;
            }
        }
        else maxByName = "Коллекция пуста";
        return maxByName;
    }
    public static String filterGreater(Long min){
        String filterGreater = " ";
        if (collection.size() != 0) {
            for (SpaceMarine s : collection) {
                if (s.getHeight().compareTo(min) > 0) {
                    filterGreater = filterGreater + " " + s.getName();
                }
            }
        }
        return filterGreater;
    }
    private static String getFilePathForScript(){
        String path = System.getenv("dos");
        System.out.println(System.getProperty("user.dir"));
        if (path == null){
            return ("----\nПуть через переменную окружения dos не указан\n----");
        } else {
            return path;
        }
    }
    private static String addForScript() throws IOException {
        String scriptReceive = "";
        SpaceMarine sm =new SpaceMarine();
        sm.setNameF();
        sm.setHealthF();
        sm.setHeightF();
        sm.setWeaponTypeF();
        sm.setMeleeWeaponF();
        sm.setCoordinatesF();
        sm.setChapterF();
        sm.setId();
        sm.setCreationDate();
        if ((sm.getName() ==null)||(sm.getName().trim().length() ==0)||(sm.getChapter().getChapterName().trim().length()==0)||(sm.getChapter().getChapterName()==null)||((sm.getChapter().getMarinesCount())>1000)||((sm.getChapter().getMarinesCount())<=0)||(sm.getHealth() == null)||(sm.getCoordinates().getX() == null)||(sm.getHealth() <= 0)||(sm.getHeight() == null)||(sm.getMeleeWeapon() == null)||(sm.getCoordinates()==null)){
            scriptReceive = "некорректные поля в скрипте\n"+
                    "Такой объект в коллекцию не будет добавлен";
        }
        else {
            collection.add(sm);
            scriptReceive = "Объект успешно добавлен в коллекцию.";
        }
        return scriptReceive;
    }

  public static String removeGreater (int lj){
      PreparedStatement preparedStatement = null;
      Connection connection = null;
      String answer ="";
      lj = lj+1;
      String select = "DELETE FROM spacemarine WHERE id > " + lj + " AND login = " + "'" + Receiver.login + "'" + ";";
      try  {
          connection = DriverManager.getConnection(Receiver.URL, Receiver.USERNAME, Receiver.PASSWORD);
          preparedStatement = connection.prepareStatement(select);
          int j = preparedStatement.executeUpdate();
          if (j==1){
              answer = "Элементы, id которых превышает " + lj + " созданы пользователем с логином "+ Receiver.login +", успешно удалены.";
          } else if (j == 0){
              answer = "Элементы, id которых превышает " + lj + " не найдены.";
          }else{
              answer = "Элементы, id которых превышает " + lj + " созданы пользователем с логином "+ Receiver.login +", успешно удалены.";
          }
      } catch (SQLException e) {
          e.printStackTrace();
      }
      collection.clear();
      uploadData();
      return answer;
  }
    public static Weapon setWeaponType(String weapon){
        Weapon wp = Weapon.BOLTGUN;
        switch (weapon){
            case ("BOLTGUN"):
                 wp = Weapon.BOLTGUN;
                break;
            case ("boltgun"):
                wp = Weapon.BOLTGUN;
                break;
            case ("HEAVY_BOLTGUN"):
                wp = Weapon.HEAVY_BOLTGUN;
                break;
            case ("heavy_boltgun"):
                wp = Weapon.HEAVY_BOLTGUN;
                break;
            case ("HEAVY_FLAMER"):
                wp = Weapon.HEAVY_FLAMER;
                break;
            case ("heavy_flamer"):
                wp = Weapon.HEAVY_FLAMER;
                break;
            case ("GRENADE_LAUNCHER"):
                wp = Weapon.GRENADE_LAUNCHER;
                break;
            case  ("grenade_launcher"):
                wp = Weapon.GRENADE_LAUNCHER;
                break;
            case ("MULTI_MELTA"):
                wp = Weapon.MULTI_MELTA;
                break;
            case ("multi_melta"):
                wp = Weapon.MULTI_MELTA;
                break;
        }
        if (weapon.trim().length() == 0) return null;
        return wp;
    }
    public static MeleeWeapon setMeleeWeapon(String meleeWeapon){
        MeleeWeapon mw = MeleeWeapon.CHAIN_AXE;
        switch (meleeWeapon){
            case("CHAIN_AXE"):
                mw = MeleeWeapon.CHAIN_AXE;
                break;
            case("chain_axe"):
                mw = MeleeWeapon.CHAIN_AXE;
                break;
            case("CHAIN_SWORD"):
                mw = MeleeWeapon.CHAIN_SWORD;
                break;
            case("chain_sword"):
                mw = MeleeWeapon.CHAIN_SWORD;
                break;
            case("LIGHTING_CLAW"):
                mw = MeleeWeapon.LIGHTING_CLAW;
                break;
            case("lighting_claw"):
                mw = MeleeWeapon.LIGHTING_CLAW;
                break;
            case ("POWER_FIST"):
                mw = MeleeWeapon.POWER_FIST;
                break;
            case ("power_fist"):
                mw = MeleeWeapon.POWER_FIST;
                break;
        }
        return mw;
    }
    public static ArrayList<String> executeScript() throws IOException, SQLException {
        ArrayList<String> scriptoData = new ArrayList<>();
        ArrayList<String> scriptoHistory = new ArrayList<>();
        al.add(q);
        q++;
        if (al.size() > 4){
            System.out.println("скрипт может зациклиться");
        }
        else {
            try {
                String pathToFile = getFilePathForScript();
                if (pathToFile == null)
                    System.out.println("----\nПуть не указан, дальнейшая работа не возможна.\n----");
                else {
                    String[] ImpData = null;
                    try {
                        String str = null;
                        //slmmsk = new Scanner(new File("script.txt"));
                        slmmsk = new Scanner(new InputStreamReader(new FileInputStream(pathToFile)));
                        while ((str = slmmsk.nextLine()) != null) {
                            String[] list = str.split(" ");
                            for (int i = 0; i < list.length; i++) {
                                str = list[i];
                                str = str.trim();
                                list = str.trim().split(" ", 2);
                                try {
                                    switch (list[0]) {
                                        case "info":
                                            info();
                                            scriptoData.add(info());
                                            scriptoHistory.add("info");
                                            break;
                                        case "help":
                                            help();
                                            scriptoData.add(help());
                                            scriptoHistory.add("help");
                                            break;
                                        case "head":
                                            if (collection.size() != 0) {
                                                scriptoData.add(head());
                                            } else scriptoData.add("Коллекция пуста");
                                            scriptoHistory.add("head");
                                            break;
                                        case "clear":
                                            scriptoData.add(clear());
                                            scriptoHistory.add("clear");
                                            break;
                                        case "show":
                                            scriptoData.add(show());
                                            String show = "show";
                                            scriptoHistory.add(show);
                                            break;
                                        case "add":
                                            scriptoData.add(addForScript());
                                            scriptoHistory.add("add");
                                            break;
                                        case "update_id":
                                            String update = "";
                                            if (collection.size() != 0) {
                                                try {
                                                    int x = slmmsk.nextInt();
                                                    int k = 0;
                                                    for (SpaceMarine s : collection) {
                                                        if (x == s.getId()) {
                                                            s.setNewId();
                                                            k++;
                                                            update = "Команда update_id успешно выполнена.";
                                                        }
                                                    }
                                                    if (k == 0) {
                                                        update = "Элемент с таким id не найден";
                                                    }
                                                } catch (InputMismatchException e) {
                                                    update ="Поле в скрипте заполнено некорректно";
                                                }
                                            } else update = "Коллекция пуста";
                                            scriptoHistory.add("update_id");
                                            scriptoData.add(update);
                                            break;
                                        case "remove_by_id":
                                            String remove = "";
                                            if (collection.size() != 0) {
                                                try {
                                                    int x = slmmsk.nextInt();
                                                    int ki = 0;
                                                    for (SpaceMarine s : collection) {
                                                        if (x == s.getId()) {
                                                            collection.remove(s);
                                                            ki++;
                                                        }
                                                    }
                                                    if (ki == 0) {
                                                        remove = "Элемент с таким id не найден";
                                                    }
                                                } catch (InputMismatchException e) {
                                                    remove = "Поле id в скрипте заполнено некорректно";
                                                }
                                            } else remove = "Коллекция пуста";
                                            scriptoHistory.add("remove_by_id");
                                            scriptoData.add(remove);
                                            break;
                                        case "sum_of_height":
                                            if (collection.size() != 0) {
                                                scriptoData.add(SumOfHeight());
                                            } else scriptoData.add("Коллекция пуста");
                                            scriptoHistory.add("sum_of_height");
                                            break;
                                        case "max_by_name":
                                            if (collection.size() != 0) {
                                                scriptoData.add(maxByName());
                                            } else scriptoData.add("Коллекция пуста");
                                            scriptoHistory.add("max_by_name");
                                            break;
                                        case "filter_greater_than_height":
                                            if (collection.size() != 0) {
                                                try {
                                                    Long min = slmmsk.nextLong();
                                                    for (SpaceMarine s : collection) {
                                                        if (s.getHeight() > min) {
                                                            scriptoData.add(s.getName());
                                                        }
                                                    }
                                                } catch (InputMismatchException e) {
                                                    scriptoData.add("Поле в скрипте заполнено некорректно");
                                                }
                                            } else scriptoData.add("Коллекция пуста");
                                            scriptoHistory.add("filter_greater_than_height");
                                            break;
                                        case "remove_greater":
                                            if (collection.size() != 0) {
                                                try {
                                                    int z = slmmsk.nextInt() - 1;
                                                    if (z < 0) {
                                                        scriptoData.add("при вызове команды remove_greater аргумент должен быть целым и больше нуля");
                                                    }
                                                    Object[] arr = collection.toArray();
                                                    for (int p = 0; p < arr.length; p++) {
                                                        if (p > z) {
                                                            collection.remove(arr[p]);
                                                            scriptoData.add("Команда remove_greater успешно выполнена");
                                                        }
                                                    }
                                                } catch (InputMismatchException e) {
                                                    scriptoData.add("Некорректные данные в скрипте для команды remove_greater: должно быть целое положительное число");
                                                }
                                            } else scriptoData.add("Коллекция пуста");
                                            scriptoHistory.add("remove_greater");
                                            break;
                                        case "history":
                                            scriptoData.add(scriptoHistory.toString());
                                            scriptoHistory.add("history");
                                            break;
                                        case "exit":
                                            break;
                                        case "execute_script":
                                            executeScript();
                                            //CommandControl.history.add(CommandControl.commandName);
                                            break;
                                    }
                                } catch (NoSuchElementException e) {
                                    System.out.println("Программа завершена");
                                }
                            }
                        }
                        slmmsk.close();
                    } catch (FileNotFoundException e) {
                        System.out.println("Файл не найден ;(");
                    }

                }
            } catch (NoSuchElementException e) {
                e.getMessage();
            }

        }
        return scriptoData;
    }
}
