
package mars.uwCommManager.benchmarking;

import java.util.LinkedList;
import java.util.List;
import mars.events.CommunicationType;
import mars.sensors.CommunicationMessage;
import mars.uwCommManager.CommunicationState;

/**
 *
 * @author Jasper Schwinghammer
 */
public class BenchmarkRunnable implements Runnable {

    CommunicationBenchmark benchmarkMain;
    CommunicationState comState;
    ISLogger log;
    List<String> auvList;
    private boolean started = false;
    private boolean cancel = false;

    public BenchmarkRunnable(CommunicationBenchmark benchmarkMain, CommunicationState comState, ISLogger log) {
        this.benchmarkMain = benchmarkMain;
        this.log = log;
        this.comState = comState;
        auvList = new LinkedList();
        auvList.add("jasper");
        auvList.add("laura");
        auvList.add("kat");
        auvList.add("raphael");
        auvList.add("monsun");
        //auvList.add("thomas");
        //auvList.add("ina");
        log.info(this.toString(), "Done initializing the runnable");

    }

    private synchronized void started() {
        if (!started) {
            started = true;
            log.info(this.toString(), "Started the Runnable");
        }
    }

    @Override
    public void run() {
        if (cancel) {
            return;
        }
        started();
        for (String i : auvList) {
            comState.putMsg(new CommunicationMessage(i,"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet." , CommunicationType.UNDERWATERSOUND));
            //comState.putMsg(new CommunicationMessage(i, "Jasper", CommunicationType.UNDERWATERSOUND));
        }

    }

    public void cancel() {
        cancel = true;
    }

}
