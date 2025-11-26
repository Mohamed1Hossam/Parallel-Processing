package Testing;

import java.io.IOException;
import model.SudokuBoard;
import model.CellPosition;
import sudokuproject.io.SudokuIO;
import java.util.Optional;

/**
 * Mock run for testing the completed I/O and model classes.
 * Loads puzzles, prints boards, and checks basic functionality.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════╗");
        System.out.println("║ Sudoku I/O + Model Mock Run   ║");
        System.out.println("╚════════════════════════════════╝\n");

        SudokuIO io = new SudokuIO();

        // === TEST 1: Load puzzle from array ===
        int[][] puzzleArray = {
            {5,3,0,0,7,0,0,0,0}, {6,0,0,1,9,5,0,0,0}, {0,9,8,0,0,0,0,6,0},
            {8,0,0,0,6,0,0,0,3}, {4,0,0,8,0,3,0,0,1}, {7,0,0,0,2,0,0,0,6},
            {0,6,0,0,0,0,2,8,0}, {0,0,0,4,1,9,0,0,5}, {0,0,0,0,8,0,0,7,9}
        };

        System.out.println("=== Test 1: Load from Array ===");
        SudokuBoard board1 = io.loadPuzzle(puzzleArray);
        io.printBoard(board1, "Puzzle Loaded from Array");
        io.printStatistics(board1);

        Optional<CellPosition> empty1 = board1.findNextEmptyCell();
        empty1.ifPresent(pos -> 
            System.out.println("First empty cell: (" + pos.getRow() + "," + pos.getColumn() + ")\n")
        );

        // === TEST 2: Load puzzle from file (if exists) ===
        System.out.println("=== Test 2: Load from File ===");
        String filename = "puzzles/easy.txt";
        try {
            SudokuBoard board2 = io.loadPuzzle(filename);
            io.printBoard(board2, "Puzzle Loaded from File: " + filename);
            io.printStatistics(board2);
        } catch (IOException e) {
            System.out.println("File load skipped: " + e.getMessage() + "\n");
        }

        // === TEST 3: Test isValidMove() and setValue() ===
        System.out.println("=== Test 3: Validate Move and Set Value ===");
        int testRow = 0, testCol = 2, testValue = 4;
        boolean validMove = board1.isValidMove(testRow, testCol, testValue);
        System.out.println("Is value " + testValue + " valid at (" + testRow + "," + testCol + ")? " + validMove);
        if (validMove) {
            board1.setValue(testRow, testCol, testValue);
            io.printBoard(board1, "After Setting Value " + testValue);
        }

        System.out.println("\nMock run completed.");
    }
}
