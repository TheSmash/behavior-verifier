package com.smash.revolance.appreview.agent;

import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * Created by ebour on 29/03/14.
 */
public class AgentHelper
{
    public static boolean isInPackages(final String className, final Set<String> packages)
    {
        for(String packageName : packages)
        {
            if(className.startsWith( packageName ))
            {
                return true;
            }
        }
        return false;
    }

    public static ClassFileTransformer instanciateClassTransformer(String agentArguments)
    {
        final Set packages = new HashSet();

        if (agentArguments != null)
        {
            final Map<String, String> properties = new HashMap<String, String>();

            handleProperties(agentArguments, properties);

            handlePackages(packages, properties);

            handleListeners(properties);

            handleReporters(properties);
        }

        ClassTransformer classTransformer = new ClassTransformer();
        classTransformer.setPackages( packages );

        return classTransformer;
    }

    private static void handleProperties(final String agentArguments, Map<String, String> properties)
    {
        for(String propertyAndValue: agentArguments.split(","))
        {
            final String[] tokens = propertyAndValue.split(":", 2);
            if (tokens.length == 2)
            {
                properties.put(tokens[0], tokens[1]);
            }
        }
    }

    public static void load(final String agentPath, final String agentOptions)
    {

        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        String pid = nameOfRunningVM.substring(0, p);

        if(!new File(agentPath).exists())
        {
            return;
        }
        else
        {
            try
            {
                VirtualMachine vm = VirtualMachine.attach(pid);
                vm.loadAgent(agentPath, agentOptions);
                vm.detach();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private static void handlePackages(final Set packages, Map<String, String> properties)
    {
        String packageList = properties.get("includedPackages");
        if (packageList != null)
        {
            packages.addAll( Arrays.asList(packageList.split(";")) );
        }
    }

    private static void handleListeners(final Map<String, String> properties)
    {
        final Set listeners = new HashSet();
        String listenersList = properties.get("listeners");
        if (listenersList != null)
        {
            listeners.addAll( Arrays.asList(listenersList.split(";")) );
        }

        Notifier.getInstance().addListeners(listenersList);
    }

    private static void handleReporters(final Map<String, String> properties)
    {
        final Set reporters = new HashSet();
        String reportersList = properties.get("reporters");
        if (reportersList != null)
        {
            reporters.addAll( Arrays.asList(reportersList.split(";")) );
        }

        Notifier.getInstance().addReporters(reportersList);
    }

}
