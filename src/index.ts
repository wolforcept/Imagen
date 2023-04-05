import { FileMode, QFileDialog, QMainWindow, QWidget, WidgetEventTypes } from "@nodegui/nodegui";
import { LocalStorage } from "node-localstorage";
import { Local } from "./Local";
import { Main } from "./Main";
import { style } from "./style"

export class OpenProjectWindow extends QMainWindow {

    contentWidget: Main | undefined;

    constructor() {
        super()
        this.addEventListener(WidgetEventTypes.Close, () => process.exit())
        this.init();
    }

    init() {
        const lastProjLoc: string | null = Local.getCurrentProjectLocation();
        this.contentWidget?.hide()
        this.contentWidget?.close()
        this.contentWidget = new Main(lastProjLoc ?? this.showDialog())
        this.setCentralWidget(this.contentWidget);
        this.setWindowTitle("Imagen: Programmable Image Generator");
        this.setStyleSheet(style);
        this.resize(600, 600)
        this.show();
    }

    showDialog(): string {
        const fileDialog = new QFileDialog();
        fileDialog.setFileMode(FileMode.Directory);
        fileDialog.exec();
        const selectedFiles = fileDialog.selectedFiles();
        if (selectedFiles?.length > 0) {
            Local.setCurrentProjectLocation(selectedFiles[0])
            return selectedFiles[0];
        }
        process.exit()
    }

}

export const INDEX = new OpenProjectWindow();
(global as any).win = INDEX

const { Image } = require('canvas')
const fs = require('fs')
const { registerFont, createCanvas } = require('canvas')
// registerFont('comicsans.ttf', { family: 'Comic Sans' })

const canvas = createCanvas(500, 500)
const ctx = canvas.getContext('2d')

ctx.font = '12px "Comic Sans"'
ctx.fillText('Everyone hates this font :(', 250, 10)