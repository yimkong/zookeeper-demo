package domain;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;

/**
 * author yg
 * description
 * date 2020/2/29
 */
public class SimpleInfo {
    private String name;

    public SimpleInfo() {
    }

    public SimpleInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
