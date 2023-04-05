import { QKeySequence, QLabel, QPushButton } from "@nodegui/nodegui";
import { Box } from "./Box";

export class Button extends QPushButton {

    constructor(text: string, private _onClick: () => void, keyCombination?: string) {
        super()
        this.setObjectName('button')
        this.setText(text)
        this.addEventListener('clicked', _onClick)
        if (keyCombination)
            this.setShortcut(new QKeySequence(keyCombination))
    }

    w(w: number): Button {
        this.setFixedWidth(w)
        return this
    }

    h(h: number): Button {
        this.setFixedHeight(h)
        return this
    }

    delete() {
        this.removeEventListener('clicked', this._onClick)
        super.delete()
    }

}

var curr: ButtonH

export class ButtonH extends Button {

    constructor(text: string, onClick: () => void, keyCombination?: string) {
        super(text, () => {
            onClick()
            this.highlight()
        }, keyCombination)
        this.highlight()
    }

    highlight() {
        try {
            curr.setObjectName('button')
        } catch (e) { }
        if (this.native) {
            this.setObjectName('button-active')
            curr = this
        }
    }

}