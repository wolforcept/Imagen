package wolforce.imagen4;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.IOException;

public class Watcher {

    private final FileAlterationMonitor monitor;
    private final FileAlterationObserver observer;

    public Watcher(String scriptPath, String gridsPath, Main main) {

        try {

            monitor = new FileAlterationMonitor(100);

            observer = FileAlterationObserver.builder().setFile(System.getProperty("user.dir")).get();
            FileAlterationListener listenerList = new FileAlterationListenerAdaptor() {
                @Override
                public void onFileChange(File file) {
                    String path = file.getParent();
                    if (file.getAbsolutePath().equals(scriptPath))
                        main.scriptChanged();
                    if (path.equals(gridsPath))
                        main.gridsChanged();
                }

                @Override
                public void onFileDelete(File file) {
                    String path = file.getParent();
                    if (file.getAbsolutePath().equals(scriptPath))
                        main.scriptChanged();
                    if (path.equals(gridsPath))
                        main.gridsChanged();
                }
            };
            observer.addListener(listenerList);

            monitor.addObserver(observer);

            monitor.start();
            System.out.println("[Watcher] started watching " + scriptPath);

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
