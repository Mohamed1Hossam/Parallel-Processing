package model;

public enum DifficultyLevel {

    EASY(1),

    MEDIUM(2),

    HARD(3);

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