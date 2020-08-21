package tictactoe;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        boolean isExit = false;
        String[] command = null;

        while (true) {
            while (true) {
                System.out.print("Input command: ");
                String input = scanner.nextLine().toUpperCase();
                if ("EXIT".equals(input)) {
                    isExit = true;
                    break;
                }
                command = input.split(" ");
                if (command.length != 3 || Gamers.isNotContain(command[1]) ||
                        !Command.contains(command[0]) || Gamers.isNotContain(command[2])) {
                    System.out.println("Bad parameters!");
                } else {
                    break;
                }
            }
            if (isExit) {
                break;
            }

            Game game = new Game(command);
            game.start();
        }
    }
}

class Coordinats {
    private final static String[] coord =
            {"1 3", "2 3", "3 3",
                    "1 2", "2 2", "3 2",
                    "1 1", "2 1", "3 1"};

    public static int getIndexOf(String str) {
        for (int i = 0; i < 9; i++) {
            if (coord[i].equals(str)) {
                return i;
            }
        }
        return -1;
    }
}

enum Command {
    EXIT ("EXIT"),
    START ("START");

    private final String  name;

    Command (String name) {
        this.name = name;
    }

    public static boolean contains(String s) {
        return s.equals(EXIT.name) || s.equals(START.name);
    }
}

enum Gamers {
    USER ("USER"),
    EASY ("EASY"),
    MEDIUM ("MEDIUM"),
    HARD ("HARD");

    private final String name;

    Gamers(String name) {
        this.name = name;
    }

    public static boolean isNotContain(String s) {
        return !(USER.name.equals(s) || EASY.name.equals(s) || HARD.name.equals(s) || MEDIUM.name.equals(s));
    }
}

class Game {
    private final Field field;
    private final Gamer gamer1;
    private final Gamer gamer2;
    boolean isGameOver = false;

    protected Game (String[] command) {
        field = new Field();
        gamer1 = CreateGamer.create(command[1]);
        gamer2 = CreateGamer.create(command[2]);
    }

    void start() {
        while (!isGameOver) {
            gamerMove(gamer1);
            if (isGameOver) {
                break;
            }
            gamerMove(gamer2);
        }
    }

    void gamerMove(Gamer gamer) {

        int index = gamer.choseCoordinates(field);
        if (gamer instanceof PCGamer) {
            try {
                System.out.println(((PCGamer) gamer).message);
//                sleep(2000);
            } catch (Exception e) {
                System.out.println("something wrong");
            }
        }
        field.add(index);
        field.print();
        String check = field.check();
        if (!check.equals("Game not finished")) {
            System.out.println(check);
            isGameOver = true;
        }
    }
}

class Field {
    private final char[] chars;
    private int countX = 0;
    private int countO = 0;
    private static final String X_STRING = "XXX";
    private static final String O_STRING = "OOO";

    Field() {
        chars = new char[9];
        Arrays.fill(chars, ' ');
        this.print();
    }

    Field(Field field, int i) {
        this.chars = Arrays.copyOf(field.chars, 9);
        this.countO = field.countO;
        this.countX = field.countX;
        char ch = countX > countO ? 'O' : 'X';
        this.chars[i] = ch;
        if (ch == 'X') {
            countX++;
        } else {
            countO++;
        }
    }

    int getCountX() {
        return countX;
    }

    int getCountO() {
        return countO;
    }

    int getCountOfEmpty() {
        return 9 - countO - countX;
    }

    void print() {
        int n = 0;
        System.out.println("---------");
        for (int i = 0; i < 3; i++) {
            System.out.printf("| %c %c %c |\n", chars[n++], chars[n++], chars[n++]);
        }
        System.out.println("---------");

    }

    boolean isBusy(int ind) {
        return chars[ind] != ' ';
    }

    boolean isBusy(int[] coord) {
        String s = String.format("%d %d", coord[0], coord[1]);
        return isBusy(Coordinats.getIndexOf(s));
    }

    int[] getEmptyField() {
        int[] arr = new int[getCountOfEmpty()];
        int n = 0;
        for (int i = 0; i < 9; i++) {
            if(chars[i] == ' ') {
                arr[n] = i;
                n++;
            }
        }
        return arr;
    }

