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
        String              name       = "Test Web Service Data Source";
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(WebServiceDataSource.WSDLURL_PROPERTYNAME, "http://www.webservicex.net/LondonGoldFix.asmx?wsdl");
        properties.put(WebServiceDataSource.SERVICENAMESPACE_PROPERTYNAME, "http://www.webservicex.net");
        properties.put(WebServiceDataSource.SERVICENAME_PROPERTYNAME, "LondonGoldAndSilverFix");
        properties.put(WebServiceDataSource.PORTNAMESPACE_PROPERTYNAME, "http://www.webservicex.net");
        properties.put(WebServiceDataSource.PORTNAME_PROPERTYNAME, "LondonGoldAndSilverFixSoap12");
        properties.put(WebServiceDataSource.SCHEDULEEXPRESSION_PROPERTYNAME, "");

        try
        {
            WebServiceDataSource webServiceDataSource = new WebServiceDataSource(name, properties);

            for (int count = 0; count < 1; count ++)
            {
                webServiceDataSource.invokeWebService();
                Thread.sleep(2000);
            }
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "", throwable);
        }
    }

    @PreDestroy
    public void cleanup()
    {
    }
}
