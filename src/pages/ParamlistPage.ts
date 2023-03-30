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
        ]).h(40))

        // this.add(1)

        this.varsList = new BoxH().h(40);
        this.add(this.varsList)

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
        this.refreshVarsList();
    }

    addLine() {
        this.saveObj.lines.push({
            values: this.saveObj.vars.map(x => x.name)
        })
    }

    addVar() {

        const names = this.saveObj.vars.map(x => x.name);
        let newName = 'var1'
        let i = 2
        while (names.find(x => x === newName))
            newName = 'var' + i++

        this.saveObj.vars.push({ name: newName, x: 0, y: 0 })
        this.refreshVarsList();
        this.save()
    }

    save() {
        this.main.data.saveParamlist(this.paramlistName, this.saveObj)
    }

    refreshVarsList() {

        this.varsList.resetLayout()

        this.varsList.add(new Button('+', () => {
            this.addVar()
            this.refreshParamsList()
            this.save()
        }).w(28).h(28))

        for (let i = 0; i < this.saveObj.vars.length; i++) {
            const varIndex = i;
            const varName = this.saveObj.vars[varIndex].name;
            this.varsList.add(new TextField(newVarName => {
                this.saveObj.vars[varIndex].name = newVarName;
                this.refreshParamsList()
                this.save()
            }, varName))
        };

        this.varsList.add(new Button('+', () => {
            this.addVar()
            this.refreshParamsList()
            this.save()
        }).w(28).h(28))

        // this.varsList.addSpacing()
    }

    refreshParamsList() {

        this.listOfLines.resetLayout()

        for (let lineIndex = 0; lineIndex < this.saveObj.lines.length; lineIndex++) {

            // const line = this.saveObj.lines[lineIndex];
            // const lineBox = new BoxH()

            // lineBox.add(new Button('❌', () => {
            //     this.saveObj.lines.splice(lineIndex, 1)
            //     this.save()
            //     setTimeout(() => {
            //         this.refreshParamsList()
            //     }, 1);
            // }).w(28).h(28))

            // let i = 0
            // this.saveObj.vars.forEach(() => {
            //     const index = i++;
            //     // lineBox.add(new Label(v))
            //     lineBox.add(new TextField((t) => {
            //         this.saveObj.lines[lineIndex].values[index] = t
            //         this.save()
            //     }, line.values[index] ?? ''))
            // })

            // // lineBox.addSpacing()

            // lineBox.add(new Button('🔍', () => {
            //     this.main.renderer.render(this.saveObj, lineIndex)
            // }).w(28).h(28))

            // this.listOfLines.add(lineBox)
            const paramBox = new ParamBox(this.saveObj, lineIndex);
            this.listOfLines.add(paramBox)
        }
        this.listOfLines.addSpacing()
    }

}