package solver;

/**
 * Solver API used by all solver implementations.
 */
public interface SudokuSolver {
    int GRID_SIZE = 9;

    /**
     * Try to solve the provided board in-place.
     * @param board 9x9 sudoku board (0 = empty)
     * @return true if solved, false otherwise
     */
    boolean solve(int[][] board);

    /**
     * Check if placing num at (row, col) is valid.
     */
    boolean isValid(int[][] board, int row, int col, int num);
}
