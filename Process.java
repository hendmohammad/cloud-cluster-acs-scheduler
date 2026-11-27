public class Process {
    String name;
    int arrival_time;
    int burst_time;
    int priority;
    int remaining_time;
    int tier2_enter_time;         // for aging
    int eff_priority;           
    int finish_time;              // turnaround, waiting
    int first_response_time = -1; // for response time


    // getters & setters for gui table
    public String getName() {
        return name;
    }
    public int getArrival() {
        return arrival_time;
    }
    public int getBurst() {
        return burst_time;
    }
    public int getPrio() {
        return priority;
    }

    public void setName(String n){
        name = n;
    }
    public void setArrival(int a){
        arrival_time = a;
    }
    public void setBurst(int b){
        burst_time = b;
    }
    public void setPrio(int p){
        priority = p;
    }

    
    public Process(String n, int arrival, int burst, int priority){
        this.name = n;
        this.arrival_time = arrival;
        this.burst_time = burst;
        this.priority = priority;
        this.remaining_time = burst;
    }
}
