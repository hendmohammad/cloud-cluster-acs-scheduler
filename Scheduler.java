import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Arrays; // for processes array sorting 

public class Scheduler {

    Process[] processes;
    int quantum_time;
    int context_switching;

    Queue<Process> RRQueue = new LinkedList<>();
    PriorityQueue<Process> PrQueue = new PriorityQueue<>( (p1, p2) -> Integer.compare(p1.priority, p2.priority) );

    int i = 0;           // used to loop on all the processes in the array (track what hasn't arrived yet) 
    int curr_time = 0;   // used to update time to check if it equals the arrival time of processes to enter RR_queue
    int finished_p = 0;  // to count the finished processes

    
    public Scheduler(Process[] processes, int quantum, int cs) {
        this.processes = processes;
        this.quantum_time = quantum;
        this.context_switching = cs;

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

        Process p = RRQueue.peek(); // to get the current "first" process, but don't remove it
        if (p.first_response_time == -1) {
         p.first_response_time = curr_time;
                  }//calculate first response time

        // calculating execution time of that round of the process (remaining_time instead of burst bec. it's the value to be updated for further comparison)
        int execution_time;
        if (p.remaining_time > quantum_time) {execution_time = quantum_time;}
        else {execution_time = p.remaining_time;}

        // update remaining time of the whole burst time
        p.remaining_time -= execution_time;

        // demotion logic
        if (p.remaining_time == 0){  // if remaining = 0, remove it from queue of tier 1 
            RRQueue.poll();
            finished_p++;
            p.finish_time = curr_time + execution_time;
        }   
        else {                       // if not, move it to tier 2 queue >> priority , remaining
            RRQueue.poll();
            PrQueue.add(p);
        }

        curr_time += execution_time;
               
    }

    public void Demotion(){

        Process p = PrQueue.poll();
        curr_time += p.remaining_time;
        p.remaining_time = 0;
        finished_p++;

         p.finish_time = curr_time;

    }

    public void Aging(){
        // Tier3 will be implemented later
    }

    public void Schedule(){

        while(finished_p < processes.length){

            ifArrived();

            if (!RRQueue.isEmpty()) RoundRobin();    // RR starts with the processes already arrived it 
            else if (!PrQueue.isEmpty()) Demotion(); // if nothing arrived RR yet, start its first process till the end
            else {                                   // if both empty, update the current time to the arrival time of next process
                curr_time = processes[i].arrival_time;
                continue;
            }

            if(!RRQueue.isEmpty() || !PrQueue.isEmpty()){ // if there's another process to execute, add the cs time
                curr_time += context_switching;
            }
        }
        
    }
    
}
