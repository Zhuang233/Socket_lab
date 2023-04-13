import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Window extends JFrame {
    Node p;
    JLabel label1;
    JLabel label2;
    JLabel label3;
    JLabel label4;
    JLabel label5;
    JLabel label6;
    JTextField port;
    JTextField X;
    JTextField Y;
    JTextField mac;
    JTextField msg;
    JTextField dest;
    JButton send;
    JTextArea log;
    JScrollPane log1;
    JButton set;
    JButton link;
    JButton clean;


    public Window(Node p){
        this.p = p;


        label1 = new JLabel("监听端口：");
        port = new JTextField(5);
//        port.setEditable(false);

        label2 = new JLabel("X:");
        X = new JTextField(3);

        label3 = new JLabel("Y:");
        Y = new JTextField(3);

        label4 = new JLabel("mac:");
        mac = new JTextField(3);

        set = new JButton("设置站点");

        link = new JButton("交流位置");

        label5 = new JLabel("目的站");
        dest = new JTextField(3);

        label6 = new JLabel("消息");
        msg = new JTextField(15);

        send = new JButton("发送");

        log = new JTextArea();
        log.setLineWrap(true);
        log.setPreferredSize(new Dimension(450,180));

        log1 = new JScrollPane(log);
        log1.setBounds(13,10,450,180);
        log1.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        clean = new JButton("清空日志");


        add(label1);
        add(port);
        add(label2);
        add(X);
        add(label3);
        add(Y);
        add(label4);
        add(mac);
        add(set);
        add(link);
        add(label5);
        add(dest);
        add(label6);
        add(msg);
        add(send);
        add(log1);
        add(clean);

        set.addActionListener(e -> {
            try {
                p.SetInfo();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        send.addActionListener(e -> {
            p.Send();
        });

        link.addActionListener(e -> {
            try {
                p.BroadcastXY();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        clean.addActionListener(e -> {
            log.setText("");
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500,400);
        setLayout(new FlowLayout(FlowLayout.CENTER,9,20));
        setVisible(true);

    }

    public void print(String s){
        LocalTime currentTime = LocalTime.now();
        // 使用DateTimeFormatter格式化时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:  ");
        this.log.append(currentTime.format(formatter)+s+"\n");
    }


    public static void main(String[] argv){
//        new Window(new Node());
    }
}
