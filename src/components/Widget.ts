import { Direction, QBoxLayout, QLayout, QWidget } from "@nodegui/nodegui";

export class Widget extends QWidget {

    private _children: QWidget[]

    constructor(public _layout: QLayout) {
        super()
        this._children = []
        this.setLayout(this._layout)
    }

    add(child: QWidget) {
        this._layout.addWidget(child)
        this._children.push(child)
    }

    deleteAllChildren() {
        this._children.forEach(child => {
            this._layout.removeWidget(child)
            child.delete()
        });
        this._children = []
    }

    noMargins(): Widget {
        this._layout.setContentsMargins(0, 0, 0, 0)
        return this
    }

    margins(t: number, r: number, b: number, l: number) {
        this._layout.setContentsMargins(t, r, b, l)
    }

}
