public class logs {

    int time;
    String runningProcess;
    String tier1;
    String tier2;
    String note;

    public logs(int time, String runningProcess,
                        String tier1, String tier2, String note) {

        this.time = time;
        this.runningProcess = runningProcess;
        this.tier1 = tier1;
        this.tier2 = tier2;
        this.note = note;
    }
}