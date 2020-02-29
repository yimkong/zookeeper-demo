import domain.SimpleInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.log4j.PropertyConfigurator;

import java.io.InputStream;

/**
 * author yg
 * description
 * date 2020/2/29
 */
public class Main {
    public static final String BASE_PATH = "/root";
    public static final String NODE_NAME = "FIRST";

    public static void main(String[] args) throws Exception {
        InputStream resourceAsStream = Main.class.getClassLoader().getResourceAsStream("log4j.properties");
        PropertyConfigurator.configure(resourceAsStream);
        ExponentialBackoffRetry backoffRety = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", backoffRety);
        client.start();
        SimpleInfo model = new SimpleInfo(NODE_NAME);
        ServiceInstanceBuilder<SimpleInfo> sib = ServiceInstance.builder();
        sib.address("127.0.0.1");
//        sib.id("");
        sib.name(model.getName());
        ServiceInstance<SimpleInfo> instance = sib.payload(model).build();
        ServiceDiscovery<SimpleInfo> serviceDiscovery = ServiceDiscoveryBuilder.builder(SimpleInfo.class)
                .client(client).basePath(BASE_PATH).build();
        // 服务注册,相当于添加一个临时节点
        serviceDiscovery.registerService(instance);
        serviceDiscovery.start();
        Thread.sleep(1000 * 60 * 30);
    }
}
