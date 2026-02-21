package com.abdullahkoyuncu.tictactoe;

import java.util.Scanner;
import java.util.InputMismatchException;

enum Mark {
    X, O
}

public class TicTacToe {
    static void main() {
        ConsoleIO io = new ConsoleIO(new Scanner(System.in));
        GameRunner runner = new GameRunner(io);
        runner.run();
    }
}

class GameRunner {
    private final ConsoleIO io;

    GameRunner(ConsoleIO io) {
        this.io = io;
    }

    void run() {
        boolean again;
        do {
            Game game = new Game(io);
            game.play();
            again = io.askYesNo();
        } while (again);
    }
}

class Game {
    private final Mark[] board = new Mark[9];
    private final ConsoleIO io;
    private Mark current = Mark.X;

    Game(ConsoleIO io) {
        this.io = io;
    }

    void play() {
        while (true) {
            io.printBoard(board);
            int choice;
            try {
                choice = io.readInt("Player " + current + " choose (1-9): ");
            } catch (Exception e) {
                io.printError("Please enter a number.");
                continue;
            }
            int index = choice - 1;
            if (index < 0 || index >= 9) {
                io.printError("Choice must be between 1 and 9.");
                continue;
            }
            if (board[index] != null) {
                io.printError("This position is already taken.");
                continue;
            }
            board[index] = current;
            GameResult result = evaluate();
            if (result.finished()) {
                io.printBoard(board);
                io.printResult(result);
                return;
            }
            current = (current == Mark.X) ? Mark.O : Mark.X;
        }
    }

    private GameResult evaluate() {
        int[][] wins = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};
        for (int[] w : wins) {
            if (board[w[0]] != null && board[w[0]] == board[w[1]] && board[w[1]] == board[w[2]]) {
                return GameResult.win(board[w[0]]);
            }
        }
        for (Mark m : board) {
            if (m == null) return GameResult.continueGame();
        }
        return GameResult.drawGame();
    }
}

record GameResult(boolean finished, boolean draw, Mark winner) {
    static GameResult win(Mark m) {
        return new GameResult(true, false, m);
    }

    static GameResult drawGame() {
        return new GameResult(true, true, null);
    }

    static GameResult continueGame() {
        return new GameResult(false, false, null);
    }
}

class ConsoleIO {
    private final Scanner in;

    ConsoleIO(Scanner in) {
        this.in = in;
    }

    int readInt(String prompt) {
        System.out.print(prompt);
        if (!in.hasNextInt()) {
            in.nextLine();
            throw new InputMismatchException();
        }
        int value = in.nextInt();
        in.nextLine();
        return value;
    }

    boolean askYesNo() {
        System.out.print("Play again? (y/n): ");
        return in.nextLine().trim().toLowerCase().startsWith("y");
    }

    void printBoard(Mark[] board) {
        System.out.println();
        for (int i = 0; i < board.length; i++) {
            String cell;
            if (board[i] == null) {
                cell = String.valueOf(i + 1);
            } else {
                switch (board[i]) {
                    case X -> cell = "\u001B[34mX\u001B[0m"; // ANSI blue
                    case O -> cell = "\u001B[31mO\u001B[0m"; // ANSI red
                    default -> cell = String.valueOf(i + 1);
                }
            }
            System.out.print(cell);
            if (i % 3 != 2) System.out.print(" | ");
            else if (i < 8) System.out.println();
        }
        System.out.println("\n");
    }

    void printResult(GameResult result) {
        if (result.draw()) {
            System.out.println("It's a draw!");
        } else {
            System.out.println("Winner: " + result.winner());
        }
    }

    void printError(String msg) {
        System.out.println("Error: " + msg);
    }
}