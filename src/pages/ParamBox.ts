import { BoxH } from "../components/Box";
import { Grid } from "../components/Grid";
import { Label } from "../components/Label";
import { TextField } from "../components/Textbox";
import { ParamList, ParamVar } from "../data/ParamList";
import { Main } from "../Main";

export class ParamBox extends Grid {

    constructor(saveObj: ParamList, lineIndex: number) {
        super()

        let i = 0;
        saveObj.vars.forEach(v => {
            const valueIndex = i;
            const box = new BoxH([
                new Label(v.name),
                new TextField(t => {
                    saveObj.lines[lineIndex].values[valueIndex] = t
                }),
            ])
            this.add(box, v.x, v.y,v.x,v.y)
            i++;
        })


        // const line = this.saveObj.lines[lineIndex];
        //     const lineBox = new BoxH()

        //     lineBox.add(new Button('❌', () => {
        //         this.saveObj.lines.splice(lineIndex, 1)
        //         this.save()
        //         setTimeout(() => {
        //             this.refreshParamsList()
        //         }, 1);
        //     }).w(28).h(28))

        //     let i = 0
        //     this.saveObj.vars.forEach(() => {
        //         const index = i++;
        //         // lineBox.add(new Label(v))
        //         lineBox.add(new TextField((t) => {
        //             this.saveObj.lines[lineIndex].values[index] = t
        //             this.save()
        //         }, line.values[index] ?? ''))
        //     })

        //     // lineBox.addSpacing()

        //     lineBox.add(new Button('🔍', () => {
        //         this.main.renderer.render(this.saveObj, lineIndex)
        //     }).w(28).h(28))

        //     this.listOfLines.add(lineBox)
    }

}