package wolforce.imagen4.ui;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

public class StartupFrame {

    private static final Path recentProjectsPath = new File("recent.projects").toPath();
    private static final String saveLineSeparator = "\"";

    record ProjectInfo(String name, String path) {

        public static ProjectInfo fromSaveLine(String line) {
            String[] parts = line.split(saveLineSeparator);
            String name = parts.length > 0 ? parts[0] : "";
            String path = parts.length > 1 ? parts[1] : "";
            return new ProjectInfo(name, path);
        }

        public String toSaveLine() {
            return name + saveLineSeparator + path;
        }

        public String toString() {
            return name + " - " + path;
        }
    }

    private JFrame frame;
    private JList<ProjectInfo> projectsList;

    public StartupFrame(boolean shouldAutoOpen) {

        var projectsFromFile = getProjectsFromFile();

        if (shouldAutoOpen && !projectsFromFile.isEmpty()) {
            openProject(projectsFromFile.get(0));
            return;
        }

        projectsList = new JList<>();
        projectsList.setFixedCellHeight(50);
        projectsList.setFont(projectsList.getFont().deriveFont(16f));
        projectsList.setPreferredSize(new Dimension(500, 500));
        updateProjectsList(projectsFromFile);

        int buttonSize = 100;

        JButton openProjectButton = new JButton("Open");
        openProjectButton.setPreferredSize(new Dimension(buttonSize, openProjectButton.getPreferredSize().height));
        openProjectButton.addActionListener(this::onOpenProjectClicked);

        JButton newProjectButton = new JButton("Add");
        newProjectButton.setPreferredSize(new Dimension(buttonSize, newProjectButton.getPreferredSize().height));
        newProjectButton.addActionListener(this::onAddProjectClicked);

        JButton removeProjectButton = new JButton("Remove");
        removeProjectButton.setPreferredSize(new Dimension(buttonSize, removeProjectButton.getPreferredSize().height));
        removeProjectButton.addActionListener(this::onRemoveProjectClicked);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();

        buttonsPanel.add(openProjectButton, cons);
        buttonsPanel.add(newProjectButton, cons);
        buttonsPanel.add(removeProjectButton, cons);

        frame = new JFrame();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(projectsList, BorderLayout.CENTER);
        content.add(buttonsPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void updateProjectsList(LinkedList<ProjectInfo> projects) {
        var arr = projects.toArray(ProjectInfo[]::new);
        projectsList.setModel(new AbstractListModel<>() {
            @Override
            public int getSize() {
                return arr.length;
            }

            @Override
            public ProjectInfo getElementAt(int i) {
                return arr[i];
            }
        });
//        projectsList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> new JLabel(value.toViewLine()));
    }

    private void onOpenProjectClicked(ActionEvent ev) {
        ProjectInfo proj = projectsList.getSelectedValue();
        if (proj == null) return;
        openProject(proj);
    }

    private File showFileChooser() {
        JFileChooser chooser = new JFileChooser() {
            public void approveSelection() {
                if (getSelectedFile().isDirectory()) super.approveSelection();
            }
        };

        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Folders only";
            }
        });
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        var result = chooser.showDialog(frame, "Select Folder");
        if (result != JFileChooser.APPROVE_OPTION) return null;
        return chooser.getSelectedFile();
    }

    private void onAddProjectClicked(ActionEvent ev) {
        File file = showFileChooser();
        if (file == null) return;
        ProjectInfo proj = new ProjectInfo(file.getName(), file.getAbsolutePath());
        LinkedList<ProjectInfo> projects = getProjectsFromFile();
        projects.add(proj);
        updateProjectsList(projects);
        saveProjectsToFile(projects);
    }

    private void onRemoveProjectClicked(ActionEvent ev) {
        ProjectInfo proj = projectsList.getSelectedValue();
        if (proj == null) return;

        LinkedList<ProjectInfo> list = getProjectsFromFile();
        list.remove(proj);

        saveProjectsToFile(list);
        updateProjectsList(list);
    }

    private void moveToTop(ProjectInfo proj) {
        var projects = getProjectsFromFile();
        projects.remove(proj);
        projects.add(0, proj);
        saveProjectsToFile(projects);
    }

    private LinkedList<ProjectInfo> getProjectsFromFile() {
        if (!new File(recentProjectsPath.toUri()).exists()) saveProjectsToFile(new LinkedList<>());
        try {
            return new LinkedList<>(Files.readAllLines(recentProjectsPath).stream().map(ProjectInfo::fromSaveLine).toList());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to retrieve recent projects");
            return new LinkedList<>();
        }
    }

    private void saveProjectsToFile(LinkedList<ProjectInfo> projects) {
        var projectLines = projects.stream().map(ProjectInfo::toSaveLine).toList();
        try {
            Files.write(recentProjectsPath, projectLines);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to save recent projects");
        }
    }

    private void openProject(ProjectInfo proj) {
        moveToTop(proj);
        new ProjectFrame(proj.path);
        if (frame != null)
            frame.dispose();
    }
}
