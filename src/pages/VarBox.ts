import { BoxH } from "../components/Box";
import { Button } from "../components/Button";
import { Grid } from "../components/Grid";
import { Label } from "../components/Label";
import { TextField } from "../components/Textbox";
import { ParamList, ParamVar } from "../data/ParamList";
import { Main } from "../Main";

export class VarBox extends BoxH {

    constructor(private main: Main, private saveObj: ParamList, varIndex: number) {
        super()

        this.margins(10)

        const v = saveObj.vars[varIndex];

        this.add(new BoxH([
            new Label('Var Name:'),
            new TextField(t => {
                saveObj.vars[varIndex].name = t
                this.save()
            }, saveObj.vars[varIndex].name)
        ]))

        this.add(new BoxH([
            new Label('X:'),
            new TextField(t => {
                saveObj.vars[varIndex].x = parseInt(t)
                this.save()
            }, '' + saveObj.vars[varIndex].x)
        ]))
        this.add(new BoxH([
            new Label('Y:'),
            new TextField(t => {
                saveObj.vars[varIndex].y = parseInt(t)
                this.save()
            }, '' + saveObj.vars[varIndex].y)
        ]))

        this.add(new BoxH([
            new Label('W:'),
            new TextField(t => {
                saveObj.vars[varIndex].w = parseInt(t)
                this.save()
            }, '' + saveObj.vars[varIndex].w)
        ]))
        this.add(new BoxH([
            new Label('H:'),
            new TextField(t => {
                saveObj.vars[varIndex].h = parseInt(t)
                this.save()
            }, '' + saveObj.vars[varIndex].h)
        ]))

        this.add(new Button('❌', () => {
            this.saveObj.vars.splice(varIndex, 1)
            this.save()
            setTimeout(() => {
                this.delete()
            }, 1);
        }).w(28).h(28))
    }

    save() {
        this.main.data.saveParamlist(this.saveObj)
    }

}