    void add(int index) {
        char ch = countX > countO ? 'O' : 'X';
        this.chars[index] = ch;
        if (ch == 'X') {
            countX++;
        } else {
            countO++;
        }
    }

    String getStringFromRows() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            sb.append(chars[i]);
        }
        return sb.toString();
    }

    String getStringFromCol() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j += 3) {
                sb.append(chars[i + j]);
            }
        }
        return sb.toString();
    }

    String getDig1() {
        return chars[0] + "" + chars[4] + "" + chars[8];
    }

    String getDig2() {
        return chars[2] + "" + chars[4] + "" + chars[6];
    }

    int result() {
        String strFRow = getStringFromRows();
        String strFCol = getStringFromCol();
        for (int i = 0; i < 3; i++) {
            String strRow = strFRow.substring(3 * i, 3 * i + 3);
            String strCol =strFCol.substring(3 * i, 3 * i + 3);
            if (X_STRING.equals(strRow) || X_STRING.equals(strCol)) {
                return 5;
            }
            if (O_STRING.equals(strRow) || O_STRING.equals(strCol)) {
                return -5;
            }
        }

        String dig1 = getDig1();
        String dig2 = getDig2();
        if (X_STRING.equals(dig1) || X_STRING.equals(dig2)) {
            return 5;
        }
        if (O_STRING.equals(dig1) || O_STRING.equals(dig2)) {
            return -5;
        }

        if ((countX + countO) == chars.length) {
            return 2;
        }
        return 0;
    }

    String check() {
        int result = result();
        if (result == 5) {
            return "X wins";
        }
        if (result == -5) {
            return "O wins";
        }
        if (result == 2) {
            return "Draw";
        }
        return "Game not finished";
    }

    int[] canWinInOneMove(char ch) {
        int[] coord = new int[2];
        char nonCh = ch == 'X' ? 'O' : 'X';
        String strFRow = getStringFromRows();
        String strFCol = getStringFromCol();
        for (int i = 0; i < 3; i++) {
            String strRow = strFRow.substring(3 * i, 3 * i + 3);
            if (strRow.contains(" ") && !strRow.contains(nonCh + "") && strRow.contains(ch + "") &&
                    strRow.indexOf(ch) != strRow.lastIndexOf(ch)) {
                coord[0] = strRow.indexOf(" ") + 1;
                coord[1] = 3 - i;
                return coord;
            }
        }

        for (int i = 0; i < 3; i++) {
            String strCol = strFCol.substring(3 * i, 3 * i + 3);
            if (strCol.contains(" ") && !strCol.contains(nonCh + "") && strCol.contains(ch + "") &&
                    strCol.indexOf(ch) != strCol.lastIndexOf(ch)) {
                coord[0] = i + 1;
                coord[1] = 3 - strCol.indexOf(" ");
                return coord;
            }
        }

        String dig1 = getDig1();
        if (dig1.contains(" ") && !dig1.contains(nonCh + "") && dig1.contains(ch + "") &&
                dig1.indexOf(ch) != dig1.lastIndexOf(ch)) {
            int indWS = dig1.indexOf(" ");
            if (indWS == 0) {
                coord[0] = 1;
                coord[1] = 3;
            } else if (indWS == 1) {
                coord[0] = 2;
                coord[1] = 2;
            } else {
                coord[0] = 3;
                coord[1] = 1;
            }
            return coord;
        }

        String dig2 = getDig2();
        if (dig2.contains(" ") && !dig2.contains(nonCh + "") && dig2.contains(ch + "") &&
                dig2.indexOf(ch) != dig2.lastIndexOf(ch)) {
            int indWS = dig2.indexOf(" ");
            if (indWS == 0) {
                coord[0] = 3;
                coord[1] = 3;
            } else if (indWS == 1) {
                coord[0] = 2;
                coord[1] = 2;
            } else {
                coord[0] = 1;
                coord[1] = 1;
            }
            return coord;
        }
        return null;
    }
}

class CreateGamer {
    public static Gamer create(String gamer) {
        switch (gamer) {
            case "EASY": {
                return new EasyPcGamer();
            }
            case "USER": {
                return new UserGamer();
            }
            case "MEDIUM": {
                return new MediumPCGamer();
            }
            case "HARD": {
                return new HardPCGamer();
            }
        }
        return null;
    }
}

abstract class Gamer {
    abstract int choseCoordinates(Field field);
}

