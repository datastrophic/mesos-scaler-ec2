package model;

import lombok.Data;

import java.util.List;

/**
 * Created by ssa on 2015-10-09 16:03
 */

@Data
public class HAProxyConfig {

    private List<String> routes;
    private List<Frontend> frontends;
    private List<Backend> backends;

    public HAProxyConfig(){}

}
