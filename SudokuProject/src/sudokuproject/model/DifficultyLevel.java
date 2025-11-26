package model;

/**
 * Represents the difficulty level of a Sudoku puzzle.
 *
 * <p>This enum is intentionally simple, but it also exposes a configurable
 * {@code parallelSplitDepth} that can be used by the parallel solver to
 * decide how aggressively to fork new tasks depending on difficulty.
 */
public enum DifficultyLevel {

    /**
     * Easy puzzles: typically solved quickly by the sequential solver.
     * Using shallow parallelization is usually enough.
     */
    EASY(1),

    /**
     * Medium puzzles: moderate search depth; parallelization can help.
     */
    MEDIUM(2),

    /**
     * Hard puzzles: often require deeper search; more parallel splitting
     * may be beneficial.
     */
    HARD(3);

    /**
     * Recommended maximum decision depth (number of recursive choices) at which
     * a parallel solver should still create new tasks. After this depth,
     * solvers may switch to a purely sequential search to avoid oversubscription.
     */
    private final int parallelSplitDepth;

    DifficultyLevel(int parallelSplitDepth) {
        if (parallelSplitDepth < 0) {
            throw new IllegalArgumentException("parallelSplitDepth must be non-negative.");
        }
        this.parallelSplitDepth = parallelSplitDepth;
    }

    /**
     * Returns the recommended maximum depth for parallel task splitting
     * in a Fork/Join-based solver.
     *
     * @return non-negative depth threshold
     */
    public int getParallelSplitDepth() {
        return parallelSplitDepth;
    }

    /**
     * Parses a difficulty level from a string (case-insensitive).
     *
     * @param value textual difficulty, e.g. "easy", "MEDIUM", "Hard"
     * @return the corresponding {@link DifficultyLevel}
     * @throws IllegalArgumentException if the string does not match any level
     */
    public static DifficultyLevel fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Difficulty string must not be null.");
        }
        String normalized = value.trim().toUpperCase();
        try {
            return DifficultyLevel.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown difficulty level: " + value, ex);
        }
    }
}