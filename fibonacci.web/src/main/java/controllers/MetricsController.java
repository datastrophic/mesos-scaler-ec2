package controllers;

import ninja.Result;
import ninja.Results;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Set;

/**
 * Created by ssa on 2015-10-09 12:13
 * name:fibonacci.web:name=controllers.ApplicationController.calculateFibonacci
 */
public class MetricsController {

    public Result getResponseTime75p() throws Exception{
        String value = "0";
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> objectNames = server.queryNames(null, null);
        for (ObjectName objectName : objectNames) {

            if(objectName.toString().contains("ApplicationController.calculateFibonacci")){
                MBeanInfo info = server.getMBeanInfo(objectName);
                for(MBeanAttributeInfo attributeInfo : info.getAttributes()){
                    if(attributeInfo.getName().equals("75thPercentile")) {
                        String attributeName = attributeInfo.getName();
                        value = server.getAttribute(objectName, attributeName).toString();
                    }
                }
                break;
            }

        }

        return Results.ok().json().render(value);
    }
}
