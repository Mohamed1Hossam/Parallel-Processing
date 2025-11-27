package model;

/**
 * Represents the difficulty level of a Sudoku puzzle.
 */
public enum DifficultyLevel {
    EASY(1), MEDIUM(2), HARD(3);

    private final int parallelSplitDepth;

    DifficultyLevel(int parallelSplitDepth) {
        if (parallelSplitDepth < 0) {
            throw new IllegalArgumentException("parallelSplitDepth must be non-negative.");
        }
        this.parallelSplitDepth = parallelSplitDepth;
    }

    public int getParallelSplitDepth() {
        return parallelSplitDepth;
    }
}
