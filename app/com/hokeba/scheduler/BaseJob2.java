package com.hokeba.scheduler;

import akka.actor.Cancellable;
import play.Logger;
import play.libs.Akka;
import play.libs.Time.CronExpression;
import scala.concurrent.duration.FiniteDuration;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.hokeba.util.CommonFunction;

public abstract class BaseJob2 extends Thread implements Cancellable{

    private String cornFinal;
    private int scheduleInterval;
    private TimeUnit scheduleIntervalTimeUnit;
    private boolean isCanceled;

    public BaseJob2(String corn) {
        this.cornFinal = corn;
    }
    
    public BaseJob2(String corn, int scheduleInterval, TimeUnit scheduleIntervalTimeUnit) {
    	this.cornFinal = corn;
    	this.scheduleInterval = scheduleInterval;
    	this.scheduleIntervalTimeUnit = scheduleIntervalTimeUnit;
    }

    public abstract void doJob();

    public abstract String getScheduleName();
    
    @Override
    public void run() {
//        Logger.debug("Running scheduler :" + cornFinal);

        try {
            doJob();
        } catch (Exception e) {
            Logger.error("doJob error", e);
        }
    }

    public Cancellable schedule() {
    	FiniteDuration duration = getScheduleTime();
    	FiniteDuration interval = getScheduleInterval();
    	return (interval == null) ?
    			Akka.system().scheduler()
                .scheduleOnce(duration, this, Akka.system().dispatcher()) :
    			Akka.system().scheduler()
                .schedule(duration, interval, this, Akka.system().dispatcher());
    }
    
    public Cancellable scheduleOnceAfterInterval() {
    	FiniteDuration interval = getScheduleInterval();
    	return (interval == null) ? null :
    			Akka.system().scheduler()
                .scheduleOnce(interval, this, Akka.system().dispatcher());
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
        FiniteDuration d = getScheduleTime();
        FiniteDuration oneMinute = FiniteDuration.create(1, TimeUnit.MINUTES);
        return Akka.system().scheduler()
                .schedule(d, oneMinute, this, Akka.system().dispatcher());
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
            CronExpression e = new CronExpression(cornFinal);
            Date currentDate = new Date(System.currentTimeMillis());
            Date nextValidTimeAfter = e.getNextValidTimeAfter(currentDate);
            long dateDiff = CommonFunction.getDateDifference(nextValidTimeAfter, currentDate, TimeUnit.MILLISECONDS);

            d = FiniteDuration.create(dateDiff, TimeUnit.MILLISECONDS);

            Logger.debug(getScheduleName() + ": " + "Scheduling to run at " + nextValidTimeAfter);
        } catch (ParseException e) {
            Logger.error("BASEJOB", e);
        }
        return d;
    }
    
    public FiniteDuration getScheduleInterval() {
        if (this.scheduleIntervalTimeUnit != null && this.scheduleInterval > 0) {
        	return FiniteDuration.create(scheduleInterval, scheduleIntervalTimeUnit);
        } else {
        	return null;
        }
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
