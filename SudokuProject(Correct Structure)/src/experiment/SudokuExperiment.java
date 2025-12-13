package experiment;

import io.SudokuIO;
import solver.SudokuSolver;
import solver.SequentialSudokuSolver;
import solver.ParallelSudokuSolver;
import model.SudokuBoard;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SudokuExperiment {



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

                Path path = Paths.get(file);
                String fileName = path.getFileName().toString();
                String difficulty = fileName.replace(".txt", "");
                System.out.println(difficulty);
                SudokuBoard board = io.loadPuzzle("" + file);

                SudokuSolver seqSolver = new SequentialSudokuSolver();
                int[][] seqBoard = board.toArray(); // Use toArray() method
                long startSeq = System.currentTimeMillis();
                boolean seqSuccess = seqSolver.solve(seqBoard);
                long endSeq = System.currentTimeMillis();

                SudokuSolver parSolver = new ParallelSudokuSolver(1);
                int[][] parBoard = board.toArray(); // Use toArray() method
                long startPar = System.currentTimeMillis();
                boolean parSuccess = parSolver.solve(parBoard);
                long endPar = System.currentTimeMillis();

                SudokuSolver parSolver2 = new ParallelSudokuSolver(2);
                int[][] parBoard2 = board.toArray(); // Use toArray() method
                long startPar2 = System.currentTimeMillis();
                boolean parSuccess2 = parSolver.solve(parBoard2);
                long endPar2 = System.currentTimeMillis();

                SudokuSolver parSolver3 = new ParallelSudokuSolver(3);
                int[][] parBoard3 = board.toArray(); // Use toArray() method
                long startPar3 = System.currentTimeMillis();
                boolean parSuccess3 = parSolver.solve(parBoard3);
                long endPar3 = System.currentTimeMillis();
                ResultRecord r = new ResultRecord();
                r.puzzleName = file;
                r.difficulty = difficulty;
                r.sequentialTime = endSeq - startSeq;
                r.parallelTime = endPar - startPar;
                r.parallelTime2 = endPar2 - startPar2;
                r.parallelTime3 = endPar3 - startPar3;

                results.add(r);

                System.out.println("  Sequential: " + r.sequentialTime + " ms (solved: " + seqSuccess + ")");
                System.out.println("  Parallel (depth=1):   " + r.parallelTime + " ms (solved: " + parSuccess + ")");
                System.out.println("  Parallel(depth=2):   " + r.parallelTime2+ " ms (solved: " + parSuccess2 + ")");
                System.out.println("  Parallel(depth=3):   " + r.parallelTime3 + " ms (solved: " + parSuccess3 + ")");
                System.out.println("  Speedup:    " + String.format("%.2f", r.getSpeedup()) + "\n");


                output.append("  Sequential: ").append(r.sequentialTime).append(" ms (solved: ").append(seqSuccess)
                        .append(")\n");
                output.append("  Parallel(depth=1) :   ").append(r.parallelTime).append(" ms (solved: ").append(parSuccess)
                        .append(")\n");
                output.append("  Parallel(depth=2) :   ").append(r.parallelTime2).append(" ms (solved: ").append(parSuccess)
                        .append(")\n");
                output.append("  Parallel (depth=3) :   ").append(r.parallelTime3).append(" ms (solved: ").append(parSuccess)
                        .append(")\n");
                output.append("  Speedup:    ").append(String.format("%.2f", r.getSpeedup())).append("x\n\n");

            } catch (Exception e) {
                output.append("Error processing puzzle: ").append(file).append("\n");

            }
        }


        exporter.exportCSV(results, "SudokuProject(Correct Structure)\\results\\runtime_comparison.csv");
        exporter.exportSummary(results, "SudokuProject(Correct Structure)\\results\\experiment_summary.txt");

        output.append("All experiments completed!\n");
        output.append("Results saved to results/ runtime_comparison.csv and experiment_summary.txt\n");

        return output.toString();
    }

    public static void main(String[] args) {
        SudokuExperiment experiment = new SudokuExperiment();
        experiment.runAndGetOutput();

    }
}