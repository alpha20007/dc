import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class BullyAlgorithm {
    static class Node {
        int id;
        boolean alive;
        boolean coordinator;
        Node(int id, boolean alive, boolean coordinator) {
            this.id = id; this.alive = alive; this.coordinator = coordinator;
        }
    }

    public static void main(String[] args) {
        String csvFile = "bully_cluster_125.csv";
        List<Node> nodes = new ArrayList<>();
        int oldCoordinator = 5;

        File file = new File(csvFile);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] p = line.split(",");
                    nodes.add(new Node(Integer.parseInt(p[0].trim()), Boolean.parseBoolean(p[1].trim()), Boolean.parseBoolean(p[2].trim())));
                }
            } catch (Exception e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        } else {
            nodes = Arrays.asList(
                new Node(1, true, false),
                new Node(2, true, false),
                new Node(3, true, false),
                new Node(4, false, false),
                new Node(5, false, true)
            );
        }

        System.out.println("Detected failure of coordinator: Node " + oldCoordinator);
        int initiator = 1;
        System.out.println("Initiator sending ELECTION to higher nodes from Node " + initiator);

        int currentLeader = initiator;
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            if (n.id > initiator) {
                System.out.println("Initiator sending ELECTION to Node " + n.id);
                if (n.alive) {
                    System.out.println("Node " + n.id + " responding with OK");
                    if (n.id > currentLeader) currentLeader = n.id;
                } else {
                    System.out.println("Node " + n.id + " is unresponsive");
                }
            }
        }

        System.out.println("the highest alive node broadcasting COORDINATOR: Node " + currentLeader + " is elected Leader");
    }
}

/*
CSV FILE NAME: bully_cluster_125.csv

Create this CSV only if the question requires an actual CSV file.

CSV COLUMNS:
node_id,is_alive,is_current_coordinator

SAMPLE CSV DATA:
node_id,is_alive,is_current_coordinator
1,TRUE,FALSE
2,TRUE,FALSE
3,TRUE,FALSE
4,FALSE,FALSE
5,FALSE,TRUE

HOW TO SAVE:
If the program reads an actual CSV file, save the sample data as bully_cluster_125.csv in the same directory as this Java file.
*/
