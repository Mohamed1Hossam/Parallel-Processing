package io;

import model.SudokuBoard;
import java.io.IOException;


public class SudokuIO {
    
    private final PuzzleLoader loader;   
    private final PuzzleWriter writer;   
    
    
    public SudokuIO() {
        this.loader = new PuzzleLoader();
        this.writer = new PuzzleWriter();
    }

    
    public SudokuBoard loadPuzzle(String filename) throws IOException {
        return loader.loadFromFile(filename);
    }

    
    public SudokuBoard loadPuzzle(int[][] grid) {
        return loader.loadFromArray(grid);
    }

    
    public void printBoard(SudokuBoard board) {
        writer.printToConsole(board);
    }

    
    public void printBoard(SudokuBoard board, String title) {
        writer.printToConsole(board, title);
    }

    
    public void writeSolution(SudokuBoard board, String filename) throws IOException {
        writer.writeToFile(board, filename);
    }

    
    public String exportToString(SudokuBoard board) {
        return writer.exportToString(board);
    }

    
    public void printComparison(SudokuBoard original, SudokuBoard solved) {
        writer.printComparison(original, solved);
    }

    
    public void printStatistics(SudokuBoard board) {
        writer.printStatistics(board);
    }
}
