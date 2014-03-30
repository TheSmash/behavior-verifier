package com.smash.revolance.appreview.agent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ebour on 29/03/14.
 */
public class NotifierHelper
{

    public static Map<String, String> buildEventData(final String method)
    {
        final Map<String, String> data = new HashMap<String, String>();

        final String methodName = method.substring(method.lastIndexOf(".")+1, method.length());
        String className = method.substring(0, method.length()-methodName.length()-1);
        className = className.substring(className.lastIndexOf(".")+1, className.length());

        final String packageName = method.substring(0, method.length()-methodName.length()-1-className.length()-1);

        data.put("methodName",  methodName );
        data.put("className",   className  );
        data.put("packageName", packageName);

        return data;
    }

    public static Map<? extends String, ? extends String> buildEventData(String prependString, String methodName)
    {
        final Map<String, String> prependData = new HashMap();

        final Map<String, String> data = buildEventData(methodName);
        for(String key : data.keySet())
        {
            prependData.put(prependString+"."+key, data.get(key));
        }
        return prependData;
    }
}
