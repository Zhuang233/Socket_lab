import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class DrawBoard extends JPanel {
    Graphics2D g2d;
    int NodeNum;
    int[]x;
    int[]y;
    String[]name;
    int[]status;

    public DrawBoard(){
        this.NodeNum = 0;
        this.x = new int[10];
        this.y = new int[10];
        this.name = new String[10];
        this.status = new int[10];
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());


        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
//        g2d.drawString("Hello World!", 50, 50);
//        g2d.drawLine(0, 0, getWidth(), getHeight());
        for(int i=0;i<NodeNum;i++){
            if(status[i]==1){
                g2d.setColor(Color.GREEN);
                g2d.drawString("<< data",x[i]+20,y[i]+80);
                g2d.drawString("data >>",x[i]+90,y[i]+80);
            }
            else if(status[i]==0){
                g2d.setColor(Color.BLACK);
            }
            else if(status[i]==2){
                g2d.setColor(Color.RED);
                g2d.drawString("<< fail",x[i]+20,y[i]+80);
                g2d.drawString("fail >>",x[i]+90,y[i]+80);
            }
            else if(status[i]==3){
                g2d.setColor(Color.BLUE);
                g2d.drawString("<< rts ",x[i]+20,y[i]+80);
                g2d.drawString(" rts >>",x[i]+90,y[i]+80);
            }
            else if(status[i]==4){
                g2d.setColor(Color.orange);
                g2d.drawString("<< cts ",x[i]+20,y[i]+80);
                g2d.drawString(" cts >>",x[i]+90,y[i]+80);
            }
            else if(status[i]==6){
                g2d.setColor(Color.magenta);
                g2d.drawString("<< ack ",x[i]+20,y[i]+80);
                g2d.drawString(" ack >>",x[i]+90,y[i]+80);
            }
            else if(status[i]==5){
                g2d.setColor(Color.BLACK);
                g2d.drawString("<< x,y ",x[i]+20,y[i]+80);
                g2d.drawString(" x,y >>",x[i]+90,y[i]+80);
            }

            g2d.drawOval(x[i],y[i],150,150);
            g2d.drawString(name[i],x[i]+70,y[i]+80);
        }
//        g2d.drawOval(100, 100, 100, 100);
    }

    public void AddNode(String name,int x,int y){
        this.x[NodeNum] = x*15;
        this.y[NodeNum] = y*15;
        this.name[NodeNum] = name;
        this.status[NodeNum] = 0;
        NodeNum++;
        repaint();
    }

    public void NodeNone(String name){
        for(int i = 0;i<10;i++){
            if(Objects.equals(this.name[i], name)){
                this.status[i] = 0;
            }
        }
        repaint();
    }


    public void NodeSendData(String name){
        for(int i = 0;i<10;i++){
            if(Objects.equals(this.name[i], name)){
                this.status[i] = 1;
            }
        }
        repaint();
        RestoreThread re = new RestoreThread(this);
        re.time = 5000;
        re.name = name;
        re.start();
    }

    public void NodeSendRTS(String name) throws InterruptedException {
        for(int i = 0;i<10;i++){
            if(Objects.equals(this.name[i], name)){
                this.status[i] = 3;
            }
        }
        repaint();
    }

    public void NodeSendCTS(String name) throws InterruptedException {
        for(int i = 0;i<10;i++){
            if(Objects.equals(this.name[i], name)){
                this.status[i] = 4;
            }
        }
        repaint();
        RestoreThread re = new RestoreThread(this);
        re.time = 500;
        re.name = name;
        re.start();
    }

    public void NodeSendExchange(String name){
        for(int i = 0;i<10;i++){
            if(Objects.equals(this.name[i], name)){
                this.status[i] = 5;
            }
        }
        repaint();
        RestoreThread re = new RestoreThread(this);
        re.time = 500;
        re.name = name;
        re.start();
    }

    public void NodeSendFail(String name){
        for(int i = 0;i<10;i++){
            if(Objects.equals(this.name[i], name)){
                this.status[i] = 2;
            }
        }
        repaint();
        RestoreThread re = new RestoreThread(this);
        re.time = 500;
        re.name = name;
        re.start();
    }

    public void NodeSendACK(String name){
        for(int i = 0;i<10;i++){
            if(Objects.equals(this.name[i], name)){
                this.status[i] = 6;
            }
        }
        repaint();
        RestoreThread re = new RestoreThread(this);
        re.time = 500;
        re.name = name;
        re.start();
    }


    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("Java 2D Demo");
        DrawBoard panel = new DrawBoard();
        frame.add(panel);
        frame.setSize(400, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.AddNode("A",0,5);
//        panel.AddNode("B",5,5);
//        panel.AddNode("C",10,5);
//        panel.AddNode("D",15,5);
//        Thread.sleep(1000);
//        panel.NodeSendFail(0);
//        Thread.sleep(1000);
//        panel.NodeSendACK(0);
//        Thread.sleep(1000);
//        panel.NodeSendCTS(0);
//        Thread.sleep(1000);
//        panel.NodeSendRTS(0);
//        Thread.sleep(1000);
//        panel.NodeSendData(0);
//        Thread.sleep(1000);
//        panel.NodeSendExchange(0);




//        Thread.sleep(1000);
//        panel.NodeSendFail(3);
//        panel.NodeNone(2);
    }
}
