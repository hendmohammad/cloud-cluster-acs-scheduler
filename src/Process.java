public class Process {
    String name;
    int arrival_time;
    int burst_time;
    int priority;
    int remaining_time;
    int tier2_enter_time;         // for aging
    int first_response_time = -1; // for response time
    int finish_time;              // turnaround, waiting
    int effective_priority;
    public Process(String n, int arrival, int burst, int priority){
        this.name = n;
        this.arrival_time = arrival;
        this.burst_time = burst;
        this.priority = priority;
        this.remaining_time = burst;
        this.effective_priority = priority;
    }
}
