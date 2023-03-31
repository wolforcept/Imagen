import { BoxH, BoxV } from "../components/Box";
import { Button } from "../components/Button";
import { Label } from "../components/Label";
import { Page } from "../components/Page";
import { ScrollArea } from "../components/ScrollArea";
import { TextField } from "../components/Textbox";
import { Title } from "../components/Title";
import { ParamList } from "../data/ParamList";
import { Main } from "../Main";
import { ParamBox } from "./ParamBox";

export class ParamlistPage extends Page {

    isSettings = false
    listOfLines: BoxV
    // varsList: BoxH
    saveObj: ParamList

    constructor(private main: Main, public paramlistName: string) {
        super()

        this.saveObj = this.main.data.openParamlist(this.paramlistName)

        this.add(new Title("Parameter List: " + paramlistName))

        this.listOfLines = new BoxV();
        this.add(new ScrollArea(this.listOfLines))

        this.add(new Button('+', () => {
            this.addLine()
            this.refreshParamsList()
            this.save()
        }))
    }

    onOpen(): void {
        this.saveObj = this.main.data.openParamlist(this.paramlistName)
        this.refreshParamsList();
    }

    addLine() {
        this.saveObj.lines.push({
            values: this.saveObj.vars.map(x => x.name)
        })
    }

    save() {
        this.main.data.saveParamlist(this.saveObj)
    }

    refreshParamsList() {
        this.listOfLines.resetLayout()

        for (let lineIndex = 0; lineIndex < this.saveObj.lines.length; lineIndex++) {
            const paramBox = new ParamBox(this.main, this.saveObj, lineIndex);
            this.listOfLines.add(paramBox)
        }

        this.listOfLines.addSpacing()
    }

}
