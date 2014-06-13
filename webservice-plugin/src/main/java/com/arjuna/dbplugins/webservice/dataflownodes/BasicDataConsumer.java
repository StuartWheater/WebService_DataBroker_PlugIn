/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.webservice.dataflownodes;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.arjuna.databroker.data.DataConsumer;
import com.arjuna.databroker.data.DataFlowNode;
import com.arjuna.databroker.data.DataProvider;

public class BasicDataConsumer<T> implements DataConsumer<T>
{
    private static final Logger logger = Logger.getLogger(BasicDataConsumer.class.getName());

    public BasicDataConsumer(DataFlowNode dataFlowNode, String methodName)
    {
        logger.log(Level.FINE, "BasicDataConsumer: " + dataFlowNode + ", " + methodName);
        
        _dataFlowNode = dataFlowNode;
        _methodName   = methodName;
    }

    @Override
    public DataFlowNode getDataFlowNode()
    {
        return _dataFlowNode;
    }

    @Override
    public void consume(DataProvider<T> dataProvider, T data)
    {
        try
        {
            logger.log(Level.INFO, "BasicDataConsumer.consume: [" + data.toString() + "]");
            getMethod(_dataFlowNode.getClass(),_methodName).invoke(_dataFlowNode, data);
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "Problem invoking consumer", throwable);
        }
    }

    private static Method getMethod(Class<?> nodeClass, String nodeMethodName)
    {
        try
        {
            return nodeClass.getMethod(nodeMethodName, new Class[]{String.class});
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "Unable to find method \"" + nodeMethodName + "\"", throwable);

            return null;
        }
    }

    private DataFlowNode _dataFlowNode;
    private String       _methodName;
}
