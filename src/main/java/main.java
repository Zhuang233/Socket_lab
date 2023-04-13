import javax.swing.*;
import java.io.IOException;

public class main {
    public static void main(String[] argv) throws IOException, InterruptedException {
        JFrame frame = new JFrame("Java 2D Demo");
        DrawBoard panel = new DrawBoard();
        frame.add(panel);
        frame.setSize(400, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ComTable table = new ComTable();
        Node A = new Node(panel,table,"127.0.0.1","22221","A","0","5");
        Node B = new Node(panel,table,"127.0.0.1","22222","B","4","5");
        Node C = new Node(panel,table,"127.0.0.1","22223","C","8","5");
        Node D = new Node(panel,table,"127.0.0.1","22224","D","12","5");



        Thread.sleep(1500000);
    }
}
