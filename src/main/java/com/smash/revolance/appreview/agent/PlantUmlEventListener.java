package com.smash.revolance.appreview.agent;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ebour on 30/03/14.
 */
public class PlantUmlEventListener implements EventListener, EventReporter
{
    private final static Logger LOG = Logger.getLogger(PlantUmlEventListener.class.getName());

    private final static List<Map<String, String>> events  = new ArrayList<Map<String, String>>();
    private final static List                      diagram = new ArrayList();

    private static String reportDir = System.getProperty("user.home");

    public PlantUmlEventListener()
    {
        diagram.add("@startuml");
    }

    @Override
    public void receive(Map<String, String> event)
    {
        events.add(event);
        if(event.get("eventType").equals("method begin"))
        {
            diagram.add(event.get("caller.className") + " --> " + event.get("className") + ": " + event.get("methodName"));
        }
        else if(event.get("eventType").equals("method end"))
        {
            diagram.add(event.get("className") + " --> " + event.get("caller.className") + ": " + event.get("methodName"));
        }
    }

    @Override
    public List<Map<String, String>> getEvents()
    {
        return events;
    }

    @Override
    public void setReportDir(String reportDir)
    {
        this.reportDir = reportDir;
    }

    @Override
    public void doReport(String filename) throws Exception
    {
        diagram.add("@enduml");

        File diagramFile = new File(reportDir, filename);
        FileUtils.writeLines(diagramFile, diagram);

        SourceFileReader reader = new SourceFileReader(diagramFile);
        List<GeneratedImage> list = reader.getGeneratedImages();
        list.get(0).getPngFile();

        LOG.log(Level.INFO, "diagram generated in file: " + filename);
    }

}
