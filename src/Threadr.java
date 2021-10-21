import java.util.*;

public class Threadr {

    private Set<Thread> threadSet;


    public Threadr() {
        updateThreadSet();
        createThread("my_thread_1");
        createThread("kill_me");
        createThread("Super Important");

    }

    public void updateThreadSet() {
        threadSet = Collections.synchronizedSet(Thread.getAllStackTraces().keySet());

    }

    public Object[][] getThreadArray(String groupFilter) {

        updateThreadSet();
        int index = 0;

        ArrayList<Thread> threadList = new ArrayList<>();
        for (Thread thread : threadSet) {
            if (Thread.getAllStackTraces().containsKey(thread)) {
                if (thread.getThreadGroup().getName().toLowerCase().startsWith(groupFilter.toLowerCase()) || groupFilter.equals("")) {
                    threadList.add(thread);
                }
            }
        }

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


        /*final Object[][] threadArray = new Object[threadSet.size()][6];

        for (Thread thread : threadSet) {
            if (Thread.getAllStackTraces().containsKey(thread)) {
                if (thread.getThreadGroup().getName().toLowerCase().startsWith(groupFilter) || groupFilter.equals("")) {
                    threadArray[index][0] = thread.getThreadGroup().getName();
                    threadArray[index][1] = thread.getName();
                    threadArray[index][2] = thread.getState();
                    threadArray[index][3] = thread.getPriority();
                    threadArray[index][4] = thread.getId();
                    threadArray[index][5] = thread;

                    index++;
                }
            }
        }*/
        System.out.println("num items = " + index);
        Sort2DArrayBasedOnColumnNumber(threadArray, 0);

        return threadArray;
    }

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

    public void interruptThread(Object selected) {
        if (selected instanceof Thread) {
            threadSet.remove(selected);
            ((Thread) selected).interrupt();
        }
    }

    public void createThread(String name) {
        Thread t = new Thread(name) {
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
                System.out.println(this.getName() + "Stopped!!");
            }
        };
        t.start();
    }

    public static void main(String[] args) {
        new GUI(new Threadr());
    }
}
