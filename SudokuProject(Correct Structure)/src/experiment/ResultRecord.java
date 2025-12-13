package experiment;

public class ResultRecord {
    public String puzzleName;
    public String difficulty;
    public long sequentialTime;
    public long parallelTime;
    public long parallelTime2;
    public long parallelTime3;
    public double getSpeedup() {
        if (parallelTime == 0) return 0;
        return (double) sequentialTime / parallelTime;
    }
}
