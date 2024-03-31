import { QComboBox } from "@nodegui/nodegui";

export class Dropdown extends QComboBox {

    constructor(private data: string[], onChange?: (data: string, index: number) => void) {
        super()
        this.setObjectName('dropdown')
        this.addItems(data)
        if (onChange) {
            this.addEventListener('currentIndexChanged', () => {
                onChange(this.get(), this.currentIndex())
            })
        }
    }

    get() {
        return this.data[this.currentIndex()]
    }

}
