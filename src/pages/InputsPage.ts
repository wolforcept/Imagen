import { BoxV } from "../components/Box";
import { Page } from "../components/Page";
import { Title } from "../components/Title";
import { Main } from "../Main";

export class InputsPage extends Page {


    constructor(private main: Main) {
        super()
        this.add(new Title("Inputs"))
        // this.inputs = new BoxV();
        // this.inputs.add(new Button("Add Input Image", () => console.log("todo: add image")));
    }

    onOpen(): void {
    }

}