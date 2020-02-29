import com.google.gson.Gson;
import domain.SimpleInfo;
import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.log4j.PropertyConfigurator;
import org.apache.zookeeper.*;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * author yg
 * description
 * date 2020/2/29
 */
public class TestZk {
    private ZooKeeper zooKeeper;
    private ServiceDiscovery<SimpleInfo> serviceDiscovery;

    @Before
    public void init() throws Exception {
        InputStream resourceAsStream = Main.class.getClassLoader().getResourceAsStream("log4j.properties");
        PropertyConfigurator.configure(resourceAsStream);
        ExponentialBackoffRetry backoffRety = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", backoffRety);
        client.start();
        CuratorZookeeperClient zookeeperClient = client.getZookeeperClient();
        zooKeeper = zookeeperClient.getZooKeeper();
        serviceDiscovery = ServiceDiscoveryBuilder.builder(SimpleInfo.class)
                .client(client).basePath(Main.BASE_PATH).build();
        serviceDiscovery.start();
    }

    @Test
    public void createPermanentNOde() throws KeeperException, InterruptedException {
        SimpleInfo getTest = new SimpleInfo("GetTest");
        String s = new Gson().toJson(getTest);
        zooKeeper.create(getPath()+"/1", s.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Test
    public void getNodes() throws KeeperException, InterruptedException {
        List<String> children1 = zooKeeper.getChildren("/root", true);
        System.err.println("son:" + children1);
    }

    @Test
    public void delNodes() throws KeeperException, InterruptedException {

        String path = getPath();
        List<String> children = zooKeeper.getChildren(path, false);
        for (String child : children) {
            zooKeeper.delete(path + "/" + child, -1);
        }
        zooKeeper.delete(path, -1);
        System.err.println(children);
    }

    private String getPath() {
        return Main.BASE_PATH + "/" + Main.NODE_NAME;
    }

    @Test
    public void discovery() throws Exception {
        //添加默认监听器
        zooKeeper.register(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("已经触发了" + event.getType() + "事件！");
            }
        });
        //设置该目录监听
        zooKeeper.getChildren(getPath(), true);
        Collection<ServiceInstance<SimpleInfo>> first = serviceDiscovery.queryForInstances(Main.NODE_NAME);
        for (ServiceInstance<SimpleInfo> simpleInfoServiceInstance : first) {
            System.err.println(simpleInfoServiceInstance.toString());
        }
        Thread.sleep(500000);
    }
}
