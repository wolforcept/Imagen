import { BoxV } from "./Box";

export abstract class Page extends BoxV {

    constructor() {
        super()
        this.setObjectName('page')
    }

    abstract onOpen(): void
}