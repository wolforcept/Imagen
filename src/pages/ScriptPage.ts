import { QColor, QTextEdit } from "@nodegui/nodegui";
import { BoxH } from "../components/Box";
import { Button } from "../components/Button";
import { Page } from "../components/Page";
import { Textbox } from "../components/Textbox";
import { Title } from "../components/Title";
import { Main } from "../Main";

export class ScriptPage extends Page {

    scriptTextbox: QTextEdit

    constructor(private main: Main) {
        super()

        this.add(new Title("Script"))

        const buttonsPanel = new BoxH();

        const saveButton = new Button("Save", () => this.saveScript(), 'Ctrl+S');
        buttonsPanel.add(saveButton)

        this.scriptTextbox = new Textbox();

        this.add(buttonsPanel);
        this.add(this.scriptTextbox);
        this.updateScript()
    }

    onOpen(): void {
        this.updateScript()
    }

    updateScript() {
        this.scriptTextbox.setText(this.main.data.getScript())
    }

    saveScript() {
        this.main.data.saveScript(this.scriptTextbox.toPlainText())
    }
}
