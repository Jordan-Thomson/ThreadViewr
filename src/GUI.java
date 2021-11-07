import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * Gui to see what's going on
 */
public class GUI extends JFrame {

    private final Threadr threadr;
    private final JLabel countdownLabel = new JLabel();

    /**
     * Set the scene
     * @param threadr
     */
    public GUI(Threadr threadr) {
        this.threadr = threadr;
        JList<String> listing = new JList<>(threadr.getModel());
        listing.setSelectionModel(new NoSelectionModel());
        add(new JScrollPane(listing), BorderLayout.CENTER);
        add(filterComponent(),BorderLayout.SOUTH);
        setPreferredSize(new Dimension(800,600));
        setupTimers();
        add(countdownLabel,BorderLayout.NORTH);
        add(controlPanel(),BorderLayout.EAST);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Create a refresh timer
     */
    private void setupTimers() {
        ActionListener task = e -> threadr.updateModel();
        Timer timer = new Timer(2000,task);
        timer.setRepeats(true);
        timer.start();
        ActionListener displayTimer = new ActionListener() {
            private int val = 2;
            @Override
            public void actionPerformed(ActionEvent e) {
                countdownLabel.setText("Refresh in: " + --val);
                val = (val == 0 ? 2 : val);
            }
        };
        Timer timer2 = new Timer(1000, displayTimer);
        timer2.setRepeats(true);
        timer2.start();
    }

    /**
     * Create filter component of the display
     * @return JPanel
     */
    public Component filterComponent() {
        JLabel groupLabel = new JLabel("Group Filter");
        JTextField groupFilterField = new JTextField();
        groupFilterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField actioned = (JTextField) e.getSource();
                String filterGroup = actioned.getText();
                threadr.setGroupFilter(filterGroup);
            }
        });

        JLabel threadLabel = new JLabel("Thread Filter");
        JTextField threadFilterField = new JTextField();
        threadFilterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField actioned = (JTextField) e.getSource();
                String filterThread = actioned.getText();
                threadr.setThreadFilter(filterThread);
            }
        });
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2,2));
        panel.add(groupLabel);
        panel.add(groupFilterField);
        panel.add(threadLabel);
        panel.add(threadFilterField);
        return panel;
    }

    /**
     * Create a panel with the control buttons
     * @return JPanel
     */
    public Component controlPanel() {

        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> {
            Thread[] threads = threadr.getAllThreads();
            Object[] options = Arrays.stream(threads).map(Thread::getName).toArray();

            Object option = JOptionPane.showInputDialog(null, options[0],"Menu",JOptionPane.PLAIN_MESSAGE, null,options,options[0]);
            if (option != null) {
                int index = Arrays.asList(options).indexOf(option);
                threadr.tryStop(threads[index]);
            }
        });
        panel.add(stopButton);
        JButton philosopherButton = new JButton("Philosopher");
        philosopherButton.addActionListener(e -> threadr.addPhilosophers());
        panel.add(philosopherButton);


        return panel;
    }

    /**
     * Custom Selection Model to stop index out of bounds errors if user were to select any objects
     * in a JList
     */
    private static class NoSelectionModel extends DefaultListSelectionModel {

        @Override
        public void setAnchorSelectionIndex(final int anchorIndex) {}

        @Override
        public void setLeadAnchorNotificationEnabled(final boolean flag) {}

        @Override
        public void setLeadSelectionIndex(final int leadIndex) {}

        @Override
        public void setSelectionInterval(final int index0, final int index1) { }
    }


}
