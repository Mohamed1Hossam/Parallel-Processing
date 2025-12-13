package experiment;

import java.io.*;
import java.util.List;

public class ExperimentTableExporter {


    public void exportCSV(List<ResultRecord> results, String filename) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filename));
            writer.println("Difficulty,Sequential(ms),Parallel(depth=1),Parallel(depth=2),Parallel(depth=3) ,Speedup");

            for (int i = 0; i < results.size(); i++) {
                ResultRecord r = results.get(i);
                writer.println( r.difficulty + "," +
                               r.sequentialTime + "," + r.parallelTime + "," + r.parallelTime2 +","+ r.parallelTime3+","+
                               r.getSpeedup());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void exportSummary(List<ResultRecord> results, String filename) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filename));
            long totalSeq = 0;
            long totalPar = 0;

            for (int i = 0; i < results.size(); i++) {
                ResultRecord r = results.get(i);
                totalSeq += r.sequentialTime;
                totalPar += r.parallelTime;
            }

            double avgSeq = (double) totalSeq / results.size();
            double avgPar = (double) totalPar / results.size();

            writer.println("=== Experiment Summary ===");
            writer.println("Average Sequential Time: " + avgSeq + " ms");
            writer.println("Average Parallel Time:   " + avgPar + " ms");
            writer.println("Average Speedup:         " + (avgSeq / avgPar));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
