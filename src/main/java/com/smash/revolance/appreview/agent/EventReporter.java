package com.smash.revolance.appreview.agent;

import java.io.File;

/**
 * Created by ebour on 30/03/14.
 */
public interface EventReporter
{
    void setReportDir(String reportDir);
    void doReport(String filename) throws Exception;
}
