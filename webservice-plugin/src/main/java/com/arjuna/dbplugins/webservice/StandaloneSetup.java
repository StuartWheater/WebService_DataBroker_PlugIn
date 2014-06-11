/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.webservice;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import com.arjuna.dbplugins.webservice.dataflownodes.WebServiceDataSource;

@Startup
@Singleton
public class StandaloneSetup
{
    private static final Logger logger = Logger.getLogger(StandaloneSetup.class.getName());

    @PostConstruct
    public void setup()
    {
        logger.log(Level.INFO, "StandaloneSetup setup");
        String              name       = "Test Web Service Data Source";
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(WebServiceDataSource.SERVICEURL_PROPERTYNAME, "http://172.18.32.157/svcVehicleData/service1.asmx");
        properties.put(WebServiceDataSource.OPERATIONNAMESPACE_PROPERTYNAME, "http://tempuri.org/");
        properties.put(WebServiceDataSource.OPERATIONNAME_PROPERTYNAME, "GetVehicleData");
        properties.put(WebServiceDataSource.SCHEDULEDELAY_PROPERTYNAME, "10000");
        properties.put(WebServiceDataSource.SCHEDULEPERIOD_PROPERTYNAME, "30000");

        try
        {
            _webServiceDataSource = new WebServiceDataSource(name, properties);

            Thread.sleep(60000);

            _webServiceDataSource.stop();
         }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "", throwable);
            
            _webServiceDataSource.stop();
        }
    }

    @PreDestroy
    public void cleanup()
    {
        logger.log(Level.INFO, "StandaloneSetup setup");

        _webServiceDataSource.stop();
    }
    
    private WebServiceDataSource _webServiceDataSource;
}
