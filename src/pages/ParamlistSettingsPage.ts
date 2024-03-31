import { AcceptMode, FileMode, QFileDialog } from "@nodegui/nodegui";
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
import { VarBox } from "./VarBox";

export class ParamlistSettingsPage extends Page {

    isSettings = true
    listOfLines: BoxV
    // varsList: BoxH
    saveObj: ParamList

    constructor(private main: Main, public paramlistName: string) {
        super()

        this.saveObj = this.main.data.openParamlist(this.paramlistName)

        this.add(new Title("Parameter Settings: " + paramlistName))

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

        this.listOfLines = new BoxV();
        this.add(new ScrollArea(this.listOfLines))

        const buttonsBox = new BoxH([
            new Button('+', () => {
                this.addVar()
                this.refreshVarsList()
                this.save()
            }),
            new Button('Import', () => {
                this.tryImportCsv()
                this.refreshVarsList()
                this.save()
            }),
        ])

        this.add(buttonsBox)
    }

    onOpen(): void {
        this.saveObj = this.main.data.openParamlist(this.paramlistName)
        this.refreshVarsList();
        // this.refreshVarsList();
    }

    tryImportCsv() {
        const fileDialog = new QFileDialog();
        fileDialog.setFileMode(FileMode.ExistingFile);
        fileDialog.setAcceptMode(AcceptMode.AcceptOpen);
        fileDialog.setNameFilter('*.csv')
        fileDialog.exec();
        const selectedFiles = fileDialog.selectedFiles();
        if (selectedFiles?.length > 0) {
            const filePath = selectedFiles[0];
            this.main.data.importCsvIntoParamlist(filePath, this.saveObj)
        }
    }

    addVar() {

        const names = this.saveObj.vars.map(x => x.name);
        let newName = 'var1'
        let i = 2
        while (names.find(x => x === newName))
            newName = 'var' + i++

        this.saveObj.vars.push({ name: newName, x: 0, y: 0, w: 1, h: 1 })
        this.refreshVarsList();
        this.save()
    }

    save() {
        this.main.data.saveParamlist(this.saveObj)
    }

    // refreshVarsList() {

    //     this.varsList.resetLayout()

    //     this.varsList.add(new Button('+', () => {
    //         this.addVar()
    //         this.refreshParamsList()
    //         this.save()
    //     }).w(28).h(28))

    //     for (let i = 0; i < this.saveObj.vars.length; i++) {
    //         const varIndex = i;
    //         const varName = this.saveObj.vars[varIndex].name;
    //         this.varsList.add(new TextField(newVarName => {
    //             this.saveObj.vars[varIndex].name = newVarName;
    //             this.refreshParamsList()
    //             this.save()
    //         }, varName))
    //     };

    //     this.varsList.add(new Button('+', () => {
    //         this.addVar()
    //         this.refreshParamsList()
    //         this.save()
    //     }).w(28).h(28))

    //     // this.varsList.addSpacing()
    // }

    refreshVarsList() {

        this.listOfLines.resetLayout()

        for (let varIndex = 0; varIndex < this.saveObj.vars.length; varIndex++) {
            const varBox = new VarBox(this.main, this.saveObj, varIndex);
            this.listOfLines.add(varBox)
        }
        this.listOfLines.addSpacing()
    }

}