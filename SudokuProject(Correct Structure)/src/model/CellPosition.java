package model;

import java.util.Objects;

public final class CellPosition {

    private final int row;
    private final int column;

    public CellPosition(int row, int column) {
        if (row < 0 || column < 0) {
            throw new IllegalArgumentException(
                    String.format("Row and column must be non-negative. Got row=%d, column=%d.", row, column));
        }
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }


    public int getColumn() {
        return column;
    }

    public static CellPosition of(int row, int column) {
        return new CellPosition(row, column);
    }

    public CellPosition withRow(int newRow) {
        return new CellPosition(newRow, this.column);
    }

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