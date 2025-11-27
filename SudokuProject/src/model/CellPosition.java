package model;

import java.util.Objects;

/**
 * Immutable value object representing the position of a cell on a Sudoku board.
 */
public final class CellPosition {

    private final int row;
    private final int column;

    public CellPosition(int row, int column) {
        if (row < 0 || column < 0) {
            throw new IllegalArgumentException("Row and column must be non-negative.");
        }
        this.row = row;
        this.column = column;
    }

    public int getRow() { return row; }
    public int getColumn() { return column; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CellPosition)) return false;
        CellPosition that = (CellPosition) o;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "CellPosition{row=" + row + ", column=" + column + "}";
    }
}
