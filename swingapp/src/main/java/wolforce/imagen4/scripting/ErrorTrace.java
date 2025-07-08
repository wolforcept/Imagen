package wolforce.imagen4.scripting;

import wolforce.imagen4.data.Grid;
import wolforce.imagen4.ui.ProjectFrame;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ErrorTrace {

    private final List<String> errors = new LinkedList<>();
    private final StringBuilder trace = new StringBuilder();

    private int nErrors = 0;

    private long startTime, endTime, length;

    public ErrorTrace(Grid grid) {
        startTime = System.currentTimeMillis();
        trace.append("Starting render of ").append(grid.name).append("\n");
    }

    public void startingRow(Grid grid, int rowIndex) {
        var row = Arrays.stream(grid.get(rowIndex)).reduce((x, y) -> x + "," + y).orElse("");
        trace.append("Row ").append(rowIndex).append(": ").append(row).append("\n");
    }

    public void finishedRow() {
        trace.append("\n\n");
    }

    public void errored(String string) {
        trace.append("ERRORS:\n");
        trace.append(string);
        trace.append("\n\n");
        nErrors++;
    }

    public boolean hasErrors() {
        return nErrors > 0;
    }

    public int getNumberOfErrors() {
        return nErrors;
    }

    public void finished() {
        endTime = System.currentTimeMillis();
        length = endTime - startTime;
    }

    public void showFrame(JFrame parent) {

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date resultdate = new Date(startTime);
//        var timeLabel = new JLabel("Took : " + sdf.format(resultdate));
        var timeLabel = new JLabel("Took : " + (length / 1000) + " seconds");

        JTextArea log = new JTextArea();
        log.setEditable(false);
        log.setText(trace.toString());
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));

        JScrollPane scrollPane = new JScrollPane(log);
        scrollPane.setPreferredSize(new Dimension(600, 600));

        JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(timeLabel, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);

        frame.setLocationRelativeTo(parent);
        frame.pack();
        frame.setVisible(true);
    }

    public void startingOutputs() {
    }
}
