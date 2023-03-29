import { QTextEdit } from "@nodegui/nodegui";

export class Textbox extends QTextEdit {

    constructor(onChange?: (text: string) => void, initialText?: string) {
        super()
        this.setObjectName('textbox')
        this.setTabChangesFocus(true)
        this.setAcceptRichText(false)
        // this.setStyleSheet(`QScrollBar {height:0px;}`)
        if (initialText)
            this.setText(initialText)
        if (onChange)
            this.addEventListener('textChanged', () => onChange(this.toPlainText()))
    }

}

export class TextField extends Textbox {
    constructor(onChange: (text: string) => void, initialText?: string) {
        super(onChange, initialText)
        this.setMaximumHeight(28)
    }
}
