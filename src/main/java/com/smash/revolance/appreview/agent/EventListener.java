package com.smash.revolance.appreview.agent;

import java.util.List;
import java.util.Map;

/**
 * User: wsmash
 * Date: 28/03/14
 * Time: 23:24
 */
public interface EventListener
{
    void receive(Map<String, String> event);

    List<Map<String, String>> getEvents();
}
