import java.util.*;
import java.util.stream.Collectors;

/**
 * Class with static methods to get Thread Groups/Thread info
 */
public class ThreadLister {

    /**
     * Gathers info for a thread group recursively and appends to list as a string
     * @param g ThreadGroup to iterate over
     * @param indent indenting for viewing pleasure
     * @param list List<String> to be updated
     * @param threadFilter if filtering threads, only threads containing this text will be added.
     */
    private static void getGroupInfo(ThreadGroup g, String indent, List<String> list, String threadFilter) {
        if (g != null) {
            int numThreads = g.activeCount();
            int numGroups = g.activeCount();
            Thread[] threads = new Thread[numThreads];
            ThreadGroup[] groups = new ThreadGroup[numGroups];

            g.enumerate(threads, false);
            g.enumerate(groups, false);

            list.add(indent + "Thread Group: " + g.getName() + " Max Priority: " + g.getMaxPriority());
            for (int i = 0; i < numThreads; i++)
                if (threadFilter.equals("") || (threads[i] != null && threads[i].getName().toLowerCase().contains(threadFilter.toLowerCase())))
                    list.add(getThreadInfo(threads[i], indent + "   "));
            for (int i = 0; i < numGroups; i++) {
                getGroupInfo(groups[i], indent + "   ", list, threadFilter);
            }
        }
    }

    /**
     * Method to get a ThreadGroup by name/containing part of name
     * @param name String to check if a ThreadGroup contains it
     * @param root The root ThreadGroup to start searching from
     * @return ThreadGroup that contains the string name, otherwise null
     */
    private static ThreadGroup getThreadGroupByName(String name,
                                                    ThreadGroup root) {
        if (root == null) {
            root = getRootThreadGroup();
        }
        if (root.getName().toLowerCase().contains(name.toLowerCase())) {
            return root;
        }
        int num_groups = root.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[num_groups];
        root.enumerate(groups, false);

        for (int i = 0; i < num_groups; i++) {
            ThreadGroup threadGroup = groups[i];
            if (threadGroup != null) {
                if (threadGroup.getName().contains(name) || name.equals("")) {
                    return threadGroup;
                } else {
                    threadGroup = getThreadGroupByName(name, threadGroup);
                    if (threadGroup != null) {
                        return threadGroup;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Method to display a Threads information
     * @param thread Thread to get information from
     * @param indent indentation to assist in structured display
     * @return String detailing thread information
     */
    private static String getThreadInfo(Thread thread, String indent) {
        if (thread == null)
            return "";
        return indent + "Thread: " + thread.getName() + "  Priority: " + thread.getPriority()
                + " Status: " + thread.getState() + " Id: " + thread.getId();
    }

    /**
     * Method to get a list of Threads
     * @param groupFilter String to filter ThreadGroups by
     * @param threadFilter String to filter Threads by
     * @return List<String> containing all ThreadGroups/Threads relating to the filters
     */
    public static List<String> getAllThreadsAsList(String groupFilter, String threadFilter) {
        ThreadGroup rootThreadGroup = getRootThreadGroup();

        if (!(Objects.equals(groupFilter, ""))) {
            rootThreadGroup = getThreadGroupByName(groupFilter,getRootThreadGroup());
        }

        List<String> list = new ArrayList<>();
        getGroupInfo(rootThreadGroup, "", list, threadFilter);
        list.removeAll(Arrays.asList("",null));
        return list;
    }

    /**
     * Returns the root ThreadGroup
     * @return ThreadGroup at the root of the ThreadGroup structure
     */
    private static ThreadGroup getRootThreadGroup() {
        ThreadGroup currentThreadGroup;
        ThreadGroup rootThreadGroup;
        ThreadGroup parent;

        // Get the current thread group
        currentThreadGroup = Thread.currentThread().getThreadGroup();

        // Now go find the root thread group
        rootThreadGroup = currentThreadGroup;
        parent = rootThreadGroup.getParent();
        while (parent != null) {
            rootThreadGroup = parent;
            parent = parent.getParent();
        }
        return rootThreadGroup;
    }

    /**
     * Helper method to gather all threads, will only return those matching the filter.
     * @return an array of Threads
     * @param threadFilter Filter for threads
     * @param groupFilter Filter for groups
     */
    public static Thread[] getAllThreads(String threadFilter, String groupFilter) {
        ThreadGroup root = getRootThreadGroup();
        int numThreads = root.activeCount();
        Thread[] threads = new Thread[numThreads];
        root.enumerate(threads,true);
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        List<Thread> threadList = threadSet.stream().filter(t -> t.getName().toLowerCase().contains(threadFilter.toLowerCase())).collect(Collectors.toList());
        threadList = threadList.stream().filter(t -> t.getThreadGroup().getName().toLowerCase().contains(groupFilter.toLowerCase())).collect(Collectors.toList());
        return threadList.toArray(new Thread[0]);
    }


}