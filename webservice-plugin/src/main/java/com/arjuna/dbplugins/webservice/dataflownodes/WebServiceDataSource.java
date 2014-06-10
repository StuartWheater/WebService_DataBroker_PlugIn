/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.webservice.dataflownodes;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import org.w3c.dom.NodeList;
import com.arjuna.databroker.data.DataProvider;
import com.arjuna.databroker.data.DataSource;

public class WebServiceDataSource implements DataSource
{
    private static final Logger logger = Logger.getLogger(WebServiceDataSource.class.getName());

    public static final String SERVICEURL_PROPERTYNAME         = "Service URL";
    public static final String OPERATIONNAMESPACE_PROPERTYNAME = "Operation Namespace";
    public static final String OPERATIONNAME_PROPERTYNAME      = "Operation Name";
    public static final String SCHEDULEEXPRESSION_PROPERTYNAME = "Schedule Expression";

    public WebServiceDataSource(String name, Map<String, String> properties)
    {
        logger.log(Level.INFO, "WebServiceDataSource: " + name + ", " + properties);

        _name          = name;
        _properties    = properties;

        _dataProvider = new TestingDataProvider<String>(this);

        _serviceURL         = properties.get(SERVICEURL_PROPERTYNAME);
        _operationNamespace = properties.get(OPERATIONNAMESPACE_PROPERTYNAME);
        _operationName      = properties.get(OPERATIONNAME_PROPERTYNAME);
        _scheduleExpression = properties.get(SCHEDULEEXPRESSION_PROPERTYNAME);
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
            MessageFactory messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            SOAPMessage    request         = messageFactory.createMessage();
            SOAPPart       requestPart     = request.getSOAPPart();
            SOAPEnvelope   requestEnvelope = requestPart.getEnvelope();
            SOAPBody       requestBody     = requestEnvelope.getBody();
            requestEnvelope.addNamespaceDeclaration("oper", _operationNamespace);
            requestBody.addBodyElement(requestEnvelope.createQName(_operationName, "oper"));

            ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
            request.writeTo(requestOutputStream);
            logger.log(Level.WARNING, "Request: " + requestOutputStream.toString());
            requestOutputStream.close();

            SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection        connection        = connectionFactory.createConnection();
//            connection.getRequestContext().put("javax.xml.ws.client.connectionTimeout", "30000");
//            connection.getRequestContext().put("javax.xml.ws.client.receiveTimeout", "30000");

            SOAPMessage responce = connection.call(request, _serviceURL);

            ByteArrayOutputStream responceOutputStream = new ByteArrayOutputStream();
            responce.writeTo(responceOutputStream);
            logger.log(Level.WARNING, "Responce: " + responceOutputStream.toString());
            responceOutputStream.close();

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

    private String _serviceURL;
    private String _operationNamespace;
    private String _operationName;
    private String _scheduleExpression;

    private String               _name;
    private Map<String, String>  _properties;
    private DataProvider<String> _dataProvider;
}
