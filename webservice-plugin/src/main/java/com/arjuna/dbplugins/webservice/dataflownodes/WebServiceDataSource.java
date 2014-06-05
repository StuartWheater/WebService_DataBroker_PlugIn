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
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.w3c.dom.NodeList;

import com.arjuna.databroker.data.DataProvider;
import com.arjuna.databroker.data.DataSource;

public class WebServiceDataSource implements DataSource
{
    private static final Logger logger = Logger.getLogger(WebServiceDataSource.class.getName());

    public static final String WSDLURL_PROPERTYNAME            = "WSDL URL";
    public static final String SERVICENAMESPACE_PROPERTYNAME   = "Service Namespace";
    public static final String SERVICENAME_PROPERTYNAME        = "Service Name";
    public static final String PORTNAMESPACE_PROPERTYNAME      = "Port Namespace";
    public static final String PORTNAME_PROPERTYNAME           = "Port Name";
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
        String portNamespaceProperty      = properties.get(PORTNAMESPACE_PROPERTYNAME);
        String portNameProperty           = properties.get(PORTNAME_PROPERTYNAME);
        String scheduleExpressionProperty = properties.get(SCHEDULEEXPRESSION_PROPERTYNAME);

        try
        {
            URL   wsdlURL     = new URL(wsdlURLProperty);
            QName serviceName = new QName(serviceNamespaceProperty, serviceNameProperty);

            _service  = Service.create(wsdlURL, serviceName);
            _portName = new QName(portNamespaceProperty, portNameProperty);
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

    public void invokeWebService()
    {
        logger.log(Level.INFO, "WebServiceDataSource.invokeWebService");

        String result = null;
        try {
            Dispatch<SOAPMessage> dispatch = _service.createDispatch(_portName,SOAPMessage.class, Service.Mode.MESSAGE);
            dispatch.getRequestContext().put("javax.xml.ws.client.connectionTimeout", "30000");
            dispatch.getRequestContext().put("javax.xml.ws.client.receiveTimeout", "30000");
            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            SOAPMessage    request        = messageFactory.createMessage();

            SOAPMessage responce = dispatch.invoke(request);

            SOAPPart     responcePart     = responce.getSOAPPart();
            SOAPEnvelope responceEnvelope = responcePart.getEnvelope();
            SOAPBody     responceBody     = responceEnvelope.getBody();

            NodeList childNodes = responceBody.getChildNodes();
            for (int index = 0; index < childNodes.getLength(); index++)
                logger.log(Level.INFO, "Problems with web service invoke", childNodes.item(index).toString());

            result = responceBody.getAttribute("test");
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "Problems with web service invoke", throwable);
        }

        if (result != null)
            _dataProvider.produce(result);
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

    private Service _service;
    private QName   _portName;

    private String               _name;
    private Map<String, String>  _properties;
    private DataProvider<String> _dataProvider;
}
