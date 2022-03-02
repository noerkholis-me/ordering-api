package com.hokeba.scheduler;

import akka.actor.Cancellable;
import play.Logger;
import play.libs.Akka;
import play.libs.Time.CronExpression;
import scala.concurrent.duration.FiniteDuration;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by hendriksaragih on 7/2/17.
 */
public abstract class BaseJob implements Runnable, Cancellable{

    private String cronFinal;
    private boolean isCanceled;

    public BaseJob(String corn) {
        this.cronFinal = corn;
    }

    public abstract void doJob();

    @Override
    public void run() {
//        Logger.debug("Ruuning scheduler :" + cornFinal);

        try {
            doJob();
        } catch (Exception e) {
            Logger.error("doJob error", e);
        }
    }

    /**
     * schedule every 1 day
     */
    public Cancellable scheduleInterval() {
        FiniteDuration d = getScheduleTime();
        FiniteDuration oneDay = FiniteDuration.create(1, TimeUnit.DAYS);
        return Akka.system().scheduler()
                .schedule(d, oneDay, this, Akka.system().dispatcher());
    }

    /**
     * schedule every 1 minute
     */
    public Cancellable scheduleIntervalMinutes() {
        return scheduleIntervalMinutes(1);
    }

    /**
     * schedule every 1 minute
     */
    public Cancellable scheduleIntervalMinutes(int minutes) {
        FiniteDuration d = getScheduleTime();
        FiniteDuration oneDay = FiniteDuration.create(minutes, TimeUnit.MINUTES);
        return Akka.system().scheduler()
                .schedule(d, oneDay, this, Akka.system().dispatcher());
    }

    /**
     * schedule once
     */
    public Cancellable scheduleOnce() {
        FiniteDuration d = getScheduleTime();
        return Akka.system().scheduler()
                .scheduleOnce(d, this, Akka.system().dispatcher());
    }

    public FiniteDuration getScheduleTime() {
        FiniteDuration d = null;
        try {
            CronExpression e = new CronExpression(cronFinal);
            Date nextValidTimeAfter = e.getNextValidTimeAfter(new Date());
            d = FiniteDuration.create(
                    nextValidTimeAfter.getTime() - System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS);

            Logger.debug("Scheduling to run at " + nextValidTimeAfter);
        } catch (ParseException e) {
            Logger.error("BASEJOB", e);
        }
        return d;
    }

    @Override
    public boolean cancel() {
        isCanceled = true;
        return isCanceled;
    }

    @Override
    public boolean isCancelled() {
        return isCanceled;
    }
}