package gui;

import io.SudokuIO;
import model.SudokuBoard;
import solver.SequentialSudokuSolver;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * Small Swing GUI for the Sudoku project.
 * Features: load puzzle from file, edit board, solve (sequential solver).
 */
public class SudokuGui extends JFrame {

    private static final int SIZE = SudokuBoard.SIZE;

    private final JTextField[][] cells = new JTextField[SIZE][SIZE];
    private final JLabel statusLabel = new JLabel(" ");
    // tracking solve performance
    private final JLabel avgTimeLabel = new JLabel("Avg solve time: -");
    private long totalSolveNanos = 0L;
    private int solveCount = 0;

    public SudokuGui() {
        super("Sudoku Solver");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createUi();
        pack();
        setLocationRelativeTo(null);
    }

    private void createUi() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 6, 6));

        // build 3x3 boxes each containing 3x3 inner grid
        for (int br = 0; br < 3; br++) {
            for (int bc = 0; bc < 3; bc++) {
                JPanel box = new JPanel(new GridLayout(3, 3));
                box.setBorder(new LineBorder(Color.DARK_GRAY, 1));

                for (int r = 0; r < 3; r++) {
                    for (int c = 0; c < 3; c++) {
                        int row = br * 3 + r;
                        int col = bc * 3 + c;
                        JTextField tf = new JTextField(1);
                        tf.setHorizontalAlignment(JTextField.CENTER);
                        tf.setFont(tf.getFont().deriveFont(Font.BOLD, 20f));
                        tf.setPreferredSize(new Dimension(42, 42));
                        cells[row][col] = tf;
                        box.add(tf);
                    }
                }

                boardPanel.add(box);
            }
        }

        root.add(boardPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));

        JButton loadBtn = new JButton("Load file");
        JButton sampleBtn = new JButton("Load sample");
        JButton solveBtn = new JButton("Solve");
        JButton clearBtn = new JButton("Clear");

        loadBtn.addActionListener(this::onLoadFile);
        sampleBtn.addActionListener(e -> loadSample());
        solveBtn.addActionListener(this::onSolve);
        clearBtn.addActionListener(e -> clearBoard());

        controlPanel.add(loadBtn);
        controlPanel.add(sampleBtn);
        controlPanel.add(solveBtn);
        controlPanel.add(clearBtn);

        JPanel south = new JPanel(new BorderLayout());
        south.add(controlPanel, BorderLayout.CENTER);

        JPanel stats = new JPanel(new BorderLayout());
        avgTimeLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        stats.add(avgTimeLabel, BorderLayout.WEST);
        stats.add(statusLabel, BorderLayout.CENTER);

        south.add(stats, BorderLayout.SOUTH);

        root.add(south, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void onLoadFile(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(new File("."));
        int res = chooser.showOpenDialog(this);
        if (res != JFileChooser.APPROVE_OPTION)
            return;
        File file = chooser.getSelectedFile();
        try {
            SudokuBoard board = new SudokuIO().loadPuzzle(file.getAbsolutePath());
            loadBoardToUi(board);
            setStatus("Loaded: " + file.getName());
            // Leave lastSolved out — saving removed
        } catch (IOException ex) {
            showError("Failed to load puzzle: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            showError("Invalid puzzle file: " + ex.getMessage());
        }
    }

    private void loadSample() {
        // sample puzzle included in repository under SudokuProject(Correct
        // Structure)puzzles/sample_easy.txt
        try {
            String path = "SudokuProject(Correct Structure)/puzzles/sample_easy.txt";
            SudokuBoard board = new SudokuIO().loadPuzzle(path);
            loadBoardToUi(board);
            setStatus("Loaded sample puzzle");
            // saving feature removed
        } catch (IOException | IllegalArgumentException ex) {
            showError("Failed to load sample: " + ex.getMessage());
        }
    }

    private void loadBoardToUi(SudokuBoard board) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int v = board.getValue(r, c);
                cells[r][c].setText(v == 0 ? "" : Integer.toString(v));
            }
        }
        highlightConflicts();
    }

    private void onSolve(ActionEvent e) {
        try {
            int[][] grid = readGridFromUi();
            SequentialSudokuSolver solver = new SequentialSudokuSolver();
            long start = System.nanoTime();
            boolean solved = solver.solve(grid);
            long duration = System.nanoTime() - start;

            // update average timing stats
            totalSolveNanos += duration;
            solveCount++;
            double avgMs = (totalSolveNanos / (double) solveCount) / 1_000_000.0;

            if (solved) {
                SudokuBoard result = new SudokuBoard(grid);
                loadBoardToUi(result);
                setStatus(String.format("Solved ✓ (last: %.2f ms)", duration / 1_000_000.0));
            } else {
                setStatus(String.format("No solution found (last: %.2f ms)", duration / 1_000_000.0));
            }

            avgTimeLabel.setText(String.format("Avg solve time: %.2f ms", avgMs, solveCount));
        } catch (IllegalArgumentException ex) {
            showError("Invalid board: " + ex.getMessage());
        }
    }

    // save functionality removed from GUI intentionally

    private int[][] readGridFromUi() {
        int[][] grid = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                String t = cells[r][c].getText().trim();
                if (t.isEmpty())
                    grid[r][c] = 0;
                else {
                    if (t.length() > 1)
                        throw new IllegalArgumentException("Only single digits allowed");
                    char ch = t.charAt(0);
                    if (!Character.isDigit(ch))
                        throw new IllegalArgumentException("Non-digit in cell at " + (r + 1) + "," + (c + 1));
                    int v = Character.getNumericValue(ch);
                    if (v < 0 || v > 9)
                        throw new IllegalArgumentException("Value out of range at " + (r + 1) + "," + (c + 1));
                    grid[r][c] = v;
                }
            }
        }
        return grid;
    }

    private void clearBoard() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                cells[r][c].setText("");
        setStatus("Cleared");
    }

    private void highlightConflicts() {
        // build a board from UI but ignore empty cells when checking conflicts
        int[][] board = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++) {
                String t = cells[r][c].getText().trim();
                board[r][c] = t.isEmpty() ? 0 : Integer.parseInt(t);
            }

        SudokuBoard model;
        try {
            model = new SudokuBoard(board);
        } catch (IllegalArgumentException ex) {
            // if the board is invalid when viewed as givens, continue with a blank model
            model = new SudokuBoard();
        }

        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++) {
                JTextField tf = cells[r][c];
                String t = tf.getText().trim();
                if (t.isEmpty()) {
                    tf.setBackground(Color.WHITE);
                    continue;
                }
                try {
                    int v = Integer.parseInt(t);
                    boolean ok = model.isValidMove(r, c, v);
                    tf.setBackground(ok ? Color.WHITE : new Color(255, 200, 200));
                } catch (Throwable ex) {
                    tf.setBackground(new Color(255, 230, 200));
                }
            }
    }

    private void setStatus(String s) {
        statusLabel.setText(s);
    }

    private void showError(String s) {
        JOptionPane.showMessageDialog(this, s, "Error", JOptionPane.ERROR_MESSAGE);
        setStatus("Error: " + s);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SudokuGui g = new SudokuGui();
            g.setVisible(true);
        });
    }
}
