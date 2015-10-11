package model;

import lombok.Data;

/**
 * Created by ssa on 2015-10-09 16:04
 */

@Data
public class Frontend {

    private String name = "fibonacci-service-frontend";
    private Integer bindPort;
    private String bindIp = "0.0.0.0";
    private String defaultBackend = "fibonacci-service-backend";
    private String mode = "http";

    public Frontend(){}
}
