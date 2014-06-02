/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.webservice.dataflownodes;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Timeout;
import javax.ejb.TimerService;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import com.arjuna.databroker.data.DataProvider;
import com.arjuna.databroker.data.DataSource;

public class WebServiceDataSource implements DataSource
{
    private static final Logger logger = Logger.getLogger(WebServiceDataSource.class.getName());

    public static final String WSDLURL_PROPERTYNAME            = "WSDL URL";
    public static final String SERVICENAMESPACE_PROPERTYNAME   = "Service Namespace";
    public static final String SERVICENAME_PROPERTYNAME        = "Service Name";
    public static final String SCHEDULEEXPRESSION_PROPERTYNAME = "Schedule Expression";

    public WebServiceDataSource(String name, Map<String, String> properties)
    {
        logger.log(Level.INFO, "WebServiceDataSource: " + name + ", " + properties);

        _name          = name;
        _properties    = properties;

        _dataProvider = new TestingDataProvider<String>(this);

        String wsdlURLProperty            = properties.get(WSDLURL_PROPERTYNAME);
        String serviceNamespaceProperty   = properties.get(SERVICENAMESPACE_PROPERTYNAME);
        String serviceNameProperty        = properties.get(SERVICENAME_PROPERTYNAME);
        String scheduleExpressionProperty = properties.get(SCHEDULEEXPRESSION_PROPERTYNAME);

        try
        {
            URL     wsdlURL     = new URL(wsdlURLProperty);
            QName   serviceName = new QName(serviceNamespaceProperty, serviceNameProperty);
            Service service     = Service.create(wsdlURL, serviceName);
/*
            _smnReceiver = service.getPort(SOAPReceiver.class);
            ((BindingProvider) _smnReceiver).getRequestContext().put("javax.xml.ws.client.connectionTimeout", "180000");
            ((BindingProvider) _smnReceiver).getRequestContext().put("javax.xml.ws.client.receiveTimeout", "180000");
*/
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "WebServiceDataSource: Setup problems", throwable);
        }
    }

    @Override
    public String getName()
    {
        return _name;
    }

    @Override
    public Map<String, String> getProperties()
    {
        return Collections.unmodifiableMap(_properties);
    }

    @Timeout
    public void pollWebService()
    {
        logger.log(Level.INFO, "WebServiceDataSource.pollWebService");

        String data = "Test";
        
        _dataProvider.produce(data);
    }

    @Override
    public Collection<Class<?>> getDataProviderDataClasses()
    {
        Set<Class<?>> dataProviderDataClasses = new HashSet<Class<?>>();

        dataProviderDataClasses.add(String.class);
        
        return dataProviderDataClasses;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> DataProvider<T> getDataProvider(Class<T> dataClass)
    {
        if (dataClass == String.class)
            return (DataProvider<T>) _dataProvider;
        else
            return null;
    }

    private String               _name;
    private Map<String, String>  _properties;
    private DataProvider<String> _dataProvider;

    @Resource
    private TimerService _timerService;
}
