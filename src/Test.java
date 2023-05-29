import java.util.*;

public class Test {
        public static void main(String[] args) {
                while (true) {
                        double count_of_nodes_best = 0, count_of_nodes_ABCD = 0;
                        System.out.println("\n1. Testing with random data-set for documenation.");
                        System.out.println("2. Testing with data-set from console.");

                        Scanner scan = new Scanner(System.in);
                        int decide = scan.nextInt();
                        switch (decide) {
                                case 1 -> {
                                        Scanner scanner = new Scanner(System.in);
                                        int num = 100;

                                        String[][] tempMasive = new String[num][2];
                                        int NUM_VARIABLES = scanner.nextInt();

                                        for (int l = 0; l < num; l++) {
                                                Random random = new Random();

                                                //int count = 5;
                                                int count = random.nextInt(6) + 10;

                                                Random rand = new Random();
                                                StringBuilder sb = new StringBuilder();

                                                for (int i = 0; i < count; i++) {
                                                        StringBuilder term = new StringBuilder();

                                                        for (int j = 0; j < NUM_VARIABLES; j++) {
                                                                if (rand.nextBoolean()) {
                                                                        if (rand.nextBoolean()) {
                                                                                term.append('!').append((char) ('A' + j));
                                                                        } else {
                                                                                term.append((char) ('A' + j));
                                                                        }
                                                                }
                                                        }
                                                        sb.append(term);

                                                        if (i != count - 1 && sb.length() > 0) {
                                                                if (sb.charAt(sb.length() - 1) != '+')
                                                                        sb.append("+");
                                                        }
                                                }

                                                while (sb.charAt(sb.length() - 1) == '+') {
                                                        sb.deleteCharAt(sb.length() - 1);
                                                }

                                                StringBuilder order = new StringBuilder();
                                                for (int i = 0; i < NUM_VARIABLES; i++) {
                                                        char variable = (char) ('A' + i);
                                                        order.append(variable);
                                                }
                                                tempMasive[l][0] = sb.toString();
                                                tempMasive[l][1] = order.toString();
                                        }
                                        long startTime = System.currentTimeMillis();

                                        BDD diagram = new BDD();

                                        for (int i = 0; i < num; i++) {
                                                diagram.create(tempMasive[i][0], tempMasive[i][1]);
                                                count_of_nodes_ABCD += diagram.count_of_nodes;
                                                diagram.count_of_nodes = 0;
                                                diagram.count_of_reduced_nodes = 0;
                                                diagram.lReduction.clear();
                                        }

                                        long endTime = System.currentTimeMillis();
                                        double elapsedTime = (double) (endTime - startTime) / 1000;
                                        System.out.println("Elapsed time for \"ABCD..\" order: " + elapsedTime + " seconds");

                                        ////////////////////////////////////////////////////////////////////////////////////////////////////

                                        String[] tempMasive3 = new String[num];

                                        BDD diagram2 = new BDD();
                                        for (int i = 0; i < num; i++) {
                                                tempMasive3[i] = diagram2.createWithBestOrder(tempMasive[i][0]);
                                        }

                                        long startTime2 = System.currentTimeMillis();

                                        for (int i = 0; i < num; i++) {
                                                diagram2.create(tempMasive[i][0], tempMasive3[i]);
                                                count_of_nodes_best += diagram2.count_of_nodes;
                                                diagram2.count_of_nodes = 0;
                                                diagram2.lReduction.clear();
                                        }

                                        long endTime2 = System.currentTimeMillis();
                                        double elapsedTime2 = (double) (endTime2 - startTime2) / 1000;
                                        System.out.println("Elapsed time for the best order: " + elapsedTime2 + " seconds");
                                        ////////////////////////////////////////////////////////////////////////////////////////////////////
                                        // 2^n + 1

                                        int power = 1;
                                        int sum = 0;
                                        double final_sum = 0;

                                        for (int ko = 0; ko < 100; ko++) {
                                                int lol = tempMasive[ko][1].length();
                                                for (int i = 0; i < lol + 1; i++) {
                                                        sum += power;
                                                        power *= 2;
                                                }
                                                final_sum += sum;
                                                sum = 0;
                                                power = 1;
                                        }

                                        ////////////////////////////////////////////////////////////////////////////////////////////////////

                                        double red_perc1 = count_of_nodes_ABCD / final_sum;
                                        double red_perc2 = count_of_nodes_best / final_sum;

                                        System.out.println("Count of the nodes used to build the tree with \"ABCD..\" order: " + count_of_nodes_ABCD / 100);
                                        System.out.println("Count of the nodes used to build the tree with the best order: " + count_of_nodes_best / 100);
                                        System.out.println("Count of the nodes used to build the full tree (2^n): " + final_sum / 100);
                                        System.out.println("Reduction % for ABCD: " + (1 - red_perc1) * 100);
                                        System.out.println("Reduction % for best: " + (1 - red_perc2) * 100);
                                }
                                case 2 -> {
                                        Scanner scanner = new Scanner(System.in);
                                        String bfunction = scanner.nextLine();
                                        String order = scanner.nextLine();
                                        BDD bdd = new BDD();
                                        long startTime = System.nanoTime();

                                        bdd.create(bfunction, order);

                                        Generate generator = new Generate("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                                        generator.Generate_use_BDD_input(BDD.order_generator(bfunction).length());

                                        System.out.println("|   TREE  | THEORY  |");
                                        for (int k = 0; k < generator.binary.length; k++) {
                                                System.out.println("|    "+ bdd.bddUse(generator.binary[k]) +"    |    " + checkCorrectness(bfunction, order, generator.binary[k]) + "    |");

                                                if(bdd.bddUse(generator.binary[k]) != checkCorrectness(bfunction, order, generator.binary[k])){
                                                        System.out.println("Error!");
                                                }
                                        }
                                        System.out.println("\n");
                                        bdd.print();

                                        long endTime = System.nanoTime();
                                        double elapsedTime = (double) (endTime - startTime) / 1000000000;

                                        System.out.println("Count of reduced nodes: " + bdd.count_of_reduced_nodes);
                                        System.out.println("Elapsed time: " + elapsedTime + " seconds");
                                }
                        }
                }
        }

        private static char checkCorrectness(String bfunction, String order, String value) {
                for (int j = 0; j < order.length(); j++) {
                        if (value.charAt(j) == '1') {
                                bfunction = bfunction.replaceAll("!" + order.charAt(j), "0");
                                bfunction = bfunction.replaceAll(String.valueOf(order.charAt(j)), "1");
                        } else if (value.charAt(j) == '0') {
                                bfunction = bfunction.replaceAll("!" + order.charAt(j), "1");
                                bfunction = bfunction.replaceAll(String.valueOf(order.charAt(j)), "0");
                        }
                }
                String[] binaryNumbers = bfunction.split("\\+");
                for (String binaryNumber : binaryNumbers) {
                        for (int i = 0; i < binaryNumber.length(); i++) {
                                char c = binaryNumber.charAt(i);
                                if (c == '0') {
                                        break;
                                } else if (i == binaryNumber.length() - 1) {
                                        return '1';
                                }
                        }
                }
                return '0';
        }
}
