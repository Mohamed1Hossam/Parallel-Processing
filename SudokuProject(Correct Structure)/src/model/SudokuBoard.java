package model;

import java.util.Arrays;
import java.util.Optional;

public class SudokuBoard implements Cloneable {

    public static final int SIZE = 9;
    public static final int SUBGRID_SIZE = 3;

    private final int[][] grid;

    public SudokuBoard() {
        this.grid = new int[SIZE][SIZE];
    }

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


    public SudokuBoard(SudokuBoard other) {
        if (other == null) {
            throw new IllegalArgumentException("Other board must not be null.");
        }
        this.grid = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            System.arraycopy(other.grid[row], 0, this.grid[row], 0, SIZE);
        }
    }


    public int getValue(int row, int column) {
        validateCoordinates(row, column);
        return grid[row][column];
    }


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


    public void clearCell(int row, int column) {
        validateCoordinates(row, column);
        grid[row][column] = 0;
    }


    public boolean isCellEmpty(int row, int column) {
        validateCoordinates(row, column);
        return grid[row][column] == 0;
    }


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

    @Override
    public SudokuBoard clone() {
        return new SudokuBoard(this);
    }


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