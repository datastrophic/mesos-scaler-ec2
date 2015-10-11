package model;

import lombok.Data;

import java.util.List;

/**
 * Created by ssa on 2015-10-09 16:04
 */

@Data
public class Backend {

    private String name;
    private String mode = "http";
    private List<Server> servers;
    private boolean proxyMode = false;

}
