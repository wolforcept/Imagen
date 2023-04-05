import { QLabel } from "@nodegui/nodegui";

export class Label extends QLabel {

    constructor(text: string) {
        super()
        this.setText(text)
        this.setObjectName('label')
    }

    wMin(v: number): Label {
        this.setMinimumWidth(v)
        return this
    }
}