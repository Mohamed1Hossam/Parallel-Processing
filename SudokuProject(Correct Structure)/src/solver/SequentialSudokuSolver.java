package solver;

public class SequentialSudokuSolver implements SudokuSolver {

    @Override
    public boolean solve(int[][] board) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] == 0) {
                    for (int numToTry = 1; numToTry <= GRID_SIZE; numToTry++) {
                        if (isValid(board, row, col, numToTry)) {
                            board[row][col] = numToTry;
                            if (solve(board)) {
                                return true;
                            } else {
                                board[row][col] = 0;
                            }
                        }
                    }
                    return false; // dead end
                }
            }
        }
        return true; // solved
    }

    @Override
    public boolean isValid(int[][] board, int row, int col, int num) {
        return !isNumberInRow(board, num, row)
                && !isNumberInColumn(board, num, col)
                && !isNumberInBox(board, num, row, col);
    }

    private boolean isNumberInRow(int[][] board, int num, int row) {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[row][i] == num) return true;
        }
        return false;
    }

    private boolean isNumberInColumn(int[][] board, int num, int col) {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[i][col] == num) return true;
        }
        return false;
    }

    private boolean isNumberInBox(int[][] board, int num, int row, int col) {
        int localRow = row - row % 3;
        int localCol = col - col % 3;
        for (int r = localRow; r < localRow + 3; r++) {
            for (int c = localCol; c < localCol + 3; c++) {
                if (board[r][c] == num) return true;
            }
        }
        return false;
    }

}
