package experiment;

import io.SudokuIO;
import solver.SudokuSolver;
import solver.SequentialSudokuSolver;
import solver.ParallelSudokuSolver;
import model.SudokuBoard;

import java.util.ArrayList;
import java.util.List;

public class SudokuExperiment {

    public void run() {
        List<ResultRecord> results = new ArrayList<>();
        ExperimentTableExporter exporter = new ExperimentTableExporter();
        SudokuIO io = new SudokuIO();

        String[] puzzles = { "SudokuProject(Correct Structure)\\puzzles\\easy.txt",
                "SudokuProject(Correct Structure)\\puzzles\\medium.txt",
                "SudokuProject(Correct Structure)\\puzzles\\hard.txt" };

        System.out.println("Starting Sudoku Solver Experiments...\n");

        for (String file : puzzles) {
            try {
                System.out.println(file.replace(".txt", ""));

                SudokuBoard board = io.loadPuzzle("" + file);

                SudokuSolver seqSolver = new SequentialSudokuSolver();
                int[][] seqBoard = board.toArray(); // Use toArray() method
                long startSeq = System.currentTimeMillis();
                boolean seqSuccess = seqSolver.solve(seqBoard);
                long endSeq = System.currentTimeMillis();

                SudokuSolver parSolver = new ParallelSudokuSolver();
                int[][] parBoard = board.toArray(); // Use toArray() method
                long startPar = System.currentTimeMillis();
                boolean parSuccess = parSolver.solve(parBoard);
                long endPar = System.currentTimeMillis();

                ResultRecord r = new ResultRecord();
                r.puzzleName = file;
                r.difficulty = file.replace(".txt", "");
                r.sequentialTime = endSeq - startSeq;
                r.parallelTime = endPar - startPar;

                results.add(r);

                // Print results
                System.out.println("  Sequential: " + r.sequentialTime + " ms (solved: " + seqSuccess + ")");
                System.out.println("  Parallel:   " + r.parallelTime + " ms (solved: " + parSuccess + ")");
                System.out.println("  Speedup:    " + String.format("%.2f", r.getSpeedup()) + "x\n");

            } catch (Exception e) {
                System.out.println("Error processing puzzle: " + file);
                e.printStackTrace();
            }
        }

        // Export results
        exporter.exportCSV(results, "SudokuProject(Correct Structure)\\results\\runtime_comparison.csv");
        exporter.exportSummary(results, "SudokuProject(Correct Structure)\\results\\experiment_summary.txt");

        System.out.println("All experiments completed!");
        System.out.println("Results saved to results/ runtime_comparison");
    }

    public String runAndGetOutput() {
        List<ResultRecord> results = new ArrayList<>();
        ExperimentTableExporter exporter = new ExperimentTableExporter();
        SudokuIO io = new SudokuIO();

        String[] puzzles = { "SudokuProject(Correct Structure)\\puzzles\\easy.txt",
                "SudokuProject(Correct Structure)\\puzzles\\medium.txt",
                "SudokuProject(Correct Structure)\\puzzles\\hard.txt" };

        StringBuilder output = new StringBuilder();
        output.append("Starting Sudoku Solver Experiments...\n\n");

        for (String file : puzzles) {
            try {
                output.append(file.replace(".txt", "").replace("SudokuProject(Correct Structure)\\puzzles\\", ""))
                        .append("\n");

                SudokuBoard board = io.loadPuzzle("" + file);

                SudokuSolver seqSolver = new SequentialSudokuSolver();
                int[][] seqBoard = board.toArray(); // Use toArray() method
                long startSeq = System.currentTimeMillis();
                boolean seqSuccess = seqSolver.solve(seqBoard);
                long endSeq = System.currentTimeMillis();

                SudokuSolver parSolver = new ParallelSudokuSolver();
                int[][] parBoard = board.toArray(); // Use toArray() method
                long startPar = System.currentTimeMillis();
                boolean parSuccess = parSolver.solve(parBoard);
                long endPar = System.currentTimeMillis();

                ResultRecord r = new ResultRecord();
                r.puzzleName = file;
                r.difficulty = file.replace(".txt", "");
                r.sequentialTime = endSeq - startSeq;
                r.parallelTime = endPar - startPar;

                results.add(r);

                // Append results to output
                output.append("  Sequential: ").append(r.sequentialTime).append(" ms (solved: ").append(seqSuccess)
                        .append(")\n");
                output.append("  Parallel:   ").append(r.parallelTime).append(" ms (solved: ").append(parSuccess)
                        .append(")\n");
                output.append("  Speedup:    ").append(String.format("%.2f", r.getSpeedup())).append("x\n\n");

            } catch (Exception e) {
                output.append("Error processing puzzle: ").append(file).append("\n");
                // Optionally append stack trace, but for GUI, maybe not
            }
        }

        // Export results
        exporter.exportCSV(results, "SudokuProject(Correct Structure)\\results\\runtime_comparison.csv");
        exporter.exportSummary(results, "SudokuProject(Correct Structure)\\results\\experiment_summary.txt");

        output.append("All experiments completed!\n");
        output.append("Results saved to results/ runtime_comparison.csv and experiment_summary.txt\n");

        return output.toString();
    }

    public static void main(String[] args) {
        SudokuExperiment experiment = new SudokuExperiment();
        experiment.run();
    }
}