package solver.tasks;

import model.SudokuBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class SolveTask extends RecursiveTask<SudokuBoard> {

    private final SudokuBoard board;
    private final int parallelDepthRemaining;
    private final AtomicBoolean solutionFound;

    public SolveTask(SudokuBoard board,
                     int parallelDepthRemaining,
                     AtomicBoolean solutionFound) {
        this.board = board;
        this.parallelDepthRemaining = parallelDepthRemaining;
        this.solutionFound = solutionFound;
    }

    @Override
    protected SudokuBoard compute() {
        // If another task already found a solution, stop early.
        if (solutionFound.get()) {
            return null;
        }

        // If this board is already a complete valid solution, publish it.
        if (board.isComplete()) {
            solutionFound.set(true);
            return board;
        }

        // Find next empty cell (row, col). Do not rely on CellPosition to avoid coupling.
        int row = -1;
        int col = -1;
        outer:
        for (int r = 0; r < SudokuBoard.SIZE; r++) {
            for (int c = 0; c < SudokuBoard.SIZE; c++) {
                if (board.isCellEmpty(r, c)) {
                    row = r;
                    col = c;
                    break outer;
                }
            }
        }

        // No empty cell found: either solved (caught above) or invalid.
        if (row == -1) {
            return null;
        }

        // If we've exhausted the parallel depth budget, solve sequentially from here.
        if (parallelDepthRemaining <= 0) {
            boolean solved = solveSequential(board);
            if (solved) {
                solutionFound.set(true);
                return board;
            }
            return null;
        }

        // Otherwise, branch on all valid candidates in parallel.
        List<SolveTask> subtasks = new ArrayList<>();

        for (int candidate = 1; candidate <= SudokuBoard.SIZE; candidate++) {
            if (!board.isValidMove(row, col, candidate)) {
                continue;
            }

            SudokuBoard childBoard = board.clone();
            childBoard.setValue(row, col, candidate);

            SolveTask task = new SolveTask(
                    childBoard,
                    parallelDepthRemaining - 1,
                    solutionFound
            );
            subtasks.add(task);
        }

        if (subtasks.isEmpty()) {
            return null; // dead end
        }

        // Fork all but the first task for better work-stealing behavior.
        for (int i = 1; i < subtasks.size(); i++) {
            subtasks.get(i).fork();
        }

        // Compute first task in current thread.
        SudokuBoard solution = subtasks.get(0).compute();
        if (solution != null) {
            return solution;
        }

        // Join others until we find a non-null solution or all fail.
        for (int i = 1; i < subtasks.size(); i++) {
            if (solutionFound.get()) {
                // A solution was found elsewhere; we can stop checking.
                break;
            }
            SudokuBoard result = subtasks.get(i).join();
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private boolean solveSequential(SudokuBoard b) {
        if (solutionFound.get()) {
            return false;
        }

        if (b.isComplete()) {
            solutionFound.set(true);
            return true;
        }

        int row = -1;
        int col = -1;
        outer:
        for (int r = 0; r < SudokuBoard.SIZE; r++) {
            for (int c = 0; c < SudokuBoard.SIZE; c++) {
                if (b.isCellEmpty(r, c)) {
                    row = r;
                    col = c;
                    break outer;
                }
            }
        }

        if (row == -1) {
            return false;
        }

        for (int candidate = 1; candidate <= SudokuBoard.SIZE; candidate++) {
            if (solutionFound.get()) {
                return false;
            }
            if (!b.isValidMove(row, col, candidate)) {
                continue;
            }
            b.setValue(row, col, candidate);

            if (solveSequential(b)) {
                return true;
            }

            // backtrack
            b.clearCell(row, col);
        }

        return false;
    }
}
