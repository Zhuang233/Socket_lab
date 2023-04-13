public class NodeInfo {
    public byte mac;
    public Byte x;
    public Byte y;
    public String ip;
    public int port;// 监听端口

    public NodeInfo() {
        this.mac = 127;
        this.x = 127;
        this.y = 127;
        this.ip = null;
        this.port = -1;
    }

    public NodeInfo(byte mac, Byte x, Byte y, String ip, int port) {
        this.mac = mac;
        this.x = x;
        this.y = y;
        this.ip = ip;
        this.port = port;
    }

    public String getName(){
        return new String(new byte[]{mac});
    }


    @Override
    public String toString() {
        return "NodeInfomation{" +
                "mac=" + mac +
                ", x=" + x +
                ", y=" + y +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
