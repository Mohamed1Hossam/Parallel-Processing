package solver;

import model.SudokuBoard;
import solver.tasks.SolveTask;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

public class ParallelSudokuSolver implements SudokuSolver {

    private static final int DEFAULT_MAX_PARALLEL_DEPTH = 2;

    private final int maxParallelDepth;

    public ParallelSudokuSolver() {
        this(DEFAULT_MAX_PARALLEL_DEPTH);
    }

    public ParallelSudokuSolver(int maxParallelDepth) {
        if (maxParallelDepth < 0) {
            throw new IllegalArgumentException("maxParallelDepth must be >= 0");
        }
        this.maxParallelDepth = maxParallelDepth;
    }

    @Override
    public boolean solve(int[][] board) {
        if (board == null) {
            throw new IllegalArgumentException("Board must not be null.");
        }

        // Wrap input in our model class (validates dimensions and givens).
        SudokuBoard modelBoard = new SudokuBoard(board);

        // Shared flag to signal that some task has already found a solution.
        AtomicBoolean solutionFound = new AtomicBoolean(false);

        // Root task
        SolveTask rootTask = new SolveTask(modelBoard, maxParallelDepth, solutionFound);

        // Use the common ForkJoinPool (no manual pool management needed).
        SudokuBoard solved = ForkJoinPool.commonPool().invoke(rootTask);

        if (solved == null) {
            return false;
        }

        // Copy solution back into the original int[][] array.
        int[][] solvedArray = solved.toArray();
        for (int row = 0; row < GRID_SIZE; row++) {
            System.arraycopy(solvedArray[row], 0, board[row], 0, GRID_SIZE);
        }

        return true;
    }

    @Override
    public boolean isValid(int[][] board, int row, int col, int num) {
        // Simple delegation to reuse existing implementation.
        return new SequentialSudokuSolver().isValid(board, row, col, num);
    }
}
