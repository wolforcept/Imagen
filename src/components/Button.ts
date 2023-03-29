import { QKeySequence, QLabel, QPushButton } from "@nodegui/nodegui";

export class Button extends QPushButton {

    constructor(text: string, onClick: () => void, keyCombination?: string) {
        super()
        this.setObjectName('button')
        this.setText(text)
        this.addEventListener('clicked', onClick)
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

}