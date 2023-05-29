import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class BDD {
    BDDNode root;
    String order;
    int count_of_nodes = 2;
    int count_of_reduced_nodes = 0;
    int myReductionLow = 0;
    int myReductionHigh = 0;
    BDDNode[] ZeroAndOne = {new BDDNode("0"), new BDDNode("1")};
    HashMap<String,BDDNode> lReduction = new HashMap<>();

    public String negation(String variable){
        StringBuilder find = new StringBuilder(variable);

        if(find.indexOf("!0") != -1){
            find.replace(find.indexOf("!0"),find.indexOf("!0")+2,"1");
        }
        else if(find.indexOf("!1") != -1){
            find.replace(find.indexOf("!1"),find.indexOf("!1")+2,"0");
        }
        return find.toString();
    }

    public void print() {
        print(root, "", true);
    }

    private void print(BDDNode node, String index, boolean bol) {
        System.out.println(index + (bol ? "└── " : "├── ") + node.variable);
        if (node.high != null) {
            print(node.high, index + (bol ? "    " : "│   "), node.low == null);
        }
        if (node.low != null) {
            print(node.low, index + (bol ? "    " : "│   "), true);
        }
    }

    public void create(String variable, String orderInput){
        root = createNode(variable, orderInput);
        //System.out.println(variable);
        order = orderInput;
    }

    public BDDNode createNode(String bfunction, String order) {
        BDDNode bdd = new BDDNode(bfunction);
        lReduction.put(bfunction, bdd);

        if (order.length() == 0) {
            return bdd;
        }

        String[] temp = bfunction.split("\\+");
        String choose = "" + order.charAt(0);
        order = order.replace(choose, "");
        StringBuilder high = new StringBuilder(); // 1
        StringBuilder low = new StringBuilder();  // 0

        for (String i : temp) {
            i = i.replace(choose, "1");
            if(i.equals("1")) myReductionHigh++;
            high.append(negation(i)).append("+");

            i = i.replace("1", "0");
            if(i.equals("!0")) myReductionLow++;
            low.append(negation(i)).append("+");
        }

        high = deleteVariablesWithZero(high);
        low = deleteVariablesWithZero(low);

        if (high.length() > 0 && high.charAt(high.length() - 1) == '+') {
            high.deleteCharAt(high.length() - 1);
        }

        if (low.length() > 0 && low.charAt(low.length() - 1) == '+') {
            low.deleteCharAt(low.length() - 1);
        }
        // remove 1
        while (high.indexOf("1") != -1 && high.length() > 1) {
            if (high.indexOf("1") != -1) {
                high.replace(high.indexOf("1"), high.indexOf("1") + 1, "");
            }
        }

        while (low.indexOf("1") != -1 && low.length() > 1) {
            if (low.indexOf("1") != -1) {
                low.replace(low.indexOf("1"), low.indexOf("1") + 1, "");
            }
        }

        if(myReductionHigh != 0){
            high = new StringBuilder("1");
            myReductionHigh = 0;
        }
        if(myReductionLow != 0){
            low = new StringBuilder("1");
            myReductionLow = 0;
        }

        //System.out.println(low + " - " +  high);

        if(high.toString().equals(low.toString()) && !high.toString().equals("1") && !high.toString().equals("0")){
            bdd.high = createNode(high.toString(), order);
            bdd = bdd.high;
            count_of_reduced_nodes++;
            return bdd;
        }

        if (high.charAt(0) != '0' && high.charAt(0) != '1') {
            if(lReduction.containsKey(high.toString())){
                bdd.high = lReduction.get(high.toString());
                count_of_reduced_nodes++;
            }else {
                bdd.high = createNode(high.toString(), order);
                count_of_nodes++;
            }
        } else if (high.charAt(0) == '0') {
            bdd.high = ZeroAndOne[0];
            count_of_reduced_nodes++;
        } else if (high.charAt(0) == '1'){
            bdd.high = ZeroAndOne[1];
            count_of_reduced_nodes++;
        }

        if (low.charAt(0) != '0' && low.charAt(0) != '1') {
            if(lReduction.containsKey(low.toString())){
                bdd.low = lReduction.get(low.toString());
                count_of_reduced_nodes++;
            }else {
                bdd.low = createNode(String.valueOf(low), order);
                count_of_nodes++;
            }
        } else if (low.charAt(0) == '0') {
            bdd.low = ZeroAndOne[0];
            count_of_reduced_nodes++;
        } else if (low.charAt(0) == '1'){
            bdd.low = ZeroAndOne[1];
            count_of_reduced_nodes++;
        }
        return bdd;
    }

    private StringBuilder deleteVariablesWithZero(StringBuilder variable) {
        int index = 0;
        boolean noZeroVariable = false;

        while (index < variable.length()) {
            int start = index;
            int end = variable.indexOf("+",start);

            if (end == -1) {
                end = variable.length();
            }
            String clause = variable.substring(start, end);
            if (clause.contains("0")) {
                variable.delete(start, end + 1);
                index = start;
            } else {
                noZeroVariable = true;
                index = end + 1;
            }
        }
        if (!noZeroVariable) {
            variable = new StringBuilder("0");
        }
        return variable;
    }

    public static String order_generator(String bfunction){
        String order = "";
        int ASCII_num;
        for (int i = 0; i < bfunction.length(); i++) {
            ASCII_num = bfunction.charAt(i);
            if (ASCII_num < 65 || ASCII_num > 90) {
                bfunction = bfunction.replace(Character.toString(bfunction.charAt(i)), "");
            }
        }

        while(bfunction.length() != 0) {
            order += bfunction.charAt(0);
            bfunction = bfunction.replace(Character.toString(bfunction.charAt(0)), "");
        }

        char[] tempArray = order.toCharArray();
        Arrays.sort(tempArray);
        return new String(tempArray);
    }

    public String createWithBestOrder(String bfunction){
        String str = order_generator(bfunction);
        String order = "";
        String[] arrayOrder = new String[9999];
        int[] arrayNodes = new int[str.length()];

        for (int i = 0; i < str.length(); i++) {
            order = str.substring(i) + str.substring(0, i);
            //System.out.println(order);
            create(bfunction,order);

            arrayOrder[i] = order;
            arrayNodes[i] = count_of_nodes;
            count_of_nodes = 2;
            count_of_reduced_nodes = 0;
            lReduction.clear();
        }

        int min = arrayNodes[0];
        int index=0;

        for(int i = 0; i < arrayNodes.length; i++) {
            if(min > arrayNodes[i]) {
                min = arrayNodes[i];
                index=i;
            }
        }
        //System.out.println(order);
        order = arrayOrder[index];
        create(bfunction, order);
        return order;
    }

    public char bddUse(String inputs){
        BDDNode node = root;
        char result;
        int i = 0;

        while (!Objects.equals(node.variable, "0") && !Objects.equals(node.variable, "1")){
            if (node.variable.contains(Character.toString(order.charAt(i)))) {
                if (inputs.charAt(i) == '0')
                    node = node.low;
                else if (inputs.charAt(i) == '1')
                    node = node.high;
            }
            i++;
        }
        result = node.variable.charAt(0);
        return result;
    }
}