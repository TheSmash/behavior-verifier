package com.smash.revolance.appreview.agent;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by ebour on 29/03/14.
 */
public class AgentHelperTest
{
    @Test
    public void helperShouldFilterPackagesOfClassToBeInstrumented()
    {
        Set packages = new HashSet<String>();
        packages.add("foo.bar");

        assertThat(AgentHelper.isInPackages("foo.Class", packages), is(false));
        assertThat(AgentHelper.isInPackages("foo.bar.Class", packages), is(true));
    }
}
