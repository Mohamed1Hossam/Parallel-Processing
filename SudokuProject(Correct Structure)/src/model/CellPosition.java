package model;

import java.util.Objects;

/**
 * Immutable value object representing the position of a cell on a Sudoku board.
 *
 * <p>Row and column indices are zero-based and should be in the range
 * [0, {@link SudokuBoard#SIZE}) when used with a {@link SudokuBoard}.
 */
public final class CellPosition {

    private final int row;
    private final int column;

    /**
     * Creates a new cell position.
     *
     * @param row    zero-based row index (must be >= 0)
     * @param column zero-based column index (must be >= 0)
     * @throws IllegalArgumentException if row or column is negative
     */
    public CellPosition(int row, int column) {
        if (row < 0 || column < 0) {
            throw new IllegalArgumentException(
                    String.format("Row and column must be non-negative. Got row=%d, column=%d.", row, column));
        }
        this.row = row;
        this.column = column;
    }

    /**
     * Zero-based row index.
     */
    public int getRow() {
        return row;
    }

    /**
     * Zero-based column index.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Convenience factory method.
     *
     * @param row    row index (>= 0)
     * @param column column index (>= 0)
     * @return a new {@code CellPosition} instance
     */
    public static CellPosition of(int row, int column) {
        return new CellPosition(row, column);
    }

    /**
     * Returns a new instance with the same column but a different row.
     */
    public CellPosition withRow(int newRow) {
        return new CellPosition(newRow, this.column);
    }

    /**
     * Returns a new instance with the same row but a different column.
     */
    public CellPosition withColumn(int newColumn) {
        return new CellPosition(this.row, newColumn);
    }

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
        return "CellPosition{" +
               "row=" + row +
               ", column=" + column +
               '}';
    }
}