package model;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents a standard 9x9 Sudoku board.
 *
 * <p>Conventions:
 * <ul>
 *     <li>Board size is fixed at 9x9.</li>
 *     <li>Digits 1–9 are valid values; 0 represents an empty cell.</li>
 *     <li>Row and column indices are zero-based and must be in [0, SIZE).</li>
 * </ul>
 *
 * <p>This class encapsulates Sudoku rules (row/column/subgrid constraints) so that
 * solvers can rely on its validation helpers rather than duplicating logic.
 *
 * <p><strong>Thread-safety:</strong> instances are mutable and not thread-safe.
 * Parallel solvers should work on separate copies obtained via {@link #clone()}.
 */
public class SudokuBoard implements Cloneable {

    public static final int SIZE = 9;
    public static final int SUBGRID_SIZE = 3;

    /**
     * Internal grid representation: grid[row][column].
     * 0 means empty, 1–9 are Sudoku digits.
     */
    private final int[][] grid;

    /**
     * Creates an empty 9x9 Sudoku board with all cells set to 0 (empty).
     */
    public SudokuBoard() {
        this.grid = new int[SIZE][SIZE];
    }

    /**
     * Creates a Sudoku board from a 9x9 integer array.
     *
     * <p>All values must be in the range [0, 9]. Non-zero values are treated as givens,
     * and the constructor verifies that they do not violate Sudoku rules.
     *
     * @param initialValues 9x9 array representing the initial puzzle (0 for empty)
     * @throws IllegalArgumentException if the array is not 9x9, contains invalid
     *                                  digits, or violates Sudoku constraints
     */
    public SudokuBoard(int[][] initialValues) {
        this();

        if (initialValues == null
                || initialValues.length != SIZE) {
            throw new IllegalArgumentException("Initial board must be a non-null 9x9 array.");
        }

        for (int row = 0; row < SIZE; row++) {
            if (initialValues[row] == null || initialValues[row].length != SIZE) {
                throw new IllegalArgumentException("Initial board must be a non-null 9x9 array.");
            }
            for (int col = 0; col < SIZE; col++) {
                int value = initialValues[row][col];
                validateDigitRange(value);
                if (value != 0) {
                    // Ensure the given does not violate Sudoku constraints
                    if (!isValidMove(row, col, value)) {
                        throw new IllegalArgumentException(
                                String.format(
                                        "Initial puzzle is invalid: digit %d at (%d, %d) violates Sudoku rules.",
                                        value, row, col));
                    }
                    grid[row][col] = value;
                }
            }
        }
    }

    /**
     * Copy constructor. Creates a deep copy of another SudokuBoard.
     *
     * @param other the board to copy
     */
    public SudokuBoard(SudokuBoard other) {
        if (other == null) {
            throw new IllegalArgumentException("Other board must not be null.");
        }
        this.grid = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            System.arraycopy(other.grid[row], 0, this.grid[row], 0, SIZE);
        }
    }

    /**
     * Returns the value stored at the given cell.
     *
     * @param row    zero-based row index in [0, SIZE)
     * @param column zero-based column index in [0, SIZE)
     * @return value at the cell (0 for empty, 1–9 for digits)
     * @throws IllegalArgumentException if indices are out of bounds
     */
    public int getValue(int row, int column) {
        validateCoordinates(row, column);
        return grid[row][column];
    }

    /**
     * Sets the value at the given cell.
     *
     * <p>For non-zero values, this method enforces Sudoku rules and throws an
     * exception if the move would create a conflict. A value of 0 always clears
     * the cell and is allowed.
     *
     * @param row    zero-based row index in [0, SIZE)
     * @param column zero-based column index in [0, SIZE)
     * @param value  0 (empty) or digit 1–9
     * @throws IllegalArgumentException if indices are out of bounds or the value
     *                                  is invalid for Sudoku rules
     */
    public void setValue(int row, int column, int value) {
        validateCoordinates(row, column);
        validateDigitRange(value);

        if (value != 0 && !isValidMove(row, column, value)) {
            throw new IllegalArgumentException(
                    String.format(
                            "Invalid move: digit %d at (%d, %d) violates Sudoku rules.",
                            value, row, column));
        }

        grid[row][column] = value;
    }

    /**
     * Clears the given cell by setting its value to 0.
     *
     * @param row    zero-based row index in [0, SIZE)
     * @param column zero-based column index in [0, SIZE)
     */
    public void clearCell(int row, int column) {
        validateCoordinates(row, column);
        grid[row][column] = 0;
    }

    /**
     * Returns {@code true} if the given cell currently holds 0 (empty).
     *
     * @param row    zero-based row index in [0, SIZE)
     * @param column zero-based column index in [0, SIZE)
     * @return true if the cell is empty, false otherwise
     */
    public boolean isCellEmpty(int row, int column) {
        validateCoordinates(row, column);
        return grid[row][column] == 0;
    }

    /**
     * Checks whether placing {@code value} at ({@code row}, {@code column})
     * would be valid under Sudoku rules (no duplicate in row, column, or 3x3 subgrid).
     *
     * <p>This method ignores whatever value is currently stored in that cell, so
     * it can be safely used both for checking initial givens and for backtracking.
     *
     * @param row    zero-based row index in [0, SIZE)
     * @param column zero-based column index in [0, SIZE)
     * @param value  candidate digit (1–9); 0 is never considered a valid "move"
     * @return true if placing the given digit would be valid; false otherwise
     */
    public boolean isValidMove(int row, int column, int value) {
        validateCoordinates(row, column);

        if (value < 1 || value > SIZE) {
            return false; // 0 is not a valid "move"; it's a clear operation
        }

        // Check row
        for (int c = 0; c < SIZE; c++) {
            if (c != column && grid[row][c] == value) {
                return false;
            }
        }

        // Check column
        for (int r = 0; r < SIZE; r++) {
            if (r != row && grid[r][column] == value) {
                return false;
            }
        }

        // Check 3x3 subgrid
        int subgridRowStart = (row / SUBGRID_SIZE) * SUBGRID_SIZE;
        int subgridColStart = (column / SUBGRID_SIZE) * SUBGRID_SIZE;

        for (int r = subgridRowStart; r < subgridRowStart + SUBGRID_SIZE; r++) {
            for (int c = subgridColStart; c < subgridColStart + SUBGRID_SIZE; c++) {
                if ((r != row || c != column) && grid[r][c] == value) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns {@code true} if the board is completely filled (no zeros) and
     * all Sudoku constraints are satisfied.
     *
     * @return true if the board represents a valid complete solution
     */
    public boolean isComplete() {
        // Check for any empty cells first
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] == 0) {
                    return false;
                }
            }
        }

        // Check all rows
        for (int row = 0; row < SIZE; row++) {
            if (!isRowValid(row)) {
                return false;
            }
        }

        // Check all columns
        for (int col = 0; col < SIZE; col++) {
            if (!isColumnValid(col)) {
                return false;
            }
        }

        // Check all 3x3 subgrids
        for (int row = 0; row < SIZE; row += SUBGRID_SIZE) {
            for (int col = 0; col < SIZE; col += SUBGRID_SIZE) {
                if (!isSubgridValid(row, col)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns the position of the next empty cell (searching row by row, left to right),
     * or an empty Optional if the board has no empty cells.
     *
     * <p>This helper is convenient for backtracking solvers.
     *
     * @return an Optional containing the next empty cell position, or empty if none
     */
    public Optional<CellPosition> findNextEmptyCell() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] == 0) {
                    return Optional.of(new CellPosition(row, col));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Creates a deep copy of this board.
     *
     * <p>Intended to be used by both sequential and parallel solvers when exploring
     * alternative branches.
     *
     * @return a deep copy of this instance
     */
    @Override
    public SudokuBoard clone() {
        return new SudokuBoard(this);
    }

    /**
     * Returns a deep copy of the underlying 9x9 grid.
     *
     * @return a new 9x9 int array containing the board values
     */
    public int[][] toArray() {
        int[][] copy = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            System.arraycopy(grid[row], 0, copy[row], 0, SIZE);
        }
        return copy;
    }

    private void validateCoordinates(int row, int column) {
        if (row < 0 || row >= SIZE || column < 0 || column >= SIZE) {
            throw new IllegalArgumentException(
                    String.format(
                            "Row and column indices must be in [0, %d). Got row=%d, column=%d.",
                            SIZE, row, column));
        }
    }

    private void validateDigitRange(int value) {
        if (value < 0 || value > SIZE) {
            throw new IllegalArgumentException(
                    String.format("Cell value must be in [0, %d]. Got: %d.", SIZE, value));
        }
    }

    /**
     * Validates that the given row contains each digit at most once and no invalid digits.
     * Zeros are allowed (for partially-filled boards).
     */
    private boolean isRowValid(int row) {
        boolean[] seen = new boolean[SIZE + 1]; // index 1-9 used
        for (int col = 0; col < SIZE; col++) {
            int value = grid[row][col];
            if (value == 0) {
                return false; // for isComplete(), rows must be fully filled
            }
            if (value < 1 || value > SIZE || seen[value]) {
                return false;
            }
            seen[value] = true;
        }
        return true;
    }

    /**
     * Validates that the given column contains each digit at most once and no invalid digits.
     */
    private boolean isColumnValid(int column) {
        boolean[] seen = new boolean[SIZE + 1];
        for (int row = 0; row < SIZE; row++) {
            int value = grid[row][column];
            if (value == 0) {
                return false;
            }
            if (value < 1 || value > SIZE || seen[value]) {
                return false;
            }
            seen[value] = true;
        }
        return true;
    }

    /**
     * Validates that the 3x3 subgrid starting at (startRow, startCol) is valid.
     */
    private boolean isSubgridValid(int startRow, int startCol) {
        boolean[] seen = new boolean[SIZE + 1];
        for (int row = startRow; row < startRow + SUBGRID_SIZE; row++) {
            for (int col = startCol; col < startCol + SUBGRID_SIZE; col++) {
                int value = grid[row][col];
                if (value == 0) {
                    return false;
                }
                if (value < 1 || value > SIZE || seen[value]) {
                    return false;
                }
                seen[value] = true;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int row = 0; row < SIZE; row++) {
            if (row > 0 && row % SUBGRID_SIZE == 0) {
                builder.append("------+------+------").append(System.lineSeparator());
            }

            for (int col = 0; col < SIZE; col++) {
                if (col > 0 && col % SUBGRID_SIZE == 0) {
                    builder.append("|");
                }
                int value = grid[row][col];
                builder.append(value == 0 ? "." : Integer.toString(value));
                if (col < SIZE - 1) {
                    builder.append(" ");
                }
            }
            if (row < SIZE - 1) {
                builder.append(System.lineSeparator());
            }
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SudokuBoard that = (SudokuBoard) obj;
        return Arrays.deepEquals(this.grid, that.grid);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }
}