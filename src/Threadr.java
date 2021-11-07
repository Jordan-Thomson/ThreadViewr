import javax.swing.*;
import java.util.List;

/**
 * Simple class to manage Threads
 */
public class Threadr {

    private final DefaultListModel<String> model = new DefaultListModel<>();
    private String groupFilter = "";
    private String threadFilter = "";
    private ThreadGroup philosopherGroup;

    public Threadr() {
        updateModel();
    }

    /**
     * Allows gui to get the listModel
     * @return DefaultListModel<String>
     */
    public DefaultListModel<String> getModel() {
        return model;
    }

    /**
     * Updates the listModel, refreshing it
     */
    public void updateModel() {
        List<String> threadInfo = ThreadLister.getAllThreadsAsList(groupFilter, threadFilter);
        model.removeAllElements();
        for (String t : threadInfo) {
            model.addElement(t);
        }
    }


    public static void main(String[] args) {
        new GUI(new Threadr());
    }

    /**
     * Update the ThreadGroup filter text
     * @param text to be used in filtering ThreadGroups
     */
    public void setGroupFilter(String text) {
        groupFilter = text;
    }

    /**
     * Update the Thread filter text
     * @param text to be used in filtering Threads
     */
    public void setThreadFilter(String text) {
        threadFilter = text;
    }

    /**
     * Collects an array of current Threads
     * @return array of Thread
     */
    public Thread[] getAllThreads() {
        return ThreadLister.getAllThreads(threadFilter,groupFilter);
    }

    /**
     * Method to create Philosophers based on damien's code allowing a better view of the
     * refresh running with state changes
     */
    public void addPhilosophers() {
        if (philosopherGroup == null) {
            philosopherGroup = new ThreadGroup("Philosophers");
        }
        Philosopher[] philosophers = new Philosopher[5];
        Object[] forks = new Object[philosophers.length+2];
        for(int i = 0; i < forks.length; i ++) {
            forks[i] = new Object();
        }

        for (int i = 0; i < philosophers.length; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % forks.length];

            philosophers[i] = new Philosopher(leftFork, rightFork);

            Thread thread = new Thread(philosopherGroup, philosophers[i],"Philosopher " + (i+1));
            thread.start();
        }

    }

    /**
     * Method to attempt interrupting a Thread
     * ** Note: This doesn't work on system threads, protection added to Swing SWT Threads
     * @param thread Thread to try and interrupt
     */
    public void tryStop(Thread thread) {
        System.out.println(thread.getName() + " interrupt attempted");
        if (!thread.getName().contains("AWT")) {
            thread.interrupt();
        }
    }
}
