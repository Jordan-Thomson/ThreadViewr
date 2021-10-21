import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;


public class GUI extends JFrame {

    private Threadr threadr;
    private JTable table = new JTable();
    private DefaultTableModel model;
    private Object[][] threads;
    private Thread refresh;
    private String filterGroup = "";
    private String filterName = "";

    public GUI(Threadr threadr) {
        this.threadr = threadr;

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        table.setDefaultEditor(Object.class, null);
        table.setDragEnabled(false);
        getTableModel(filterGroup, filterName);

        JScrollPane scrollPane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(500, 150));

        panel.add(scrollPane);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.PAGE_AXIS));
        JButton stop = new JButton("Stop");
        stop.addActionListener(e -> {
            int selected = table.getSelectedRow();
            threadr.interruptThread(table.getModel().getValueAt(selected,5));
            table.clearSelection();
            getTableModel(filterGroup, filterName);
            getTableModel(filterGroup, filterName);
        });
        buttonPanel.add(stop);
        contentPane.add(buttonPanel,BorderLayout.EAST);
        contentPane.add(filter(),BorderLayout.SOUTH);
        setTitle("Thread Viewer");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                dispose();  // stop the program when windows 'x' is clicked
            }
        });
        setSize(new Dimension(600, 450));
        setVisible(true);

        ActionListener task = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getTableModel(filterGroup, filterName);
            }
        };
        Timer timer = new Timer(10000,task);
        timer.setRepeats(true);
        timer.start();


        //refresh.start();

    }

    public Component filter() {
        JTextField groupFilter = new JTextField();
        groupFilter.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField actioned = (JTextField) e.getSource();
                filterGroup = actioned.getText();
                getTableModel(filterGroup,filterName);
            }
        });
        JPanel filterPanel = new JPanel();

        filterPanel.setLayout(new GridLayout(0,2));
        filterPanel.add(new JLabel("Group Filter: "));
        filterPanel.add(groupFilter);
        JTextField nameFilter = new JTextField();
        nameFilter.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField actioned = (JTextField) e.getSource();
                filterName = actioned.getText();
                getTableModel(filterGroup,filterName);
            }
        });
        /*nameFilter.addActionListener(e -> {
            filterName = nameFilter.getText();
            getTableModel(filterGroup, filterName);
        });*/
        filterPanel.add(new JLabel("Name Filter: "));
        filterPanel.add(nameFilter);
        return filterPanel;
    }

    public Thread makeRefresh(){
        return new Thread("RefreshThread") {
            public void run() {
                boolean running = true;
                while (running) {
                    try {
                        Thread.sleep(5000);
                        getTableModel(filterGroup, filterName);
                    } catch(InterruptedException e) {
                        this.interrupt();
                        System.out.println("Refresh stopping");
                        running = false;
                    }
                }
                System.out.println("Refresh Stopped");
            }
        };
    }


    private void getTableModel(String groupFilter, String nameFilter) {
        try {
            table.clearSelection();
            threads = threadr.getThreadArray(groupFilter, nameFilter);
            String[] headings = new String[]{"Thread Group", "Name", "State", "Priority", "ID", "Thread"};
            model = new DefaultTableModel(threads, headings);
            model.addTableModelListener(table);
            table.setModel(model);
            TableColumnModel tcm = table.getColumnModel();
            tcm.removeColumn(tcm.getColumn(5));

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Refresh issue");
        } catch (NullPointerException e2) {
            System.out.println("Null pointer issue");
        }

    }

}