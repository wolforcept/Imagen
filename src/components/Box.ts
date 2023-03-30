import { AlignmentFlag, Direction, QBoxLayout, QGroupBox, QLayout, QObject, QWidget } from "@nodegui/nodegui";

export class Box extends QWidget {

    private thisLayout: QBoxLayout
    private inner: QGroupBox
    private innerLayout: QBoxLayout
    // private _children: QWidget[]

    constructor(private direction: Direction, children?: QWidget[]) {
        super()
        // this._children = []
        this.thisLayout = new QBoxLayout(Direction.TopToBottom)
        this.setLayout(this.thisLayout)
        super.setObjectName('wrapper')
        this.thisLayout.setContentsMargins(0, 0, 0, 0)

        const { box, layout } = this.resetLayout()
        this.inner = box
        this.innerLayout = layout
        // this._layout = new QBoxLayout(direction)
        // this.setLayout(this._layout)

        children?.forEach(child => {
            this.add(child)
        })
    }

    margin(v: number): Box {
        this.innerLayout.setContentsMargins(v, v, v, v)
        return this;
    }

    add(child: QWidget) {

        // if (typeof child === 'number') {
        //     this._layout.addSpacing(child)
        // } else if (child === null) {
        //     this._layout.addStretch()
        // } else {
        this.innerLayout.addWidget(child)
        // this._children.push(child)
        // }
    }

    class(objectName: string) {
        this.inner.setObjectName(objectName)
    }

    resetLayout(): { box: QGroupBox, layout: QBoxLayout } {
        if (this.inner) {
            this.thisLayout.removeWidget(this.inner)
            this.inner.delete()
        }

        this.inner = new QGroupBox()
        this.innerLayout = new QBoxLayout(this.direction)
        this.inner.setLayout(this.innerLayout);
        this.inner.setObjectName('box')
        this.innerLayout.setContentsMargins(0, 0, 0, 0)

        this.thisLayout.addWidget(this.inner)
        return { box: this.inner, layout: this.innerLayout }

        // return this._layout
        // this._children.forEach(child => {
        //     this._layout.removeWidget(child)
        //     child.delete()
        // });
        // this._children = []
        // this.children().forEach(child => {
        //     console.log({ child })
        //     // if (child && child !== this._layout && child.delete)
        //     //     child.delete()
        // })
    }

    // noMargins(): Box {
    //     this.innerLayout.setContentsMargins(0, 0, 0, 0)
    //     return this
    // }

    // margins(t: number, r: number, b: number, l: number) {
    //     this.innerLayout.setContentsMargins(t, r, b, l)
    // }

    addSpacing(value?: number) {
        if (value)
            this.innerLayout.addSpacing(value);
        else
            this.innerLayout.addStretch(999999);
    }

    w(w: number): Box {
        this.setFixedWidth(w)
        return this
    }

    h(h: number): Box {
        this.setFixedHeight(h)
        return this
    }

    setAlignment(flag: AlignmentFlag) {
        this.inner.setAlignment(flag);
    }
}

export class BoxV extends Box {

    constructor(children?: QWidget[]) {
        super(Direction.TopToBottom, children)
        this.setAlignment(AlignmentFlag.AlignTop)
    }
}

export class BoxH extends Box {
    constructor(children?: QWidget[]) {
        super(Direction.LeftToRight, children)
        this.setAlignment(AlignmentFlag.AlignLeft)
    }
}