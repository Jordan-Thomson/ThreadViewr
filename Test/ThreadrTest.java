import org.junit.Test;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ThreadrTest {

    private Threadr t = new Threadr();

    @Test
    public void getModel() {
        DefaultListModel<String> testModel = t.getModel();
        assertNotNull(testModel);
        assertEquals(testModel.firstElement(),"Thread Group: system Max Priority: 10");
    }

    @Test
    public void updateModel() {
        t.updateModel();
        getModel(); // nothing should really change
    }

    @Test
    public void setGroupFilter() {
        t.setGroupFilter("main");
        t.updateModel();
        assertEquals(t.getModel().firstElement(),"Thread Group: main Max Priority: 10");
        t.setGroupFilter("");
        t.updateModel();
        assertEquals(t.getModel().firstElement(),"Thread Group: system Max Priority: 10");
    }

    @Test
    public void setThreadFilter() {
        t.addPhilosophers();
        t.setThreadFilter("Phil");
        t.updateModel();
        // as ID's and states can vary, so unable to verify the full String
        assertEquals(t.getModel().firstElement(),"Thread Group: system Max Priority: 10"); // 0 = ThreadGroup system
        assertEquals(t.getModel().getElementAt(1),"   Thread Group: main Max Priority: 10");// 1 = ThreadGroup main
        assertEquals(t.getModel().getElementAt(2),"      Thread Group: Philosophers Max Priority: 10");// 2 = ThreadGroup Philosophers
        assertTrue(t.getModel().getElementAt(3).contains("Philosopher")); // 4 = Thread Philosopher 1
    }

    @Test
    public void getAllThreads() {
        Thread[] threads = t.getAllThreads();
        t.addPhilosophers(); // add 5 more threads
        Thread[] secondThreads = t.getAllThreads();
        assertEquals(5, secondThreads.length - threads.length);
    }

    @Test
    public void addPhilosophers() {
        // covered by getAllThreads
    }

    @Test
    public void tryStop() throws InterruptedException {
        t.addPhilosophers();
        Thread[] threads = t.getAllThreads();
        List<Thread> interrupted = new ArrayList<>();
        for (Thread thread : threads) {
            if (thread.getName().contains("Philosopher")) {
                interrupted.add(thread);
                t.tryStop(thread);
            }
        }
        Thread.sleep(5000); // give the threads time to die.
        for (Thread thread : interrupted) {
            assertFalse(thread.isAlive());
        }
        Thread[] newThreads = t.getAllThreads();
        assertEquals(5, threads.length - newThreads.length);

    }
}