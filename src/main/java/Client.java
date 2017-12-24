
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.Stat;

public class Client {

    private ZooKeeper zk;
    private Watcher watcher;
    private int sessionTimeout = 5000;
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
            latch.await(5, TimeUnit.SECONDS);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            sendClosedSessionMessage();
        }
    }
    
    public boolean isConnected() {
        return (zk.getState() == States.CONNECTED);
    }
    
    public void listFiles() {
        if (!isZkNull()) {
            try {
                List<String> fileNames = zk.getChildren("/", true);
                System.out.println("Listing the file names...");
                System.out.printf("Total #files: %d\n", fileNames.size());
                for (String i : fileNames) {
                    System.out.println(i);
                }
                System.out.println();
            } catch (KeeperException e) {
                if (e.code() == Code.CONNECTIONLOSS) {
                    sendClosedSessionMessage();
                }
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void createFile(String fileName) {
        if (!isZkNull()) {
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
                if (e.code() == Code.CONNECTIONLOSS) {
                    sendClosedSessionMessage();
                }
                e.printStackTrace();
            }
        }
    }

    public void deleteFile(String fileName) {
        if (!isZkNull()) {
            try {
                Stat stat = zk.exists(fileName, true);
                if (stat == null) {
                    System.out.printf("The following file (\"%s\") does not exist in the file system.\n", fileName);
                } else {
                    zk.delete(fileName, stat.getVersion());
                    System.out.printf("The \"%s\" was was deleted.\n", fileName);
                }
            } catch (KeeperException e) {
                if (e.code() == Code.CONNECTIONLOSS) {
                    sendClosedSessionMessage();
                }
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void readFile(String fileName) {
        if (!isZkNull()) {
            try {
                if (zk.exists(fileName, true) == null) {
                    System.out.printf("The following file (\"%s\") does not exist in the file system.\n", fileName);
                } else {
                    byte[] data = zk.getData(fileName, true, zk.exists(fileName, true));
                    String dataString = new String(data);
                    System.out.printf("Reading the \"%s\" file...\n", fileName);
                    if (dataString.length() == 0) {
                        System.out.printf("The file (\"%s\") is empty.\n", fileName);
                    } else {
                        System.out.println(dataString);
                    }
                }
            } catch (KeeperException e) {
                if (e.code() == Code.CONNECTIONLOSS) {
                    sendClosedSessionMessage();
                }
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void appendToFile(String fileName, String line) {
        if (!isZkNull()) {
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
                        newDataString = previousDataString + line;
                    }
                    zk.setData(fileName, newDataString.getBytes(), zk.exists(fileName, true).getVersion());
                    System.out.printf("The line \"%s\" was appended to \"%s\" file", line, fileName);
                }
            } catch (KeeperException e) {
                if (e.code() == Code.CONNECTIONLOSS) {
                    sendClosedSessionMessage();
                }
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean fileExists(String fileName) {
        if (!isZkNull()) {
            try {
                if (zk.exists(fileName, true) != null) {
                    return true;
                } else {
                    System.out.printf("The following file (\"%s\") does not exist in the file system.\n", fileName);
                    return false;
                }
            } catch (KeeperException e) {
                if (e.code() == Code.CONNECTIONLOSS) {
                    sendClosedSessionMessage();
                }
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    public void sendClosedSessionMessage() {
        System.out.println("The connection to the server(s) was lost");
        System.out.println("Closing the session...");
        System.exit(0);
    }

    private boolean isZkNull() {
        return this.zk == null;
    }
}
