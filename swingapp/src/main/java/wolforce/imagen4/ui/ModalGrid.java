package wolforce.imagen4.ui;

import wolforce.imagen4.data.Grid;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ModalGrid extends JDialog {

    private final String[] paramTypes;
    private final List<Integer> indexesToRender;
    private final Grid grid;
    private final JCheckBox[] checkBoxes;
    private final int nRows, nCols;

    private boolean isCancelled = true;

    public ModalGrid(String[] paramTypes, Grid dataGrid, List<Integer> indexesToRender, int nCols) {
        setModal(true);
        setLayout(new BorderLayout());

        this.paramTypes = paramTypes;
        this.indexesToRender = indexesToRender;
        this.grid = dataGrid;
        this.nRows = dataGrid.rowsNumber;
        this.checkBoxes = new JCheckBox[nRows];
        this.nCols = nCols;

        JPanel grid = makeGridPanel();
        JPanel buttons = makeButtonsPanel();

        JScrollPane scrollPane = new JScrollPane(grid);
        add(scrollPane, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        pack();
    }

//    private int calcMaxCols() {
//        int maxCols = 1;
//        for (int i = 0; i < nRows; i++) {
//            if (cols.length > maxCols) maxCols = cols.length;
//        }
//        return maxCols;
//    }

    private JPanel makeGridPanel() {
        var gridPanel = new JPanel();
        gridPanel.setLayout(new BoxLayout(gridPanel, BoxLayout.X_AXIS));

        JPanel[] colPanels = new JPanel[nCols + 1];

        for (int j = 0; j < colPanels.length; j++) {
            var panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            gridPanel.add(panel);
            panel.setBorder(BorderFactory.createEmptyBorder(0, j < 2 ? 5 : 20, 0, 0));

            if (j == 0) panel.add(new JLabel(" - "));
            else
//            else if (j < paramTypes.length - 1)
                panel.add(new JLabel(paramTypes[j - 1]));
//            else panel.add(new JLabel(" - "));

            colPanels[j] = panel;
        }

        for (int i = 0; i < nRows; i++) {

            JCheckBox checkbox = new JCheckBox();
            checkBoxes[i] = checkbox;
            if (indexesToRender.contains(i)) {
                checkbox.setSelected(true);
            }

            var dimCheckbox = new Dimension(checkbox.getPreferredSize().width, 20);
            checkbox.setPreferredSize(dimCheckbox);
            checkbox.setMaximumSize(dimCheckbox);
            checkbox.setMinimumSize(dimCheckbox);
            colPanels[0].add(checkbox);

            for (int j = 0; j < nCols; j++) {
                JLabel label = new JLabel(grid.get(i, j));
                var dim = new Dimension(label.getPreferredSize().width, 20);
                label.setPreferredSize(dim);
                label.setMaximumSize(dim);
                label.setMinimumSize(dim);
                colPanels[j + 1].add(label);
            }
        }

        return gridPanel;
    }

    private JPanel makeButtonsPanel() {

        JButton selectAllButton = new JButton("Select All");
        selectAllButton.addActionListener(x -> Arrays.stream(checkBoxes).forEach(cb -> cb.setSelected(true)));

        JButton selectNoneButton = new JButton("Select None");
        selectNoneButton.addActionListener(x -> Arrays.stream(checkBoxes).forEach(cb -> cb.setSelected(false)));

        JButton doneButton = new JButton("Done - Render Now");
        doneButton.setBackground(Color.decode("#1f5e0d"));
        doneButton.addActionListener(x -> {
            isCancelled = false;
            setVisible(false);
            dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.decode("#5e0d0d"));
        cancelButton.addActionListener(x -> {
            isCancelled = true;
            setVisible(false);
            dispose();
        });

        var buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        buttons.add(selectAllButton);
        buttons.add(selectNoneButton);
        buttons.add(cancelButton);
        buttons.add(doneButton);
        return buttons;
    }

    public Set<Integer> showAndGetIndexes() {
        var ints = IntStream.range(0, checkBoxes.length).filter(index -> checkBoxes[index].isSelected()).mapToObj(index -> Integer.parseInt(String.valueOf(index))).collect(Collectors.toSet());
        return isCancelled ? null : ints;
    }

}
