import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CristianClock {
    public static void main(String[] args) {
        String csvFile = "cristian_clocks_16.csv";
        List<String> records = new ArrayList<>();

        File file = new File(csvFile);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) records.add(line);
                }
            } catch (Exception e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        } else {
            records = Arrays.asList(
                "C1,1000,1050,1100",
                "C2,2000,2080,2120",
                "C3,3000,3045,3090"
            );
        }

        for (String r : records) {
            String[] p = r.split(",");
            String client = p[0].trim();
            long t0 = Long.parseLong(p[1].trim());
            long tServer = Long.parseLong(p[2].trim());
            long t1 = Long.parseLong(p[3].trim());

            long rtt = t1 - t0;
            long estimatedServerTime = tServer + (rtt / 2);
            long offset = estimatedServerTime - t1;

            System.out.println("Synchronizing Client: " + client);
            System.out.println("Calculated RTT = (T1 - T0) = (" + t1 + " - " + t0 + ") = " + rtt + " ms");
            System.out.println("estimated server time = T_server + (RTT / 2) = " + estimatedServerTime + " ms");
            System.out.println("local clock offset adjustment: " + (offset >= 0 ? "+" : "") + offset + " ms");
            System.out.println("Adjusted Client Clock: " + estimatedServerTime + " ms");
        }
    }
}

/*
CSV FILE NAME: cristian_clocks_16.csv

Create this CSV only if the question requires an actual CSV file.

CSV COLUMNS:
client_id,t0_send_ms,t_server_ms,t1_recv_ms

SAMPLE CSV DATA:
client_id,t0_send_ms,t_server_ms,t1_recv_ms
C1,1000,1050,1100
C2,2000,2080,2120
C3,3000,3045,3090
C4,4000,4030,4080
C5,5000,5060,5110

HOW TO SAVE:
If the program reads an actual CSV file, save the sample data as cristian_clocks_16.csv in the same directory as this Java file.
*/
