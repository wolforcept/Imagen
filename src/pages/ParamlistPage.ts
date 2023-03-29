import { BoxH, BoxV } from "../components/Box";
import { Button } from "../components/Button";
import { Label } from "../components/Label";
import { Page } from "../components/Page";
import { ScrollArea } from "../components/ScrollArea";
import { TextField } from "../components/Textbox";
import { Title } from "../components/Title";
import { ParamList } from "../data/ParamList";
import { Main } from "../Main";

export class ParamlistPage extends Page {

    listOfLines: BoxV
    varsList: BoxH
    saveObj: ParamList

    constructor(private main: Main, private paramlistName: string) {
        super()

        this.saveObj = this.main.data.openParamlist(this.paramlistName)

        this.add(new Title("Parameter List: " + paramlistName))

        this.add(new BoxH([
            new Label('Width'),
            new TextField((t) => {
                this.saveObj.width = Number.parseInt(t)
                this.save()
            }, '' + this.saveObj.width),
            new Label('Height'),
            new TextField((t) => {
                this.saveObj.height = Number.parseInt(t)
                this.save()
            }, '' + this.saveObj.height),
        ]).noMargins().h(40))

        // this.add(1)

        this.varsList = new BoxH().noMargins().h(40);
        this.add(this.varsList)

        this.listOfLines = new BoxV().noMargins();
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
        this.refreshVarsList();
    }

    addLine() {
        this.saveObj.lines.push({
            values: [...this.saveObj.varNames]
        })
    }

    addVar() {
        this.saveObj.varNames.push('')
        this.refreshVarsList();
        this.save()
    }

    save() {
        this.main.data.saveParamlist(this.paramlistName, this.saveObj)
    }

    used: boolean = false
    refreshVarsList() {
        if (this.used) return
        this.used = true

        this.varsList.deleteAllChildren()

        this.varsList.add(new Button('+', () => {
            this.addVar()
            this.refreshParamsList()
            this.save()
        }).w(28).h(28))

        for (let i = 0; i < this.saveObj.varNames.length; i++) {
            const varIndex = i;
            const varName = this.saveObj.varNames[varIndex];
            this.varsList.add(new TextField(newVarName => {
                this.saveObj.varNames[varIndex] = newVarName;
                this.refreshParamsList()
                this.save()
            }, varName))
        };

        this.varsList.add(new Button('+', () => {
            this.addVar()
            this.refreshParamsList()
            this.save()
        }).w(28).h(28))

        this.varsList.addSpacing()
        this.used = false
    }

    refreshParamsList() {

        this.listOfLines.deleteAllChildren()

        for (let lineIndex = 0; lineIndex < this.saveObj.lines.length; lineIndex++) {

            const line = this.saveObj.lines[lineIndex];
            const lineBox = new BoxH().noMargins()

            lineBox.add(new Button('❌', () => {
                this.saveObj.lines.splice(lineIndex, 1)
                this.save()
                setTimeout(() => {
                    this.refreshParamsList()
                }, 1);
            }).w(28).h(28))

            let i = 0
            this.saveObj.varNames.forEach(v => {
                const index = i++;
                // lineBox.add(new Label(v))
                lineBox.add(new TextField((t) => {
                    this.saveObj.lines[lineIndex].values[index] = t
                    this.save()
                }, line.values[index] ?? ''))
            })

            lineBox.addSpacing()

            lineBox.add(new Button('🔍', () => {
                this.main.renderer.render(this.saveObj, lineIndex)
            }).w(28).h(28))

            this.listOfLines.add(lineBox)
        }
        // this.listOfLines.addSpacing()
    }

}