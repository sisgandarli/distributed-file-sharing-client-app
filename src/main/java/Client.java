
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class Client {

    private ZooKeeper zk;
    private Watcher watcher;
    private int sessionTimeout = 2000;
    private CountDownLatch latch = new CountDownLatch(1);

    public Client(String hosts) {
        try {
            watcher = new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            };
            zk = new ZooKeeper(hosts, sessionTimeout, watcher);
            latch.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void listFiles() {
        if (zk != null) {
            try {
                List<String> fileNames = zk.getChildren("/my_app", true);
                System.out.println("Listing the file names...");
                System.out.printf("Total #files: %d\n", fileNames.size());
                for (String i : fileNames) {
                    System.out.println(i);
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void createFile(String fileName) {
        if (zk != null) {
            try {
                if (zk.exists(fileName, true) == null) {
                    zk.create(fileName, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    System.out.printf("The \"%s\" was created.\n", fileName);
                } else {
                    System.out.printf("The following file (\"%s\") already exists in the file system.\n", fileName);
                }
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
                if (stat == null) {
                    System.out.printf("The following file (\"%s\") does not exist in the file system.\n", fileName);
                } else {
                    zk.delete(fileName, stat.getVersion());
                    System.out.printf("The \"%s\" was was deleted.\n", fileName);
                }
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
                if (zk.exists(fileName, true) == null) {
                    System.out.printf("The following file (\"%s\") does not exist in the file system.\n", fileName);
                } else {
                    byte[] data = zk.getData(fileName, true, zk.exists(fileName, true));
                    String dataString = new String(data);
                    System.out.printf("Reading the \"%s\" file...\n", fileName);
                    System.out.println(dataString);
                }
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
                if (zk.exists(fileName, true) == null) {
                    System.out.printf("The following file (\"%s\") does not exist in the file system.\n", fileName);
                } else {
                    byte[] previousData = zk.getData(fileName, true, zk.exists(fileName, true));
                    String previousDataString = new String(previousData);
                    String newDataString = null;
                    if (previousDataString.trim().equals("")) {
                        newDataString = line;
                    } else {
                        newDataString = previousDataString + "\n" + line;
                    }
                    zk.setData(fileName, newDataString.getBytes(), zk.exists(fileName, true).getVersion());
                    System.out.printf("The line \"%s\" was appended to \"%s\" file", line, fileName);
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
