import java.util.*;

public class Threadr {

    private Set<Thread> threadSet;

    public Threadr() {
        updateThreadSet();
        ThreadGroup tg1 = new ThreadGroup("Example 1");
        ThreadGroup tg2 = new ThreadGroup("Example 2");
        ThreadGroup tg11 = new ThreadGroup("Example 1.1");
        createThread("my_thread_1",tg1);
        createThread("kill_me",tg2);
        createThread("Super Important",tg11);
    }

    /**
     * Gets all threads as a set
     */
    public void updateThreadSet() {
        threadSet = Collections.synchronizedSet(Thread.getAllStackTraces().keySet());
    }

    /**
     * Data transforms a Set<Thread> into an Object[][] to be processed as a table in a gui
     * @param groupFilter String to be applied as filter to Thread.getThreadGroup()
     * @param nameFilter String to be applied as filter to Thread.getName()
     * @return Object[][] of Thread details
     */
    public Object[][] getThreadArray(String groupFilter, String nameFilter) {
        updateThreadSet();
        int index = 0;

        // use array list checking if the thread still exists and applying filters
        ArrayList<Thread> threadList = new ArrayList<>();
        for (Thread thread : threadSet) {
            if (Thread.getAllStackTraces().containsKey(thread)) {
                if ((thread.getThreadGroup().getName().toLowerCase().startsWith(groupFilter.toLowerCase()) || groupFilter.equals(""))
                && (thread.getName().toLowerCase().startsWith(nameFilter.toLowerCase()) || nameFilter.equals(""))) {
                    threadList.add(thread);
                }
            }
        }

        // convert the arraylist to a 2d Object array
        final Object[][] threadArray = new Object[threadList.size()][6];
        for (Thread thread : threadList) {
            if (Thread.getAllStackTraces().containsKey(thread)) {
                threadArray[index][0] = thread.getThreadGroup().getName();
                threadArray[index][1] = thread.getName();
                threadArray[index][2] = thread.getState();
                threadArray[index][3] = thread.getPriority();
                threadArray[index][4] = thread.getId();
                threadArray[index][5] = thread;

                index++;
            }
        }
        Sort2DArrayBasedOnColumnNumber(threadArray, 0);  // sort by column
        return threadArray;
    }

    /**
     * Helper method to sort a 2d array by column values
     * @param array 2d array to be sorted
     * @param column int index of column to sort by
     */
    public void Sort2DArrayBasedOnColumnNumber(Object[][] array, int column) {
        Arrays.sort(array, new Comparator<Object[]>() {
            @Override
            public int compare(Object[] first, Object[] second) {
                Object obj = first[5];
                if (obj instanceof Thread) {
                    if (!Thread.getAllStackTraces().containsKey(obj)) {
                        return -1;
                    }
                }
                if (first[column] == null) {
                    return -1;
                }
                if (column == 0 || column == 1 || column == 2) {
                    return first[column].toString().compareTo(second[column].toString());
                }
                else {
                    if (Integer.parseInt(first[column].toString()) > Integer.parseInt(second[column].toString())) return 1;
                    else return -1;
                }
            }
        });
    }

    /**
     * Attempt to interrupt a thread
     * @param selected Thread being chosen for interrupt
     */
    public void interruptThread(Object selected) {
        if (selected instanceof Thread) {
            threadSet.remove(selected);
            ((Thread) selected).interrupt();
        }
    }

    /**
     * Helper method to create a thread for a ThreadGroup
     * @param name String name of the Thread
     * @param tg ThreadGroup to associate with the thread, null will use the current ThreadGroup
     */
    public void createThread(String name, ThreadGroup tg) {
        if (tg == null) {
            tg = Thread.currentThread().getThreadGroup();
        }
        Thread t = new Thread(tg,name) {
            public void run() {
                boolean running = true;
                while (running) {
                    try {
                        Thread.sleep(5000);
                    } catch(InterruptedException e) {
                        this.interrupt();
                        System.out.println(this.getName() + " stopping");
                        running = false;
                    }
                }
                System.out.println(this.getName() + " Stopped!!");
            }
        };
        t.start();
    }

    public static void main(String[] args) {
        new GUI(new Threadr());
    }

    /**
     * Method to iterate over all threads and attempt to interrupt to allow them to safely close
     * @param tg ThreadGroup to iterate over, if none supplied then root is chosen and iterates from there.
     */
    public void safeCloseThreads(ThreadGroup tg) {
        if (tg == null) {
            tg = Thread.currentThread().getThreadGroup();
        }
        int num_threads = tg.activeCount();
        int num_groups = tg.activeGroupCount();
        Thread[] threads = new Thread[num_threads];
        ThreadGroup[] groups = new ThreadGroup[num_groups];
        tg.enumerate(threads,false);
        tg.enumerate(groups, false);
        for (int i = 0; i < num_groups; i++) {
            safeCloseThreads(groups[i]);
        }
        for (int i = 0; i < num_threads - num_groups; i++) {
            threads[i].interrupt();
        }
    }

}
