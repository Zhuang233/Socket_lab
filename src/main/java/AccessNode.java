import java.util.ArrayList;
import java.util.List;

public class AccessNode {
    Node p;
    List<NodeInfo> infos;



    public boolean IsAcc(byte x, byte y){
        double dx = x - p.info.x;
        double dy = y - p.info.y;
        if (Math.sqrt(dx*dx+dy*dy) < 5)
            return true;
        return false;
    }

    // 通过mac查看是否为邻点
    public boolean IsAcc(byte mac){
        for(NodeInfo i:infos){
            if(i.mac == mac){
                return true;
            }
        }
        return false;
    }

    public AccessNode(Node p) {
        this.p = p;
        this.infos = new ArrayList<>();
    }
}
