public class BDDNode {
    String variable;
    BDDNode low;   //0
    BDDNode high;  //1

    public BDDNode(String variable) {
        this.variable = variable;
    }
}

