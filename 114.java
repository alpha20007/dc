import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class LoadBalancer {
    static class Worker {
        String name;
        int load;
        int jobs;
        Worker(String name) { this.name = name; this.load = 0; this.jobs = 0; }
    }

    public static void main(String[] args) {
        String csvFile = "workload_114.csv";
        List<String> jobs = new ArrayList<>();

        File file = new File(csvFile);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) jobs.add(line);
                }
            } catch (Exception e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        } else {
            jobs = Arrays.asList(
                "J101,35,UNASSIGNED,PENDING",
                "J102,50,UNASSIGNED,PENDING",
                "J103,20,UNASSIGNED,PENDING",
                "J104,40,UNASSIGNED,PENDING",
                "J105,15,UNASSIGNED,PENDING"
            );
        }

        List<Worker> workers = Arrays.asList(new Worker("Worker_1"), new Worker("Worker_2"), new Worker("Worker_3"));
        System.out.println("Incoming job allocations:");

        for (String j : jobs) {
            String[] p = j.split(",");
            String jId = p[0].trim();
            int units = Integer.parseInt(p[1].trim());

            Worker minW = workers.get(0);
            for (Worker w : workers) {
                if (w.load < minW.load) minW = w;
            }

            minW.load += units;
            minW.jobs++;
            System.out.println("Job " + jId + " (" + units + " units) -> Assigned to " + minW.name);
            System.out.print("current load per worker: ");
            for (Worker w : workers) System.out.print("[" + w.name + ": " + w.load + "] ");
            System.out.println();
        }

        System.out.println("balanced summary of tasks across all worker nodes:");
        for (Worker w : workers) {
            System.out.println(w.name + " -> Jobs: " + w.jobs + ", Total Load: " + w.load);
        }
    }
}

/*
CSV FILE NAME: workload_114.csv

Create this CSV only if the question requires an actual CSV file.

CSV COLUMNS:
job_id,compute_units,assigned_worker,status

SAMPLE CSV DATA:
job_id,compute_units,assigned_worker,status
J101,35,UNASSIGNED,PENDING
J102,50,UNASSIGNED,PENDING
J103,20,UNASSIGNED,PENDING
J104,40,UNASSIGNED,PENDING
J105,15,UNASSIGNED,PENDING

HOW TO SAVE:
If the program reads an actual CSV file, save the sample data as workload_114.csv in the same directory as this Java file.
*/
