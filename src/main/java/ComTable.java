import java.util.ArrayList;
import java.util.List;

public class ComTable {
    List<NodeInfo> comItems;

    public NodeInfo checkByMac(byte mac){
        for(NodeInfo n:comItems){
            if (mac == n.mac){
                return n;
            }
        }
        return null;
    }

    // 深拷贝添加
    public void Add(NodeInfo info){
        NodeInfo infoTemp = new NodeInfo();
        infoTemp.mac = info.mac;
        infoTemp.port = info.port;
        infoTemp.ip = info.ip;
        comItems.add(infoTemp);
    }

    public ComTable() {
        this.comItems = new ArrayList<>();
    }
}
