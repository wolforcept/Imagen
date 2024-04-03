package wolforce.imagen4;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class Watcher {

    private final FileAlterationObserver observer;
    private final FileAlterationMonitor monitor;

    public Watcher(String scriptPath, Main main) {

        File directory = new File(scriptPath).getParentFile();
        observer = new FileAlterationObserver(directory);
        monitor = new FileAlterationMonitor(100);
        FileAlterationListener listener = new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                if (file.getAbsolutePath().equals(scriptPath))
                    main.scriptChanged();
            }
        };
        observer.addListener(listener);
        monitor.addObserver(observer);
        try {
            monitor.start();
            System.out.println("[Watcher] started watching " + scriptPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            monitor.stop();
            observer.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
