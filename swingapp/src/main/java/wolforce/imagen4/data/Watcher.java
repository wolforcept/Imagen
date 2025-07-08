package wolforce.imagen4.data;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import wolforce.imagen4.ui.ProjectFrame;

import java.io.File;
import java.io.IOException;

public class Watcher {

    private final FileAlterationMonitor monitor;
    private final FileAlterationObserver observer;

    public Watcher(String projectPath, String scriptPath, String gridsPath, ProjectFrame projectFrame) {

        try {

            monitor = new FileAlterationMonitor(100);

            observer = FileAlterationObserver.builder().setFile(projectPath).get();

            FileAlterationListener listenerList = new FileAlterationListenerAdaptor() {
                @Override
                public void onFileChange(File file) {
                    String path = file.getParent();
                    if (file.getAbsolutePath().equals(scriptPath))
                        projectFrame.scriptChanged();
                    if (path.equals(gridsPath)) {
                        projectFrame.updateGridsMenu();
                        projectFrame.watchedGridChanged(file);
                    }
                }

                @Override
                public void onFileDelete(File file) {
                    String path = file.getParent();
                    if (file.getAbsolutePath().equals(scriptPath))
                        projectFrame.scriptChanged();
                    if (path.equals(gridsPath))
                        projectFrame.updateGridsMenu();
                }
            };
            observer.addListener(listenerList);

            monitor.addObserver(observer);

            monitor.start();
            System.out.println("[Watcher] started watching " + projectPath);

        } catch (IOException e) {
            throw new RuntimeException("Could not create file Watcher.\n" + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Could not start file Watcher.\n" + e.getMessage(), e);
        }
    }

    public void stop() {
        try {
            monitor.stop();
            observer.destroy();
            observer.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
