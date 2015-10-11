package model;

import lombok.Data;

/**
 * Created by ssa on 2015-10-09 16:05
 */

@Data
public class Server {
    private String name;
    private String host;
    private Integer port;
    private boolean check = true;
    private Integer checkInterval = 10;

    public Server(){}

}
