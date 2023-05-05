import { FocusReason, NativeElement, QApplication, QLabel, QMainWindow, QMouseEvent, QPixmap, QScrollArea, QSize, QWindow, WidgetEventTypes } from "@nodegui/nodegui";
import * as fs from 'fs';
import * as PImage from "pureimage";
import { Bitmap } from "pureimage/types/bitmap";
import { Context, TextAlign, TextBaseline } from "pureimage/types/context";
import { FontRecord } from "pureimage/types/text";
import { ScrollArea } from "./components/ScrollArea";
import { ParamList } from './data/ParamList';
import { Main } from "./Main";

class ImageWindow extends QMainWindow {

    label: QLabel
    prevPos: { x: number, y: number } | undefined

    constructor(public paramlist: ParamList, public lineIndex: number) {
        super()

        this.label = new QLabel();
        this.label.setInlineStyle(`background-color: #223344;`);

        const scrollarea = new ScrollArea(this.label);
        scrollarea.addEventListener(WidgetEventTypes.MouseMove, (nativeEvent: any) => {
            const e = new QMouseEvent(nativeEvent);
            if (this.prevPos) {
                const dx = e.x() - this.prevPos.x
                const dy = e.y() - this.prevPos.y
                const h = scrollarea.horizontalScrollBar();
                const v = scrollarea.verticalScrollBar();
                h.setValue(h.value() - dx)
                v.setValue(v.value() - dy)
            }
            this.prevPos = { x: e.x(), y: e.y() }
        });
        scrollarea.addEventListener(WidgetEventTypes.MouseButtonRelease, () => {
            this.prevPos = undefined
        });

        this.setCentralWidget(scrollarea);
        this.setStyleSheet(`QScrollBar { width: 0px; height: 0px; }`)
    }

    showImage(path: string) {
        this.hide()
        const image = new QPixmap();
        image.load(path);
        this.label.setPixmap(image);
        this.resize(
            Math.min(1000, image.width()),
            Math.min(1000, image.height())
        )
        this.setWindowTitle(this.paramlist.lines[this.lineIndex].values.join(' '));
        this.show()
        // QApplication.instance().
        // this.focusWidget()
        // this.setFocus(FocusReason.PopupFocusReason)
        // this.setFocus()
    }

}

export class Renderer {

    imageWindows: ImageWindow[] = []
    // lastRendered?: { lineIndex: number, paramlist: ParamList }

    constructor(private main: Main, private baseDir: string) { }

    async renderLast() {
        this.imageWindows.forEach(win => {
            if (win && win.isVisible())
                this.render(win.paramlist, win.lineIndex)
        });
        // if (!this.lastRendered) return
    }

    async render(paramlist: ParamList, lineIndex: number) {
        try {
            // this.lastRendered = { paramlist, lineIndex }
            await this.inRender(this.main.data.getScript(), paramlist, lineIndex)
        } catch (e: unknown) {
            console.log(e)
        }
    }

