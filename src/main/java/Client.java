
import java.io.IOException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class Client {

    private ZooKeeper zk;
    private Watcher watcher;
    private int sessionTimeout = 2000;

    public Client(String hosts) {
        try {
            watcher = null;
            zk = new ZooKeeper(hosts, sessionTimeout, watcher);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createFile(String fileName) {
        if (zk != null) {
            try {
                zk.create(fileName, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteFile(String fileName) {
        if (zk != null) {
            try {
                Stat stat = zk.exists(fileName, true);
                zk.delete(fileName, stat.getVersion());
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void readFile(String fileName) {
        if (zk != null) {
            try {
                byte[] data = zk.getData(fileName, true, zk.exists(fileName, true));
                String dataString = new String(data);
                System.out.println(dataString);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void appendToFile(String fileName, String line) {
        if (zk != null) {
            try {
                byte[] previousData = zk.getData(fileName, true, zk.exists(fileName, true));
                String previousDataString = new String(previousData);
                String newDataString = previousDataString + "\n" + line;
                zk.setData(fileName, newDataString.getBytes(), zk.exists(fileName, true).getVersion());
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
