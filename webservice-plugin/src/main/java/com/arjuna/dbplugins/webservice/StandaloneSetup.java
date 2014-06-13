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
import com.arjuna.dbplugins.webservice.dataflownodes.PullWebServiceDataSource;

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
        properties.put(PullWebServiceDataSource.SERVICEURL_PROPERTYNAME, "http://172.18.32.157/svcVehicleData/service1.asmx");
        properties.put(PullWebServiceDataSource.OPERATIONNAMESPACE_PROPERTYNAME, "http://tempuri.org/");
        properties.put(PullWebServiceDataSource.OPERATIONNAME_PROPERTYNAME, "GetVehicleData");
        properties.put(PullWebServiceDataSource.SCHEDULEDELAY_PROPERTYNAME, "10000");
        properties.put(PullWebServiceDataSource.SCHEDULEPERIOD_PROPERTYNAME, "30000");

        try
        {
            _pullWebServiceDataSource = new PullWebServiceDataSource(name, properties);

            Thread.sleep(60000);

            _pullWebServiceDataSource.stop();
         }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "", throwable);
            
            _pullWebServiceDataSource.stop();
        }
    }

    @PreDestroy
    public void cleanup()
    {
        logger.log(Level.INFO, "StandaloneSetup setup");

        _pullWebServiceDataSource.stop();
    }
    
    private PullWebServiceDataSource _pullWebServiceDataSource;
}
