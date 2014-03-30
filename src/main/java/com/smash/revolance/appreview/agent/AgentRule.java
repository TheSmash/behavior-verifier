package com.smash.revolance.appreview.agent;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import org.apache.commons.io.FileUtils;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ebour on 29/03/14.
 */
public class AgentRule implements TestRule
{
    private final static Logger LOG = Logger.getLogger(AgentRule.class.getName());

    private static String agentPath    = "";
    private static String agentOptions = "listeners:" + PlantUmlEventListener.class.getCanonicalName() + ",reporters:"+PlantUmlEventListener.class.getCanonicalName()+",reportDir:target/";

    private static List<String>              diagram = new ArrayList<String>();
    private static List<Map<String, String>> events  = new ArrayList<Map<String, String>>();

    @Override
    public Statement apply(final Statement statement, final Description description)
    {
        return new Statement()
        {
            @Override
            public void evaluate() throws Throwable
            {
                Agent.load(agentPath, agentOptions);
                try
                {
                    statement.evaluate();
                }
                finally
                {
                    Agent.unload();
                }
            }
        };
    }

    public AgentRule withPath(final String agentPath)
    {
        this.agentPath = agentPath;
        return this;
    }

    public AgentRule withOptions(final String optionName, final String optionValue)
    {
        if(!agentOptions.isEmpty())
        {
            agentOptions += ",";
        }

        agentOptions += optionName + ":" + optionValue;
        return this;
    }

}
