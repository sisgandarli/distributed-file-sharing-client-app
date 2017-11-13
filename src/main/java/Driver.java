
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class Driver {

    public static void main(String[] args) throws IOException {
        String[] hosts = new String[]{
            "ec2-34-214-221-30.us-west-2.compute.amazonaws.com:2181",
            "ec2-34-215-107-90.us-west-2.compute.amazonaws.com:2181",
            "ec2-34-215-123-215.us-west-2.compute.amazonaws.com:2181"
        };
        String connectionString = "";
        for (int i = 0; i < hosts.length; i++) {
            connectionString += hosts[i];
            if (i != hosts.length - 1) {
                connectionString += ",";
            } 
        }
        int sessionTimeout = 2000;
        Watcher watcher = null;
        ZooKeeper zk = new ZooKeeper(connectionString, sessionTimeout, watcher);
        if (zk != null) {
            try {
                List<String> zkChildren = zk.getChildren("/", false);
                for (String child: zkChildren) {
                    System.out.println(child);
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
