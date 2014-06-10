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
        properties.put(WebServiceDataSource.SERVICEURL_PROPERTYNAME, "http://www.webservicex.net/LondonGoldFix.asmx");
        properties.put(WebServiceDataSource.OPERATIONNAMESPACE_PROPERTYNAME, "http://www.webservicex.net");
        properties.put(WebServiceDataSource.OPERATIONNAME_PROPERTYNAME, "GetLondonGoldAndSilverFix");
        properties.put(WebServiceDataSource.SCHEDULEDELAY_PROPERTYNAME, "0");
        properties.put(WebServiceDataSource.SCHEDULEPERIOD_PROPERTYNAME, "5000");

        try
        {
            WebServiceDataSource webServiceDataSource = new WebServiceDataSource(name, properties);
   
            Thread.sleep(10000);
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "", throwable);
        }
    }

    @PreDestroy
    public void cleanup()
    {
        logger.log(Level.INFO, "StandaloneSetup setup");
    }
}
