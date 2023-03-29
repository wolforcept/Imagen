import { QKeySequence, QLabel, QPushButton, QScrollArea, QWidget } from "@nodegui/nodegui";

export class ScrollArea extends QScrollArea {

    constructor(widget: QWidget) {
        super()
        this.setObjectName('scrollarea')
        this.setWidget(widget)
        this.setWidgetResizable(true)
    }

    w(w: number): ScrollArea {
        this.setFixedWidth(w)
        return this
    }

    h(h: number): ScrollArea {
        this.setFixedHeight(h)
        return this
    }

}