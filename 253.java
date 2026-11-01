import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class BerkeleyClock {
    static class NodeClock {
        String id;
        long timeSec;
        boolean faulty;
        NodeClock(String id, long timeSec, boolean faulty) {
            this.id = id; this.timeSec = timeSec; this.faulty = faulty;
        }
    }

    public static void main(String[] args) {
        String csvFile = "node_clocks_253.csv";
        List<NodeClock> nodes = new ArrayList<>();

        File file = new File(csvFile);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] p = line.split(",");
                    nodes.add(new NodeClock(p[0].trim(), Long.parseLong(p[1].trim()), Boolean.parseBoolean(p[2].trim())));
                }
            } catch (Exception e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        } else {
            nodes = Arrays.asList(
                new NodeClock("N1", 3600, false),
                new NodeClock("N2", 3620, false),
                new NodeClock("N3", 3580, false),
                new NodeClock("N4", 4200, true)
            );
        }

        System.out.println("Polled node times:");
        for (NodeClock n : nodes) {
            System.out.println("Node " + n.id + ": " + n.timeSec + " s" + (n.faulty ? " (Faulty)" : ""));
        }

        List<NodeClock> valid = new ArrayList<>();
        for (NodeClock n : nodes) {
            if (n.faulty) {
                System.out.println("identified and discarded outlier times: Node " + n.id + " (" + n.timeSec + " s)");
            } else {
                valid.add(n);
            }
        }

        long sum = 0;
        for (NodeClock n : valid) sum += n.timeSec;
        long avg = valid.isEmpty() ? 0 : sum / valid.size();
        System.out.println("calculated average time: " + avg + " s");

        System.out.println("relative adjustment per node:");
        for (NodeClock n : valid) {
            long diff = avg - n.timeSec;
            String sign = diff >= 0 ? "+" : "";
            System.out.println("Node " + n.id + " offset: " + sign + diff + " s (Synced: " + (n.timeSec + diff) + " s)");
        }
    }
}

/*
CSV FILE NAME: node_clocks_253.csv

Create this CSV only if the question requires an actual CSV file.

CSV COLUMNS:
node_id,clock_time_sec,is_faulty

SAMPLE CSV DATA:
node_id,clock_time_sec,is_faulty
N1,3600,FALSE
N2,3620,FALSE
N3,3580,FALSE
N4,4200,TRUE
N5,3610,FALSE

HOW TO SAVE:
If the program reads an actual CSV file, save the sample data as node_clocks_253.csv in the same directory as this Java file.
*/
