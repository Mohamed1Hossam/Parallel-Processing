package sudokuproject.io;

import model.SudokuBoard;
import java.io.*;
import java.util.*;

/**
 * Loads Sudoku puzzles from files or arrays.
 * Supports space, comma, or no separator formats.
 */
public class PuzzleLoader {

    private static final int SIZE = 9;

    // Load puzzle from a file
    public SudokuBoard loadFromFile(String filename) throws IOException {
        List<String> lines = readLines(filename);
        int[][] grid = parseLines(lines);
        validateGrid(grid);
        return new SudokuBoard(grid);
    }

    // Load puzzle from a 2D array
    public SudokuBoard loadFromArray(int[][] grid) {
        validateGrid(grid);
        int[][] copy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) System.arraycopy(grid[i], 0, copy[i], 0, SIZE);
        return new SudokuBoard(copy);
    }

    // Read non-empty lines, skip comments
    private List<String> readLines(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#") && !line.startsWith("//")) lines.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new IOException("Puzzle file not found: " + filename, e);
        }
        return lines;
    }

    // Convert lines to a 9x9 grid
    private int[][] parseLines(List<String> lines) {
        if (lines.size() != SIZE) throw new IllegalArgumentException("Expected 9 lines, found " + lines.size());
        int[][] grid = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) grid[r] = parseLine(lines.get(r), r);
        return grid;
    }

    // Convert a single line to integers
    private int[] parseLine(String line, int row) {
        line = line.trim();
        String[] tokens;
        if (line.contains(",")) tokens = line.split(",");
        else if (line.contains(" ")) tokens = line.split("\\s+");
        else if (line.length() == SIZE) tokens = line.split("");
        else throw new IllegalArgumentException("Invalid format at row " + (row+1));

        if (tokens.length != SIZE) throw new IllegalArgumentException("Row " + (row+1) + " must have 9 values");

        int[] rowData = new int[SIZE];
        for (int c = 0; c < SIZE; c++) {
            try {
                int v = Integer.parseInt(tokens[c].trim());
                if (v < 0 || v > 9) throw new IllegalArgumentException("Invalid value " + v + " at (" + (row+1) + "," + (c+1) + ")");
                rowData[c] = v;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number '" + tokens[c] + "' at (" + (row+1) + "," + (c+1) + ")");
            }
        }
        return rowData;
    }

    // Check grid size and values
    private void validateGrid(int[][] grid) {
        if (grid == null || grid.length != SIZE) throw new IllegalArgumentException("Grid must have 9 rows");
        for (int r = 0; r < SIZE; r++) {
            if (grid[r] == null || grid[r].length != SIZE) throw new IllegalArgumentException("Row " + (r+1) + " must have 9 columns");
            for (int c = 0; c < SIZE; c++) {
                int v = grid[r][c];
                if (v < 0 || v > 9) throw new IllegalArgumentException("Invalid value " + v + " at (" + r + "," + c + ")");
            }
        }
    }
}
