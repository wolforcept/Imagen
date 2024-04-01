package wolforce.imagen4;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class ParamTable {

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

        table.putClientProperty("terminateEditOnFocusLost", true);
        scrollPane = new JScrollPane(table);
        vScroll = scrollPane.getVerticalScrollBar();

        for (String[] row : rows)
            addNewRow(false, row);

        int[] colSizes = new int[types.length];
        for (int i = 0; i < colSizes.length; i++)
            colSizes[i] = 0;

        for (int rowIndex = 0; rowIndex < colSizes.length; rowIndex++) {
            for (int colIndex = 0; colIndex < colSizes.length; colIndex++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(rowIndex, colIndex);
                Component c = table.prepareRenderer(cellRenderer, rowIndex, colIndex);
                int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
                if (width > colSizes[colIndex])
                    colSizes[colIndex] = (int) (width * 1.5);
            }
        }
        for (int i = 0; i < colSizes.length; i++)
            System.out.println(colSizes[i]);

        for (int i = 0; i < types.length; i++) {

            if (types[i].equals("bigstring")) {
                if (colSizes[i] < 300)
                    colSizes[i] = 300;
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

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        vScroll.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                isAutoScroll = !e.getValueIsAdjusting();
            }
        });

        // set preferred widths
        int totalWidth = 0;
        for (int i = 0; i < colSizes.length; i++) {
            totalWidth += colSizes[i];
            table.getColumnModel().getColumn(i).setPreferredWidth(colSizes[i]);
        }
        table.setPreferredSize(new Dimension(totalWidth, (int) table.getPreferredSize().getHeight()));
        scrollPane.setPreferredSize(new Dimension(totalWidth, (int) scrollPane.getPreferredSize().getHeight()));
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
        // renderSelected(false);
    }

    public void addNewRow(boolean save, String... values) {

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

    public List<String[]> getSelectedRows(boolean isNewRender) {
        if (isNewRender)
            lastRenderedRows = table.getSelectedRows();
        List<String[]> rows = new ArrayList<>(lastRenderedRows.length);
        for (int selectedRow : lastRenderedRows) {
            String[] row = new String[dtm.getColumnCount()];
            for (int i = 0; i < row.length; i++)
                row[i] = dtm.getValueAt(selectedRow, i) + "";
            rows.add(row);
        }
        return rows;
    }

    public Component get() {
        return scrollPane;
    }

    // public void renderSelected(boolean isNewRender) {
    // if (isNewRender)
    // lastRenderedRows = table.getSelectedRows();
    // List<String[]> rows = new ArrayList<>(lastRenderedRows.length);
    // for (int selectedRow : lastRenderedRows) {
    // String[] row = new String[dtm.getColumnCount()];
    // for (int i = 0; i < row.length; i++)
    // row[i] = dtm.getValueAt(selectedRow, i) + "";
    // rows.add(row);
    // }
    // main.render(rows, isNewRender);
    // }

}