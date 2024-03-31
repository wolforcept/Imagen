import { QFont, QLabel } from "@nodegui/nodegui";

export class Title extends QLabel {

    constructor(text: string) {
        super()
        this.setText(text)
        this.setObjectName('title')
        // const font = new QFont('Arial', 36, 200);
        // this.setFont(font)
    }
}