/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.benchmarking;

import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import mars.MARS_Main;
import mars.uwCommManager.CommunicationState;

/**
 *
 * @author Jasper Schwinghammer
 */
public class CommunicationBenchmark {

    private CommunicationState comState;
    private ISLogger logger;
    private TablePrinter tablePrinter;
    private BenchmarkRunnable runnable;
    private ScheduledThreadPoolExecutor executor;
    float initialDelay = 5;
    final float TICKSIZE = 0.25f;
    final float MAXTICKCOUNTER;
    final float DELAY_AFTER;
    float tickCounter;
    float currentTick = 0;
    float counter = 0;
    int runnableDelay = 10000;
    int runnablePeriod = 591;
    ScheduledFuture<?> f1;

    boolean firstCheck = false;
    boolean secoundcheck = false;

    public CommunicationBenchmark(CommunicationState comState, ScheduledThreadPoolExecutor executor) {
        this.comState = comState;
        this.executor = executor;
        MAXTICKCOUNTER = 1 / TICKSIZE * (20+(runnableDelay/1000)-initialDelay);
        DELAY_AFTER = 1 / TICKSIZE * 5;
        tickCounter = 0;

    }

    public boolean init() {
        Properties systemProps = System.getProperties();
        try {
            logger = SLogger.getNewLogger(systemProps.getProperty("user.name") + "-benchmarkLog", false, ISLogger.LOG_AND_CONSOLE, ISLogger.LOG_LEVEL_INFO);
            tablePrinter = new TablePrinter(systemProps.getProperty("user.name") + "-benchmarkResults.csv");
        } catch (Exception e) {
            return false;
        }
        SLoggerBasicMethod.LogSystemInformation(logger, "MARS-CommunicationBranch", "0.9");
        tablePrinter.init("step", "fpsValue");
        logger.info(this.toString(), "Logs and Tableprinters initialized");

        logger.info(this.toString(), "Initializing the BenchmarkRunnable");
        runnable = new BenchmarkRunnable(this, comState, logger);
        TimeUnit unit = TimeUnit.MILLISECONDS;
        logger.info(this.toString(), "Scheduling the runnable with: delay: " + runnableDelay + " period: " + runnablePeriod + " timeUnit: " + unit.toString());
        f1 =executor.scheduleAtFixedRate(runnable, runnableDelay, runnablePeriod, unit);
        logger.info(this.toString(), "This Benchmark will run: " + MAXTICKCOUNTER + " ticks á " +TICKSIZE+ " millisecounds ");
        logger.info(this.toString(), " Schedule: initialdelay: " +initialDelay + " secounds. runnableDelay: "+runnableDelay/1000*1/TICKSIZE+ " ticks. delay after: " + DELAY_AFTER + " ticks." );

        logger.info(this.toString(), "System up and running");
        return true;
    }

    public void update(float tpf) {
        if (!firstCheck) {
            firstCheck = true;
            logger.info(this.toString(), "Integrated into mainloop, waiting for the initial delay");
            return;
        }
        if (initialDelay > 0) {
            initialDelay -= tpf;
            return;
        }
        if (!secoundcheck) {
            secoundcheck = true;
            logger.info(this.toString(), "done delaying, starting the benchmark");
        }
        counter++;
        currentTick += tpf;
        if (currentTick >= TICKSIZE) {
            float error = TICKSIZE - currentTick;
            if (error > 0) {
                logger.info(this.toString(), "Tick completed. Error: " + error);
            }
            tablePrinter.addValue(Float.toString(1f / (currentTick / (float) counter)));
            currentTick = 0;
            counter = 0;
            tickCounter++;
        }

        if (tickCounter >= MAXTICKCOUNTER && runnable != null) {
            logger.info(this.toString(), "Spamtest over, shuting down runnable");
            if(!f1.cancel(true)) logger.debug("Runnable was not stopped");
            runnable.cancel();
            runnable = null;
            tickCounter -= DELAY_AFTER;
        }

        if (tickCounter >= MAXTICKCOUNTER) {
            endBenchmark();
        }

    }

    private void endBenchmark() {
        logger.info(this.toString(), "Benchmark done, shutting down Systems");
        tablePrinter.close();
        logger.shutdown();
        System.exit(0);
    }
}