    private async inRender(script: string, paramlist: ParamList, lineIndex: number) {

        const { width, height, varNames, name } = paramlist
        const values: string[] = paramlist.lines[lineIndex].values
        const baseDir = this.baseDir

        const images = new Map<string, Bitmap>()
        const canvas = PImage.make(width, height, undefined)
        const ctx = canvas.getContext('2d');

        // const images_293865 = new Map<string, Bitmap>()
        // const canvas = createCanvas(width, height)
        // const canvas = PImage.make(width, height, undefined)
        // const ctx = canvas.getContext('2d', { alpha: true });

        //
        // SHAPES

        var currentShapesColor = 'red'
        function drawRect(x: number, y: number, w: number, h: number) {
            ctx.fillStyle = currentShapesColor;
            ctx.strokeRect(x, y, w, h);
        }

        function fillRect(x: number, y: number, w: number, h: number) {
            ctx.fillStyle = currentShapesColor;
            ctx.fillRect(x, y, w, h);
        }

        //
        // IMAGE

        async function loadImage(path: string): Promise<string> {
            const stream = fs.createReadStream(`${baseDir}/inputs/${path}`)
            const img = path.endsWith('.png')
                ? await PImage.decodePNGFromStream(stream)
                : await PImage.decodeJPEGFromStream(stream)
            images.set(path, img)
            return path
        }

        function drawImage(path: string, x: number, y: number, w?: number, h?: number) {
            const img = images.get(path)
            if (img)
                ctx.drawImage(img,
                    0, 0, img.width, img.height,
                    x, y, w ?? img.width, h ?? img.height
                );
        }

        //
        // TEXT

        function loadFont(path: string): string {
            const fontName = path.substring(0, path.lastIndexOf('.'));
            PImage.registerFont(`${baseDir}/inputs/${path}`, fontName, 5, '', '').loadSync();
            return fontName
        }

        var currentFontColor = 'red'
        var currentFont = ''
        var currentFontSize = 18

        function setFontColor(color: string) {
            currentFontColor = color;
        }

        function setFont(font: string, options: { size?: number }) {
            currentFont = font;
            if (options?.size)
                currentFontSize = options.size;
        }

        function drawText(text: string, x: number, y: number,
            options?: {
                baseline?: TextBaseline,
                align?: TextAlign,
                font?: string,
                size?: number,
                color?: string,
                outline?: boolean,
                width?: number,
                height?: number,
                images?: { [key: string]: { image?: string, x?: number, y?: number, w: number, h: number } }
            }
        ) {

            ctx.textBaseline = options?.baseline ?? "top";
            ctx.textAlign = options?.align ?? "start";
            const maybeFont = options?.font ?? currentFont;
            const maybeSize = options?.size ?? currentFontSize;
            ctx.font = `${maybeSize}pt ${maybeFont}`;
            ctx.fillStyle = options?.color ?? currentFontColor;

            function _drawText(_x: number, _y: number, _text: string) {
                if (options?.outline)
                    ctx.strokeText(_text, _x, _y)
                else
                    ctx.fillText(_text, _x, _y);
            }

            if (!options?.width) {
                _drawText(x, y, text)
                return;
            }

            const sentenceWidth = options.width
            const sentenceHeight = options.height ?? maybeSize * 1.2
            const words = text.split(/(\s+)/);
            let dy = 0;
            let sentence = ''

            words.forEach(word => {
                if (word.match(/(\s+)/)) return;

                if (word.startsWith('/') && options?.images) {
                    const image = options.images[word.substring(1)]
                    if (image && image.w && image.w > 0) {

                        let spaces = ''
                        let spacesWidth = 0
                        while (spacesWidth < image.w) {
                            spaces = spaces + ' '
                            spacesWidth = ctx.measureText(spaces).width
                        }
                        const dx = ctx.measureText(sentence + ' ').width
                        sentence = sentence + ' ' + spaces
                        drawImage(image.image ?? word.substring(1), x + dx + (image.x ?? 0), y + dy + (image.y ?? 0), image.w, image.h)
                        return
                    }
                }

                const nextSentence = sentence ? sentence + ' ' + word : word
                const nextWidth = ctx.measureText(sentence).width
                if (nextWidth < sentenceWidth) {
                    sentence = nextSentence
                } else {
                    _drawText(x, y + dy, sentence)
                    dy += sentenceHeight
                    sentence = word
                }
            })
            _drawText(x, y + dy, sentence)
        }

        function textWidth(text: string): number {
            ctx.font = `${currentFontSize}pt ${currentFont}`;
            ctx.fillStyle = currentFontColor;
            return ctx.measureText(text).width
        }

        //
        // RENDER

        const varsObj: any = {}
        for (let i = 0; i < varNames.length; i++) {
            const varr = varNames[i];
            varsObj[varr] = values[i] ?? ''
        }

        const render = eval(`(async () => {
            ${script}
        })`)
        await render()

        if (!fs.existsSync(`${baseDir}/outputs/`))
            fs.mkdirSync(`${baseDir}/outputs/`)

        if (!fs.existsSync(`${baseDir}/outputs/${name}/`))
            fs.mkdirSync(`${baseDir}/outputs/${name}/`)

        const imgDir = `${baseDir}/outputs/${name}/image_${lineIndex}.png`;
        PImage.encodePNGToStream(canvas, fs.createWriteStream(imgDir)).then(() => {
            if (!this.imageWindows[lineIndex])
                this.imageWindows[lineIndex] = new ImageWindow(paramlist, lineIndex)
            this.imageWindows[lineIndex].showImage(imgDir)
        }).catch((e) => {
            console.log(e)
            console.log("there was an error writing");
        });

    }

}