import java.util.*;

public class Scheduler {

    Process[] processes;
    int quantum_time;
    int context_switching;
    Queue<Process> RRQueue = new LinkedList<>();
    PriorityQueue<Process> PrQueue = new PriorityQueue<>( (p1, p2) -> Integer.compare(p1.priority, p2.priority) );

    int i = 0;           // used to loop on all the processes in the array (track what hasn't arrived yet)
    int curr_time = 0;   // used to update time to check if it equals the arrival time of processes to enter RR_queue
    int finished_p = 0;  // to count the finished processes
    int aging_interval;

    public Scheduler(Process[] processes, int quantum, int cs) {
        this.processes = processes;
        this.quantum_time = quantum;
        this.context_switching = cs;
        this.aging_interval = quantum + cs; //initially (it is not theoretically = quantum+cs)
        Arrays.sort(processes, (p1, p2) -> Integer.compare(p1.arrival_time, p2.arrival_time));
    }


    // add all the arrived processes to RRQueue
    public void ifArrived(){
        while (i < processes.length && processes[i].arrival_time <= curr_time) {
            Process currProcess = processes[i];
            RRQueue.add(currProcess);
            i++;
        }
    }

    public void RoundRobin(){

        Process p = RRQueue.poll(); // to get the current "first" process, and remove it because if it is not finish its execution then it will be added to tier 2

        // calculating execution time of that round of the process (remaining_time instead of burst bec. it's the value to be updated for further comparison)
        int execution_time;
        if (p.remaining_time > quantum_time) {
            execution_time = quantum_time;
        }
        else {
            execution_time = p.remaining_time;
        }

        // update remaining time of the whole burst time
        p.remaining_time -= execution_time;
        p.first_response_time= curr_time; //curr_time here represents the first start time to execute the process

        curr_time += execution_time;

        // demotion logic
        if (p.remaining_time == 0){  // if remaining = 0, remove it from queue of tier 1
            p.finish_time= curr_time; //assign its finish time
            finished_p++; // increase finished processes
        }

        else {  // if not, move it to tier 2 queue >> priority , remaining
            p.tier2_enter_time = curr_time; //assign the time the process enters the Non_preemptive PriorityQueue (tier 2)
            PrQueue.add(p);
        }
    }

    public void Demotion(){

        Aging();
        Process temp = PrQueue.peek(); //initially the min effective priority process is the first
        for(Process p : PrQueue){
            if(p.effective_priority < temp.effective_priority){
                temp = p;
            }
        }
        PrQueue.remove(temp);
        curr_time += temp.remaining_time; //non-preemptive so it is granted that it will finish its remaining time
        temp.remaining_time = 0;
        temp.finish_time = curr_time;
        finished_p++;

    }

    public void Aging(){
        // aging formula: new priority = old priority - (constant * accumulated waiting time)
        //the constant here is 1/aging_intrval
        for (Process p : PrQueue){
            int waiting_time = curr_time - p.tier2_enter_time;
            int aging = waiting_time / aging_interval;
            p.effective_priority= Math.max(0 , p.priority - aging);
        }

    }

    public void Schedule(){

        while(finished_p < processes.length){

            ifArrived();

            if (!RRQueue.isEmpty()){
                RoundRobin();    // RR starts with the processes already arrived it
            }
            else if (!PrQueue.isEmpty()){
                Demotion(); // if nothing arrived RR yet, start its first process till the end
            }
            else {// if both empty, update the current time to the arrival time of next process
                curr_time = processes[i].arrival_time;
                continue;
            }
            if(!RRQueue.isEmpty() || !PrQueue.isEmpty()){ // if there's another process to execute, add the cs time
                curr_time += context_switching;
            }
        }

    }

}
