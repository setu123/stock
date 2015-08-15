package com.mycompany.schedule;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @date Apr 20, 2015
 * @author Setu
 */
public class TestJob implements Job{    

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        JobDataMap data = jec.getJobDetail().getJobDataMap();
        
        System.out.println("This is + job: " + data.get("context"));
    }

}
