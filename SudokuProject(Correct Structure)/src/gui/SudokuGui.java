package gui;

import io.SudokuIO;
import model.SudokuBoard;
import solver.SequentialSudokuSolver;
import solver.ParallelSudokuSolver;
import experiment.SudokuExperiment;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * - Sudoku GUI application.
 * - Solver selection combo (Sequential / Parallel)
 * - Load sample / Load file / Clear
 */
public class SudokuGui extends JFrame {
    private static final int SIZE = 9;
    private final JTextField[][] cells = new JTextField[SIZE][SIZE];
    private final JLabel statusLabel = new JLabel(" ");

    // Solver choice UI
    private JComboBox<String> solverChoice;
    private JComboBox<String> difficultyChoice;
    private JButton solveButton;
    private JButton loadButton;
    private JButton sampleButton;
    private JButton clearButton;
    private JButton experimentButton;

    public SudokuGui() {
        super("Sudoku game");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.createUi();
        this.pack();
        this.setLocationRelativeTo((Component) null);
    }

    private void createUi() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JPanel boardContainer = new JPanel(new GridLayout(3, 3, 6, 6));

        for (int br = 0; br < 3; ++br) {
            for (int bc = 0; bc < 3; ++bc) {
                JPanel box = new JPanel(new GridLayout(3, 3));
                box.setBorder(new LineBorder(Color.DARK_GRAY, 1));

                for (int r = 0; r < 3; ++r) {
                    for (int c = 0; c < 3; ++c) {
                        int row = br * 3 + r;
                        int col = bc * 3 + c;
                        JTextField tf = new JTextField(1);
                        tf.setHorizontalAlignment(JTextField.CENTER);
                        tf.setFont(tf.getFont().deriveFont(Font.BOLD, 20.0f));
                        tf.setPreferredSize(new Dimension(42, 42));
                        this.cells[row][col] = tf;
                        box.add(tf);
                    }
                }

                boardContainer.add(box);
            }
        }

        root.add(boardContainer, BorderLayout.CENTER);

        // Controls
        JPanel controlRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        loadButton = new JButton("Load file");
        sampleButton = new JButton("Load sample");
        solveButton = new JButton("Solve");
        clearButton = new JButton("Clear");
        experimentButton = new JButton("Run Experiment");

        // Solver selection
        solverChoice = new JComboBox<>(new String[] { "Sequential Solver", "Parallel Solver" });
        controlRow.add(new JLabel("Solver:"));
        controlRow.add(solverChoice);

        // Difficulty selection
        difficultyChoice = new JComboBox<>(new String[] { "Easy", "Medium", "Hard" });
        controlRow.add(new JLabel("Difficulty:"));
        controlRow.add(difficultyChoice);

        controlRow.add(loadButton);
        controlRow.add(sampleButton);
        controlRow.add(solveButton);
        controlRow.add(clearButton);
        controlRow.add(experimentButton);

        loadButton.addActionListener(this::onLoadFile);
        sampleButton.addActionListener(e -> loadSample());
        solveButton.addActionListener(this::onSolve);
        clearButton.addActionListener(e -> clearBoard());
        experimentButton.addActionListener(this::onRunExperiment);

        JPanel south = new JPanel(new BorderLayout());
        south.add(controlRow, BorderLayout.CENTER);

        this.statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        south.add(this.statusLabel, BorderLayout.SOUTH);

        root.add(south, BorderLayout.SOUTH);

