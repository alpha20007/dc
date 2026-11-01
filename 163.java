import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

class MatrixMultiplication {
    public static void main(String[] args) {
        String csvFile = "matrix_163.csv";
        int[][] matrixA = new int[4][4];

        File file = new File(csvFile);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                int r = 0;
                while ((line = br.readLine()) != null && r < 4) {
                    if (line.trim().isEmpty()) continue;
                    String[] p = line.split(",");
                    for (int c = 0; c < 4; c++) matrixA[r][c] = Integer.parseInt(p[c + 1].trim());
                    r++;
                }
            } catch (Exception e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        } else {
            matrixA = new int[][]{
                {2, 4, 1, 5},
                {3, 1, 2, 4},
                {1, 0, 3, 2},
                {4, 2, 1, 3}
            };
        }

        int[][] matrixB = {
            {1, 2, 0, 1},
            {0, 1, 3, 2},
            {2, 0, 1, 4},
            {1, 1, 0, 2}
        };

        System.out.println("Master scattering matrix rows to worker tasks:");
        int[][] result = new int[4][4];
        Thread[] workers = new Thread[4];

        for (int i = 0; i < 4; i++) {
            final int rank = i;
            System.out.println("[MPI_SCATTER] Master scatters Row " + rank + " " + Arrays.toString(matrixA[rank]) + " to Worker " + rank);
            workers[i] = new Thread(() -> {
                for (int j = 0; j < 4; j++) {
                    int dot = 0;
                    for (int k = 0; k < 4; k++) {
                        dot += matrixA[rank][k] * matrixB[k][j];
                    }
                    result[rank][j] = dot;
                }
                System.out.println("Worker " + rank + " computing partial dot products: " + Arrays.toString(result[rank]));
            });
            workers[i].start();
        }

        for (int i = 0; i < 4; i++) {
            try { workers[i].join(); } catch (InterruptedException ignored) {}
        }

        System.out.println("master gathering the final matrix product:");
        for (int i = 0; i < 4; i++) {
            System.out.println(Arrays.toString(result[i]));
        }
    }
}

/*
CSV FILE NAME: matrix_163.csv

Create this CSV only if the question requires an actual CSV file.

CSV COLUMNS:
row_id,col_0,col_1,col_2,col_3

SAMPLE CSV DATA:
row_id,col_0,col_1,col_2,col_3
R0,2,4,1,5
R1,3,1,2,4
R2,1,0,3,2
R3,4,2,1,3

HOW TO SAVE:
If the program reads an actual CSV file, save the sample data as matrix_163.csv in the same directory as this Java file.
*/
