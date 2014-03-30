package com.smash.revolance.appreview.agent;

import com.smash.revolance.appreview.agent.test.A;
import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by ebour on 29/03/14.
 */
public class AgentITest
{

    @Rule
    public AgentRule agentRule = new AgentRule()
            .withPath(new File("target/appreview.jar").getAbsolutePath())
            .withOptions("includedPackages", AgentITest.class.getPackage().getName() + ".test");


    @Test
    public void agentShouldTraceMethodCallsOnlyInIncludedPackages() throws InterruptedException
    {
        A a = new A();
        a.foo();

        List<Map<String, String>> events = new PlantUmlEventListener().getEvents();
        assertThat(events.size(), is(8));
        for(Map<String, String> event : events)
        {
            assertThat(event.get("packageName"), startsWith(AgentITest.class.getPackage().getName() + ".test"));
        }
    }

    @Test
    public void agentShouldBeInitialized() throws InterruptedException
    {
        A a = new A();
        for(int i = 0; i < 2; i++)
        {
            a.foo();
        }

        assertThat(new PlantUmlEventListener().getEvents().size(), is(16));
    }

}