        this.setContentPane(root);
    }

    private void onLoadFile(ActionEvent ev) {
        JFileChooser chooser = new JFileChooser(new File("."));
        int res = chooser.showOpenDialog(this);
        if (res != JFileChooser.APPROVE_OPTION)
            return;
        File file = chooser.getSelectedFile();

        try {
            SudokuBoard board = (new SudokuIO()).loadPuzzle(file.getAbsolutePath());
            loadBoardToUi(board);
            setStatus("Loaded: " + file.getName());
        } catch (IOException ex) {
            showError("Failed to load puzzle: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            showError("Invalid puzzle file: " + ex.getMessage());
        }
    }

    private void loadSample() {
        try {
            String difficulty = (String) difficultyChoice.getSelectedItem();
            String fileName;
            if ("Easy".equals(difficulty)) {
                fileName = "easy.txt";
            } else if ("Medium".equals(difficulty)) {
                fileName = "medium.txt";
            } else {
                fileName = "hard.txt";
            }
            String path = "SudokuProject(Correct Structure)\\puzzles\\" + fileName;
            SudokuBoard board = (new SudokuIO()).loadPuzzle(path);
            loadBoardToUi(board);
            setStatus("Loaded " + difficulty.toLowerCase() + " puzzle");
        } catch (IllegalArgumentException | IOException ex) {
            showError("Failed to load puzzle: " + ex.getMessage());
        }
    }

    private void loadBoardToUi(SudokuBoard board) {
        for (int r = 0; r < SIZE; ++r) {
            for (int c = 0; c < SIZE; ++c) {
                int v = board.getValue(r, c);
                this.cells[r][c].setText(v == 0 ? "" : Integer.toString(v));
            }
        }
        // highlightConflicts();
    }

    /**
     * Solve button handler. Runs chosen solver in background (SwingWorker)
     * to keep UI responsive.
     */
    private void onSolve(ActionEvent ev) {
        // read grid first (validates input)
        final int[][] grid;
        try {
            grid = readGridFromUi();
        } catch (IllegalArgumentException ex) {
            showError("Invalid board: " + ex.getMessage());
            return;
        }

        // disable UI controls while solving
        setControlsEnabled(false);
        setStatus("Solving...");

        final boolean useParallel = "Parallel Solver".equals(solverChoice.getSelectedItem());

        // SwingWorker to run solver off EDT
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            private long durationNanos;

            @Override
            protected Boolean doInBackground() {
                long start = System.nanoTime();
                boolean solved;
                if (useParallel) {
                    ParallelSudokuSolver solver = new ParallelSudokuSolver();
                    solved = solver.solve(grid); // solver will mutate grid to solution
                } else {
                    SequentialSudokuSolver solver = new SequentialSudokuSolver();
                    solved = solver.solve(grid);
                }
                durationNanos = System.nanoTime() - start;
                return solved;
            }

            @Override
            protected void done() {
                try {
                    boolean solved = get();
                    double ms = durationNanos / 1_000_000.0;
                    if (solved) {
                        loadBoardToUi(new SudokuBoard(grid));
                        setStatus(String.format("Solved ✓ (%.2f ms) [%s]", ms,
                                useParallel ? "Parallel" : "Sequential"));
                    } else {
                        setStatus(String.format("No solution found (%.2f ms)", ms));
                    }
                } catch (Exception ex) {
                    showError("Solver error: " + ex.getMessage());
                } finally {
                    setControlsEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void onRunExperiment(ActionEvent ev) {
        // disable UI controls while running experiment
        setControlsEnabled(false);
        setStatus("Running experiment...");

        // SwingWorker to run experiment off EDT
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                SudokuExperiment exp = new SudokuExperiment();
                return exp.runAndGetOutput();
            }

            @Override
            protected void done() {
                try {
                    String results = get();
                    showResultsDialog(results);
                    setStatus("Experiment completed");
                } catch (Exception ex) {
                    showError("Experiment error: " + ex.getMessage());
                } finally {
                    setControlsEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private int[][] readGridFromUi() {
        int[][] grid = new int[SIZE][SIZE];

        for (int r = 0; r < SIZE; ++r) {
            for (int c = 0; c < SIZE; ++c) {
                String t = this.cells[r][c].getText().trim();
                if (t.isEmpty()) {
                    grid[r][c] = 0;
                } else {
                    if (t.length() > 1) {
                        throw new IllegalArgumentException("Only single digits allowed");
                    }
                    char ch = t.charAt(0);
                    if (!Character.isDigit(ch)) {
                        throw new IllegalArgumentException("Non-digit in cell at " + (r + 1) + "," + (c + 1));
                    }
                    int v = Character.getNumericValue(ch);
                    if (v < 0 || v > 9) {
                        throw new IllegalArgumentException("Value out of range at " + (r + 1) + "," + (c + 1));
                    }
                    grid[r][c] = v;
                }
            }
        }
        return grid;
    }

    private void clearBoard() {
        for (int r = 0; r < SIZE; ++r) {
            for (int c = 0; c < SIZE; ++c) {
                this.cells[r][c].setText("");
                this.cells[r][c].setBackground(Color.WHITE);
            }
        }
        this.setStatus("Cleared");
    }

    private void showResultsDialog(String results) {
        JDialog dialog = new JDialog(this, "Experiment Results", true);
        JTextArea textArea = new JTextArea(results);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        dialog.add(scrollPane);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // private void highlightConflicts() {
    // int[][] tmp = new int[SIZE][SIZE];

    // for (int r = 0; r < SIZE; ++r) {
    // for (int c = 0; c < SIZE; ++c) {
    // String t = this.cells[r][c].getText().trim();
    // tmp[r][c] = t.isEmpty() ? 0 : Integer.parseInt(t);
    // }
    // }

    // SudokuBoard model;
    // try {
    // model = new SudokuBoard(tmp);
    // } catch (IllegalArgumentException ex) {
    // model = new SudokuBoard();
    // }

    // for (int r = 0; r < SIZE; ++r) {
    // for (int c = 0; c < SIZE; ++c) {
    // JTextField tf = this.cells[r][c];
    // String t = tf.getText().trim();
    // if (t.isEmpty()) {
    // tf.setBackground(Color.WHITE);
    // } else {
    // try {
    // int v = Integer.parseInt(t);
    // boolean ok = model.isValidMove(r, c, v);
    // tf.setBackground(ok ? Color.WHITE : new Color(255, 200, 200));
    // } catch (Throwable ex) {
    // tf.setBackground(new Color(255, 230, 200));
    // }
    // }
    // }
    // }
    // }

    private void setControlsEnabled(boolean enabled) {
        loadButton.setEnabled(enabled);
        sampleButton.setEnabled(enabled);
        solveButton.setEnabled(enabled);
        clearButton.setEnabled(enabled);
        experimentButton.setEnabled(enabled);
        solverChoice.setEnabled(enabled);
        difficultyChoice.setEnabled(enabled);
    }

    private void setStatus(String s) {
        this.statusLabel.setText(s);
    }

    private void showError(String s) {
        JOptionPane.showMessageDialog(this, s, "Error", JOptionPane.ERROR_MESSAGE);
        this.setStatus("Error: " + s);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SudokuGui g = new SudokuGui();
            g.setVisible(true);
        });
    }
}
