package solver;

import model.SudokuBoard;
import model.CellPosition; // Added import for CellPosition
import java.util.Optional; // Added import for Optional
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask; // Added import for RecursiveTask
import java.util.concurrent.atomic.AtomicBoolean;

public class ParallelSudokuSolver implements SudokuSolver {

    private static final int DEFAULT_MAX_PARALLEL_DEPTH = 2;

    private final int maxParallelDepth;

    private static final SequentialSudokuSolver sequentialFallbackSolver = new SequentialSudokuSolver();


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

        SudokuBoard modelBoard = new SudokuBoard(board);

        AtomicBoolean solutionFound = new AtomicBoolean(false);


        SolveTask rootTask = new SolveTask(modelBoard, maxParallelDepth, 0, solutionFound);


        SudokuBoard solved = ForkJoinPool.commonPool().invoke(rootTask);

        if (solved == null) {
            return false;
        }

        int[][] solvedArray = solved.toArray();
        for (int row = 0; row < GRID_SIZE; row++) {
            System.arraycopy(solvedArray[row], 0, board[row], 0, GRID_SIZE);
        }

        return true;
    }

    @Override
    public boolean isValid(int[][] board, int row, int col, int num) {
        return sequentialFallbackSolver.isValid(board, row, col, num);
    }

    private static class SolveTask extends RecursiveTask<SudokuBoard> {

        private final int maxParallelDepth;
        private final int currentDepth;
        private final SudokuBoard board;
        private final AtomicBoolean solutionFound;

        public SolveTask(SudokuBoard board, int maxParallelDepth, int currentDepth, AtomicBoolean solutionFound) {
            this.board = board;
            this.maxParallelDepth = maxParallelDepth;
            this.currentDepth = currentDepth;
            this.solutionFound = solutionFound;
        }

        @Override
        protected SudokuBoard compute() {
            if (solutionFound.get()) {
                return null;
            }

            Optional<CellPosition> emptyCell = board.findNextEmptyCell();

            if (!emptyCell.isPresent()) {
                if (board.isComplete()) {
                    solutionFound.set(true);
                    return board;
                } else {
                    return null;
                }
            }

            CellPosition pos = emptyCell.get();
            int row = pos.getRow();
            int col = pos.getColumn();

            for (int numToTry = 1; numToTry <= SudokuBoard.SIZE; numToTry++) {
                if (solutionFound.get()) return null;

                if (board.isValidMove(row, col, numToTry)) {

                    board.setValue(row, col, numToTry);

                    if (currentDepth < maxParallelDepth) {

                        SudokuBoard nextBoard = board.clone();

                        SolveTask nextTask = new SolveTask(
                                nextBoard,
                                maxParallelDepth,
                                currentDepth + 1,
                                solutionFound
                        );

                        nextTask.fork();
                        SudokuBoard result = nextTask.join();

                        if (result != null) {
                            return result;
                        }

                    } else {

                        SequentialSudokuSolver sequentialSolver = sequentialFallbackSolver;

                        int[][] boardArray = board.toArray();

                        if (sequentialSolver.solve(boardArray)) {
                            solutionFound.set(true);
                            return new SudokuBoard(boardArray);
                        }
                    }

                    board.setValue(row, col, 0);
                }
            }
            
            return null;
        }
    }
}