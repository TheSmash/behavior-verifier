package com.smash.revolance.appreview.agent;


import javax.management.remote.JMXConnectorFactory;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: wsmash
 * Date: 28/03/14
 * Time: 21:59
 */
public class Agent
{
    private final static Logger LOG = Logger.getLogger(Agent.class.getName());

    private static Instrumentation      instrumentation;
    private static ClassFileTransformer transformer;

    /**
     * JVM hook to dynamically load javaagent at runtime.
     *
     * The agent class may have an agentmain method for use when the agent is
     * started after VM startup.
     *
     * @param agentArguments
     * @param instrumentation
     *
     * @throws Exception
     */
    public static void agentmain(String agentArguments, Instrumentation instrumentation) throws Exception
    {
        String msg = String.format("agentmain method invoked with args: %s and inst: %s", agentArguments, instrumentation);
        LOG.log(Level.INFO, msg);

        Agent.instrumentation = instrumentation;
        addTransformer(AgentHelper.instanciateClassTransformer(agentArguments));
    }

    /**
     * JVM hook to statically load the javaagent at startup.
     *
     * After the Java Virtual Machine (JVM) has initialized, the premain method
     * will be called. Then the real application main method will be called.
     *
     * @param agentArguments
     * @param instrumentation
     *
     * @throws Exception
     */
    public static void premain(final String agentArguments, final Instrumentation instrumentation)
    {
        String msg = String.format("premain method invoked with args: %sand inst: %s", agentArguments, instrumentation);
        LOG.log(Level.INFO, msg);

        Agent.instrumentation = instrumentation;
        addTransformer(AgentHelper.instanciateClassTransformer(agentArguments));
    }

    private static void addTransformer(ClassFileTransformer transformer)
    {
        Agent.transformer = transformer;
        Agent.instrumentation.addTransformer(transformer);
    }

    /**
     * Programmatic hook to dynamically load javaagent at runtime.
     * @param agentPath
     * @param agentOptions
     */
    public static void load(final String agentPath, final String agentOptions)
    {
        if (instrumentation == null)
        {
            LOG.log(Level.INFO, "dynamically loading appreview agent");
            AgentHelper.load(agentPath, agentOptions);
            LOG.log(Level.INFO, "dynamically loading appreview agent [Done]");
        }
    }

    /**
     * Programmatic hook to dynamically unload javaagent at runtime.
     */
    public static void unload()
    {
        if (instrumentation == null)
        {
            LOG.log(Level.INFO, "dynamically unloading appreview agent");
            if(transformer != null)
            {
                ((ClassTransformer) transformer).preventExec(true);
                if(instrumentation.removeTransformer(transformer))
                {
                    LOG.log(Level.INFO, "dynamically unloading appreview agent [Done]");
                }
                else
                {
                    LOG.log(Level.INFO, "dynamically unloading appreview agent [Failed]");
                }
            }
        }
    }


}
