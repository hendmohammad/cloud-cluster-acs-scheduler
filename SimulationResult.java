public class SimulationResult {

    // to trace the queue states/output 
    public static class logs {
        int time;
        String runningProcess;
        String tier1;
        String tier2;
        String note;

        public logs(int time, String runningProcess, String tier1, String tier2, String note){
            this.time = time;
            this.runningProcess = runningProcess;
            this.tier1 = tier1;
            this.tier2 = tier2;
            this.note = note;
        }
    }

    // one block in Gantt chart
    public static class TimeSlice {
        int startTime;
        int endTime;
        String processName;

        public TimeSlice(int startTime, int endTime, String processName) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.processName = processName;
        }
    }
}