class UserGamer extends Gamer {

    @Override
    int choseCoordinates(Field field) {
        Scanner scanner = new Scanner(System.in);
        int[] coord = new int[2];
        while (true){
            int x;
            int y;
            while (true) {
                System.out.print("Enter the coordinates: ");
                try {
                    String s = scanner.nextLine();
                    String[] str = s.split(" ");
                    x = Integer.parseInt(str[0]);
                    y = Integer.parseInt(str[1]);
                } catch (Exception e) {
                    System.out.println("You should enter numbers!");
                    continue;
                }
                if (x < 1 || x > 3 || y < 1 || y > 3) {
                    System.out.println("Coordinates should be from 1 to 3!");
                } else {
                    coord[0] = x;
                    coord[1] = y;
                    break;
                }
            }
            if (field.isBusy(coord)) {
                System.out.println("This cell is occupied! Choose another one!");
            } else break;
        }
        String s = String.format("%d %d", coord[0], coord[1]);
        return Coordinats.getIndexOf(s);
    }
}

abstract class PCGamer extends Gamer {
    String message;

    PCGamer(String message) {
        this.message = message;
    }
}

class EasyPcGamer extends PCGamer {

    EasyPcGamer() {
        super("Making move level \"easy\"");
    }

    @Override
    int choseCoordinates(Field field) {
        Random random = new Random();
        int[] coord = new int[2];
        do {
            coord[0] = random.nextInt(3) + 1;
            coord[1] = random.nextInt(3) + 1;
        } while (field.isBusy(coord));

        String s = String.format("%d %d", coord[0], coord[1]);
        return Coordinats.getIndexOf(s);
    }
}

class MediumPCGamer extends PCGamer {

    public MediumPCGamer() {
        super("Making move level \"medium\"");
    }

    @Override
    int choseCoordinates(Field field) {
        int[] coord = choose(field);
        String s = String.format("%d %d", coord[0], coord[1]);
        return Coordinats.getIndexOf(s);
    }

    int[] choose(Field field) {
        char ch = field.getCountX() > field.getCountO() ? 'O' : 'X';
        int[] coord = field.canWinInOneMove(ch);
        if (coord != null) {
            return coord;
        }

        char nonCh = ch == 'X' ? 'O' : 'X';
        coord = field.canWinInOneMove(nonCh);
        if (coord != null) {
            return coord;
        }

        Random random = new Random();
        coord = new int[2];
        do {
            coord[0] = random.nextInt(3) + 1;
            coord[1] = random.nextInt(3) + 1;
        } while (field.isBusy(coord));

        return coord;
    }
}

class HardPCGamer extends PCGamer {
    HardPCGamer() {
        super("Making move level \"hard\"");
    }

    @Override
    int choseCoordinates(Field field) {
        int n = field.getCountOfEmpty();
        boolean k = n % 2 == 0;
        int[] matrix = recursion(n, k, field);
        int bestMove = minMax(!k, matrix);
        int[] bestMatrix = matrixBestMoves(matrix, bestMove);
        Random random = new Random();
        int indexBestMatrix = random.nextInt(bestMatrix.length);
        int i = bestMatrix[indexBestMatrix];
        return field.getEmptyField()[i];
    }

    private int[] matrixBestMoves(int[] matrix, int bestMove) {
        int[] m = new int[matrix.length];
        int count = 0;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] == bestMove) {
                m[count] = i;
                count++;
            }
        }
        return Arrays.copyOf(m, count);
    }

    private int[] recursion(int n, boolean k, Field field) {
        if (n == 0 || Math.abs(field.result()) == 5) {
            return new int[]{field.result()};
        }
        else {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) {
                int ind = field.getEmptyField()[i];
                Field newField = new Field(field, ind);
                int[] temp = recursion(n - 1, !k, newField);
                int d = minMax(k, temp);
                arr[i] = d;
            }
            return arr;
        }
    }

    private int minMax(boolean k, int[] arr) { // удалить статик
        if (k) {
            int max = arr[0];
            for (int i = 1; i < arr.length; i++) {
                max = Math.max(arr[i], max);
            }
            return max;
        } else {
            int min = arr[0];
            for (int i = 1; i < arr.length; i++) {
                min = Math.min(arr[i], min);
            }
            return min;
        }
    }

}