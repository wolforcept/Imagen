import { BoxV } from "./Box";

export abstract class Page extends BoxV {

    constructor() {
        super()
        this.setObjectName('page')
        this.margin(10)
    }

    abstract onOpen(): void
}