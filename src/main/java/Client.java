
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.zookeeper.ZooKeeper;

public class Client {

    private ZooKeeper zk;

    public Client(String hosts) {
        try {
            zk = new ZooKeeper(hosts, 0, null);
        } catch (IOException e) {
            zk = null;
            e.printStackTrace();
        }
    }
    
    public void createFile(String path) {
        
    }
    
    
}
