public class Generate {
    String symbols;
    String[] binary;
    public Generate(String symbols) {
        this.symbols = symbols;
    }
    public void Generate_use_BDD_input(int length) {
        int last_number = (int) Math.pow(2, length);
        binary = new String[last_number];
        String formatString = "%" + length + "s";
        for (int i = 0; i < last_number; i++) {
            binary[i] = String.format(formatString, Integer.toBinaryString(i & 0xFF)).replace(' ', '0');
        }
    }
}