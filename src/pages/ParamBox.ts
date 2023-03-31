import { BoxH } from "../components/Box";
import { Button } from "../components/Button";
import { Grid } from "../components/Grid";
import { Label } from "../components/Label";
import { TextField } from "../components/Textbox";
import { ParamList, ParamVar } from "../data/ParamList";
import { Main } from "../Main";

export class ParamBox extends Grid {

    constructor(private main: Main, private saveObj: ParamList, lineIndex: number) {
        super()

        this.margins(10)

        let i = 0;
        let maxX = 0;
        let maxY = 0;
        saveObj.vars.forEach(v => {
            const valueIndex = i;
            const box = new BoxH([
                new Label(v.name),
                new TextField(t => {
                    saveObj.lines[lineIndex].values[valueIndex] = t
                    this.save()
                }, saveObj.lines[lineIndex].values[valueIndex]),
            ])
            this.add(box, v.x, v.y, v.w, v.h)
            i++;
            if (v.x > maxX)
                maxX = v.x
            if (v.y > maxY)
                maxY = v.y
        })

        const buttonsLine = new BoxH([
            new Button('❌', () => {
                this.saveObj.lines.splice(lineIndex, 1)
                this.save()
                setTimeout(() => {
                    this.delete()
                }, 1);
            }).w(28).h(28),

            new Button('🔍', () => {
                this.main.renderer.render(this.saveObj, lineIndex)
            }).w(28).h(28),
        ])
        buttonsLine.addSpacing()
        this.add(buttonsLine, 0, maxY + 1, maxX, 1)


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

    save() {
        this.main.data.saveParamlist(this.saveObj)
    }

}