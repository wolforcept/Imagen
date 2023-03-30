import { AlignmentFlag, Direction, QBoxLayout, QGridLayout, QLayout, QObject, QWidget } from "@nodegui/nodegui";

export abstract class Grid extends QWidget {

    private thisLayout: QBoxLayout
    private inner: QWidget
    private innerLayout: QGridLayout

    constructor() {
        super()
        this.thisLayout = new QBoxLayout(Direction.TopToBottom)
        this.setLayout(this.thisLayout)
        super.setObjectName('wrapper')
        this.thisLayout.setContentsMargins(0, 0, 0, 0)

        const { box, layout } = this.resetLayout()
        this.inner = box
        this.innerLayout = layout
    }

    add(child: QWidget, row: number, col: number, rowspan?: number, colspan?: number) {
        this.innerLayout.addWidget(child, row, col, rowspan, colspan)
    }

    class(objectName: string) {
        this.inner.setObjectName(objectName)
    }

    resetLayout(): { box: QWidget, layout: QGridLayout } {
        if (this.inner) {
            this.thisLayout.removeWidget(this.inner)
            this.inner.delete()
        }

        this.inner = new QWidget()
        this.innerLayout = new QGridLayout()
        this.inner.setLayout(this.innerLayout);
        this.inner.setObjectName('box')
        this.innerLayout.setContentsMargins(0, 0, 0, 0)

        this.thisLayout.addWidget(this.inner)
        return { box: this.inner, layout: this.innerLayout }
    }

}
