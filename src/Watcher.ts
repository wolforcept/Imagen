// import chokidar from 'chokidar';

import fs, { FSWatcher } from 'fs'

interface WatcherCallbacks {
    onScriptChanged: () => void,
    onParamlistsChanged: () => void,
    onInputsChanged: () => void
    onOutputsChanged: () => void
}

const verbose = false
export class Watcher {

    private watcher: FSWatcher

    constructor(
        private baseDir: string,
        private callbacks: WatcherCallbacks
    ) {

        this.watcher = fs.watch(baseDir, (eventType, filename) => {
            if (!filename) return;
            if (filename === 'script.js')
                callbacks.onScriptChanged()
            if (filename.endsWith('.paramlist'))
                callbacks.onParamlistsChanged()
            if (filename.startsWith('inputs'))
                callbacks.onInputsChanged()
            if (filename.startsWith('outputs'))
                callbacks.onOutputsChanged()
            if (verbose) {
                console.log("The file ", filename, " was modified!");
                console.log("It was a ", eventType, " event type.");
            }
        });

    }

    stop() {
        this.watcher.close()
    }

}