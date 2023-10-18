package ru.job4j.quartz;


import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    private static Connection connection;

    public static void init() {
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("./src/main/resources/rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) {
        try {
            Properties config = config();
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("store", store);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(config.getProperty("rabbit.interval")))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(store);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            init();
            System.out.println("Rabbit runs here ...");
            List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
            store.add(System.currentTimeMillis());
            LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
            try (PreparedStatement statement =
                         connection.prepareStatement("insert into rabbit (created_date) values (?)",
                                 Statement.RETURN_GENERATED_KEYS)) {
                Timestamp timestamp = Timestamp.valueOf(created);
                statement.setTimestamp(1, timestamp);
                statement.execute();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static Properties config() throws Exception {
        Properties cfg = new Properties();
        try (FileInputStream in = new FileInputStream("./src/main/resources/rabbit.properties")) {
            cfg.load(in);
        }
       return cfg;
    }
}



