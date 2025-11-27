package io;

import model.SudokuBoard;
import java.io.IOException;

/**
 * Provides all input/output operations for Sudoku puzzles.
 * This class acts as a facade, combining loading, printing,
 * exporting, and writing Sudoku boards through PuzzleLoader
 * and PuzzleWriter.
 */
public class SudokuIO {
    
    private final PuzzleLoader loader;   // Handles reading puzzles
    private final PuzzleWriter writer;   // Handles writing and printing puzzles
    
    /**
     * Creates a SudokuIO instance with default loader and writer.
     */
    public SudokuIO() {
        this.loader = new PuzzleLoader();
        this.writer = new PuzzleWriter();
    }

    /**
     * Loads a Sudoku puzzle from the specified file path.
     * Returns a SudokuBoard containing the puzzle data.
     * Throws IOException if the file cannot be opened or read.
     * @param filename
     * @return 
     * @throws java.io.IOException
     */
    public SudokuBoard loadPuzzle(String filename) throws IOException {
        return loader.loadFromFile(filename);
    }

    /**
     * Loads a Sudoku puzzle from a 9x9 integer array.
     * The value 0 in the array is treated as an empty cell.
     * Returns a SudokuBoard based on the provided grid.
     * @param grid
     * @return 
     */
    public SudokuBoard loadPuzzle(int[][] grid) {
        return loader.loadFromArray(grid);
    }

    /**
     * Prints a Sudoku board to the console.
     * Shows the board using the default console formatting.
     * @param board
     */
    public void printBoard(SudokuBoard board) {
        writer.printToConsole(board);
    }

    /**
     * Prints a Sudoku board to the console with a custom title
     * displayed above the board for better labeling.
     * @param board
     * @param title
     */
    public void printBoard(SudokuBoard board, String title) {
        writer.printToConsole(board, title);
    }

    /**
     * Writes the contents of the given Sudoku board to a file.
     * This is typically used to save a solved puzzle.
     * Throws IOException if the file cannot be written to.
     * @param board
     * @param filename
     * @throws java.io.IOException
     */
    public void writeSolution(SudokuBoard board, String filename) throws IOException {
        writer.writeToFile(board, filename);
    }

    /**
     * Converts the Sudoku board into a simple text format
     * with space-separated values for exporting or debugging.
     * Returns the generated formatted string.
     * @param board
     * @return 
     */
    public String exportToString(SudokuBoard board) {
        return writer.exportToString(board);
    }

    /**
     * Displays a side-by-side comparison between an original Sudoku
     * puzzle and its solved version. Useful for debugging or analysis.
     * @param original
     * @param solved
     */
    public void printComparison(SudokuBoard original, SudokuBoard solved) {
        writer.printComparison(original, solved);
    }

    /**
     * Prints general statistics about the given board, such as
     * the number of filled cells, empty cells, or other metrics.
     * @param board
     */
    public void printStatistics(SudokuBoard board) {
        writer.printStatistics(board);
    }
}
