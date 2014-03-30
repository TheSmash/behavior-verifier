package com.smash.revolance.appreview.agent;

import com.sun.tools.hat.internal.model.StackTrace;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: wsmash
 * Date: 28/03/14
 * Time: 22:39
 */
public class Notifier
{
    private static final Logger LOG = Logger.getLogger(Notifier.class.getName());

    private static final Set<EventListener> listeners = new HashSet();
    private static final Set<EventReporter> reporters = new HashSet();

    private static String reportDir = System.getProperty("user.home");

    static
    {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run()
            {
                final String jvmId = ManagementFactory.getRuntimeMXBean().getName();

                LOG.log(Level.INFO, "generating reports");
                for(EventReporter reporter : reporters)
                {
                    try
                    {
                        reporter.setReportDir(reportDir);
                        reporter.doReport(jvmId);
                    }
                    catch(Exception e)
                    {
                        LOG.log(Level.SEVERE, "Unable to generate report!", e.getCause());
                    }
                }
                LOG.log(Level.INFO, "generating reports [Done]");
            }
        });
    }

    private static Notifier instance = new Notifier();

    public synchronized static Notifier getInstance()
    {
        return instance;
    }

    public synchronized void addReporters(final String... reporterClasses)
    {
        for(String reporterClass : reporterClasses)
        {
            if(reporterClass == null)
            {
                continue;
            }
            try
            {
                Class clazz = Class.forName(reporterClass.trim());
                if(clazz == null)
                {
                    LOG.log( Level.SEVERE, "Class: " + reporterClass + " not found on classpath");
                    continue;
                }

                Object reporter = clazz.newInstance();
                if ( reporter instanceof EventReporter )
                {
                    addReporter( (EventReporter) reporter );
                }
                else
                {
                    LOG.log( Level.SEVERE, "Class: " + reporterClass + " does not implement interface: " + EventReporter.class.getName() );
                }
            }
            catch (Exception e)
            {
                LOG.log( Level.SEVERE, "Unable to instantiate class: " + reporterClass, e);
            }
        }

        if(listeners.isEmpty())
        {
            LOG.log( Level.WARNING, "No reporters have been registered");
        }
    }

    public synchronized void addListeners(final String... listenerClasses)
    {
        for(String listenerClass : listenerClasses)
        {
            if(listenerClass == null)
            {
                continue;
            }
            try
            {
                Class clazz = Class.forName( listenerClass.trim() );
                if(clazz == null)
                {
                    LOG.log( Level.SEVERE, "Class: " + listenerClass + " not found on classpath");
                    continue;
                }

                Object listener = clazz.newInstance();
                if ( listener instanceof EventListener )
                {
                    addListener( (EventListener) listener );
                }
                else
                {
                    LOG.log( Level.SEVERE, "Class: " + listenerClass + " does not implement interface: " + EventListener.class.getName() );
                }
            }
            catch (Exception e)
            {
                LOG.log( Level.SEVERE, "Unable to instantiate class: " + listenerClass, e);
            }
        }

        if(listeners.isEmpty())
        {
            LOG.log( Level.WARNING, "No listeners have been registered");
        }
    }

    public synchronized void addReporter(final EventReporter reporter)
    {
        LOG.log(Level.INFO, "adding listenerClass: " + reporter.getClass().getCanonicalName());

        reporters.add( reporter );
    }

    public synchronized void addListener(final EventListener listener)
    {
        LOG.log(Level.INFO, "adding listenerClass: " + listener.getClass().getCanonicalName());

        listeners.add( listener );
    }

    public synchronized void notify(final String eventType, final String methodName)
    {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        String callerMethod = stackTraceElement.getClassName()+"."+stackTraceElement.getMethodName();

        final Map<String, String> event = new HashMap<String, String>();

        event.put( "eventType", eventType );
        event.put( "eventTime", String.valueOf( System.currentTimeMillis() ));

        event.putAll(NotifierHelper.buildEventData("caller", callerMethod));
        event.putAll(NotifierHelper.buildEventData(methodName));

        for(EventListener listener : listeners)
        {
            listener.receive(event);
        }
    }


    public static void setReportDir(final String reportDir)
    {
        if(reportDir!=null)
        {
            Notifier.reportDir = reportDir;
        }
    }
}
