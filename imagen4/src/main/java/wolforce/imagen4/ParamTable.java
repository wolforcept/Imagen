package wolforce.imagen4;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class ParamTable extends JPanel {

    private final Main main;
    private JTable table;
    private JScrollPane scrollPane;
    private JScrollBar vScroll;
    private DefaultTableModel dtm;
    public boolean isAutoScroll;
    public String[] types;
    private int[] lastRenderedRows = new int[] {};

    public ParamTable(Main main, String[] header, String[] types, List<String[]> rows) {
        this.main = main;
        this.types = types;

        dtm = new DefaultTableModel(null, header) {

            @Override
            public Class<?> getColumnClass(int col) {
                switch (types[col]) {
                    case "b":
                    case "bool":
                    case "boolean":
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }
        };

        table = new JTable(dtm) {
            @Override
            public void editingStopped(ChangeEvent e) {
                super.editingStopped(e);
                saveAll();
            }
        };

        for (String[] row : rows) {
            addNewRow(false, row);
        }

        table.putClientProperty("terminateEditOnFocusLost", true);
        scrollPane = new JScrollPane(table);
        vScroll = scrollPane.getVerticalScrollBar();

        for (int i = 0; i < types.length; i++) {

            if (types[i].equals("bigstring")) {
                TableColumn col = table.getColumnModel().getColumn(i);
                col.setMinWidth(300);
            }
            if (types[i].contains("|")) {
                String[] parts = types[i].split("\\|");
                TableColumn col = table.getColumnModel().getColumn(i);
                JComboBox<String> comboBox = new JComboBox<>();
                comboBox.addItem("");
                for (String part : parts)
                    comboBox.addItem(part);
                col.setCellEditor(new DefaultCellEditor(comboBox));
            }
        }

        this.setLayout(new BorderLayout());
        // Dimension d = new Dimension(320, N_ROWS * table.getRowHeight());
        // table.setPreferredScrollableViewportSize(d);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        vScroll.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                isAutoScroll = !e.getValueIsAdjusting();
            }
        });
        // add(table, BorderLayout.CENTER);
        // add(table.getTableHeader(), BorderLayout.NORTH);

        this.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(new JButton(new AbstractAction("Add Row") {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewRow(true);
            }
        }));
        buttonsPanel.add(new JButton(new AbstractAction("Render") {
            @Override
            public void actionPerformed(ActionEvent e) {
                renderSelected(true);
            }

        }));
        this.add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void saveAll() {
        List<String[]> list = new ArrayList<>(dtm.getRowCount());
        for (int i = 0; i < dtm.getRowCount(); i++) {
            String[] row = new String[dtm.getColumnCount()];
            for (int j = 0; j < row.length; j++) {
                row[j] = dtm.getValueAt(i, j).toString();
            }
            list.add(row);
        }
        // @SuppressWarnings("unchecked")
        // List<String[]> data = dtm.getDataVector().stream()
        // .map(x -> (String[]) x.stream().map(y -> y.toString()).toArray())
        // .toList();
        main.save(list);
        renderSelected(false);
    }

    private void addNewRow(boolean save, String... values) {

        Object[] row = new Object[types.length];
        for (int i = 0; i < row.length; i++) {
            switch (types[i]) {
                // case INT:
                // row[i] = values.length > i ? Integer.valueOf(values[i]) : Integer.valueOf(0);
                // break;
                // case FLOAT:
                // row[i] = values.length > i ? Float.valueOf(values[i]) : Float.valueOf(0.0f);
                // break;
                // case DOUBLE:
                // row[i] = values.length > i ? Double.valueOf(values[i]) : Double.valueOf(0.0);
                // break;
                case "b":
                case "bool":
                case "boolean":
                    try {
                        row[i] = values.length > i ? Boolean.valueOf(values[i]) : Boolean.valueOf(false);
                    } catch (RuntimeException e) {
                        row[i] = false;
                    }
                    break;
                case "s":
                case "str":
                case "string":
                default:
                    row[i] = values.length > i ? String.valueOf(values[i]) : String.valueOf("");
            }
        }

        dtm.addRow(row);
        if (save)
            saveAll();
    }

    public void renderSelected(boolean isNewRender) {
        if (isNewRender)
            lastRenderedRows = table.getSelectedRows();
        List<String[]> rows = new ArrayList<>(lastRenderedRows.length);
        for (int selectedRow : lastRenderedRows) {
            String[] row = new String[dtm.getColumnCount()];
            for (int i = 0; i < row.length; i++)
                row[i] = dtm.getValueAt(selectedRow, i) + "";
            rows.add(row);
        }
        main.render(rows, isNewRender);
    }

}