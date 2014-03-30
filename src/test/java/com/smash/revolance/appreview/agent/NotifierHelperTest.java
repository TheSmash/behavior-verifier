package com.smash.revolance.appreview.agent;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by ebour on 29/03/14.
 */
public class NotifierHelperTest
{
    @Test
    public void notifierHelperShouldParseClassAndPackageAndMethodFromFullMethodName()
    {
        String fullMethodName = "com.smash.revolance.appreview.agent.test.C.foo()";
        Map<String, String> event = NotifierHelper.buildEventData(fullMethodName);

        assertThat(event.get("packageName"), equalTo("com.smash.revolance.appreview.agent.test"));
        assertThat(event.get("className"  ), equalTo("C"));
        assertThat(event.get("methodName" ), equalTo("foo()"));
    }
}
