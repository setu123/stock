package com.mycompany.service;

import com.mycompany.schedule.TestJob;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @date Apr 20, 2015
 * @author Setu
 */
@WebListener
public class MyListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            createSchedulerWithJob(sce.getServletContext()).start();
//            Class.forName("org.quartz.DisallowConcurrentExecution");
//            Class.forName("org.quartz.utils.ClassUtils");
//            Class.forName("org.quartz.spi.TriggerFiredBundle");
//            Class.forName("org.quartz.spi.TriggerFiredResult");
//            Class.forName("org.quartz.ExecuteInJTATransaction");
//            Class.forName("org.quartz.core.JobRunShell");
//            Class.forName("org.quartz.PersistJobDataAfterExecution");
//            Class.forName("org.quartz.impl.JobExecutionContextImpl");
//            System.out.println("Scheduler initiated");
        } catch (SchedulerException ex) {
            Logger.getLogger(MyListener.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Exception: " + ex);
        }
    }

    private Scheduler createSchedulerWithJob(ServletContext context) throws SchedulerException {
        
        JobDataMap jdMap = new JobDataMap();
        jdMap.put("context", context);
        String cronExpression = context.getInitParameter("CRON-EXPRESSION");

        JobDetail testJob = JobBuilder.newJob(TestJob.class).usingJobData(jdMap).withIdentity("testJob").build();
        JobDetail syncJob = JobBuilder.newJob(SyncService.class).usingJobData(jdMap).withIdentity("syncJob").build();
        
        
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("crontrigger", "crontriggergroup1").withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
        SchedulerFactory schFactory = new StdSchedulerFactory();
        Scheduler sch = schFactory.getScheduler();
        sch.scheduleJob(syncJob, cronTrigger);

        return sch;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
