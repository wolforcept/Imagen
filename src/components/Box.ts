import { AlignmentFlag, Direction, QBoxLayout, QGroupBox, QWidget } from "@nodegui/nodegui";

export class Box extends QGroupBox {

    private _layout: QBoxLayout
    private _children: QWidget[]

    constructor(direction: Direction, children?: (QWidget | null)[]) {
        super()
        this._children = []
        this._layout = new QBoxLayout(direction)
        this.setLayout(this._layout)
        this.setObjectName('box')

        children?.forEach(child => {
            if (child === null) {
                this._layout.addStretch()
            } else {
                this._layout.addWidget(child)
                this._children.push(child)
            }
        })
    }

    add(child: QWidget) {

        // if (typeof child === 'number') {
        //     this._layout.addSpacing(child)
        // } else if (child === null) {
        //     this._layout.addStretch()
        // } else {
        this._layout.addWidget(child)
        this._children.push(child)
        // }
    }

    deleteAllChildren() {
        this._children.forEach(child => {
            this._layout.removeWidget(child)
            child.delete()
        });
        this._children = []
    }

    noMargins(): Box {
        this._layout.setContentsMargins(0, 0, 0, 0)
        return this
    }

    margins(t: number, r: number, b: number, l: number) {
        this._layout.setContentsMargins(t, r, b, l)
    }

    addSpacing(value?: number) {
        if (value)
            this._layout.addSpacing(value);
        else
            this._layout.addStretch();
    }

    w(w: number): Box {
        this.setFixedWidth(w)
        return this
    }

    h(h: number): Box {
        this.setFixedHeight(h)
        return this
    }

}

export class BoxV extends Box {

    constructor(children?: (QWidget | null)[]) {
        super(Direction.TopToBottom, children)
        this.setAlignment(AlignmentFlag.AlignTop)
    }
}

export class BoxH extends Box {
    constructor(children?: (QWidget | null)[]) {
        super(Direction.LeftToRight, children)
        this.setAlignment(AlignmentFlag.AlignLeft)
    }
}