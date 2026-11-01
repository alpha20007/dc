import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

class MultithreadedLock {
    static class Task {
        String id;
        String threadName;
        int burst;
        String status;
        Task(String id, String threadName, int burst, String status) {
            this.id = id; this.threadName = threadName; this.burst = burst; this.status = status;
        }
    }

    private static final ReentrantLock mutex = new ReentrantLock();

    public static void main(String[] args) {
        String csvFile = "tasks_173.csv";
        List<Task> tasks = new ArrayList<>();

        File file = new File(csvFile);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] p = line.split(",");
                    tasks.add(new Task(p[0].trim(), p[1].trim(), Integer.parseInt(p[2].trim()), p[4].trim()));
                }
            } catch (Exception e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        } else {
            tasks = Arrays.asList(
                new Task("T1", "Worker_1", 200, "PENDING"),
                new Task("T2", "Worker_2", 150, "PENDING"),
                new Task("T3", "Worker_1", 100, "PENDING"),
                new Task("T4", "Worker_2", 250, "PENDING")
            );
        }

        List<Thread> pool = new ArrayList<>();
        for (Task t : tasks) {
            Thread th = new Thread(() -> {
                mutex.lock();
                try {
                    System.out.println("[" + Thread.currentThread().getName() + "] [LOCK ACQUIRED] Processing " + t.id);
                    t.status = "IN_PROGRESS";
                } finally {
                    mutex.unlock();
                    System.out.println("[" + Thread.currentThread().getName() + "] [LOCK RELEASED] " + t.id + " running");
                }

                try { Thread.sleep(t.burst / 10); } catch (InterruptedException ignored) {}

                mutex.lock();
                try {
                    t.status = "COMPLETED";
                    System.out.println("[" + Thread.currentThread().getName() + "] [LOCK ACQUIRED] Finished " + t.id);
                } finally {
                    mutex.unlock();
                    System.out.println("[" + Thread.currentThread().getName() + "] [LOCK RELEASED] " + t.id + " finalized");
                }
            }, t.threadName);
            pool.add(th);
        }

        for (Thread th : pool) th.start();
        for (Thread th : pool) {
            try { th.join(); } catch (InterruptedException ignored) {}
        }

        System.out.println("final completed task list:");
        for (Task t : tasks) {
            System.out.println("Task: " + t.id + " | Worker: " + t.threadName + " | Status: " + t.status);
        }
    }
}

/*
CSV FILE NAME: tasks_173.csv

Create this CSV only if the question requires an actual CSV file.

CSV COLUMNS:
task_id,thread_name,burst_ms,priority,status

SAMPLE CSV DATA:
task_id,thread_name,burst_ms,priority,status
T1,Worker_1,200,HIGH,PENDING
T2,Worker_2,150,MEDIUM,PENDING
T3,Worker_1,100,LOW,PENDING
T4,Worker_2,250,HIGH,PENDING
T5,Worker_1,180,MEDIUM,PENDING

HOW TO SAVE:
If the program reads an actual CSV file, save the sample data as tasks_173.csv in the same directory as this Java file.
*/
