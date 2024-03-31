import { QComboBox, QLabel, QPixmap, QSizePolicyPolicy, QWidget } from "@nodegui/nodegui";
import { BoxH, BoxV } from "../components/Box";
import { Dropdown } from "../components/Dropdown";
import { Grid } from "../components/Grid";
import { Label } from "../components/Label";
import { Page } from "../components/Page";
import { ScrollArea } from "../components/ScrollArea";
import { Title } from "../components/Title";
import { Local } from "../Local";
import { Main } from "../Main";

export type ListType = 'Grid' | 'List'

interface Obj {
    image?: QLabel
    path: string
    name: string
    group: string | null
}

export class FilesListPage extends Page {

    scrollArea?: ScrollArea;

    constructor(private main: Main, private basePath: string, private getFilePaths: () => string[]) {
        super()
        this.add(new BoxH([
            new Title("Inputs"),
            new Dropdown(['Grid', 'List'], (_type) => {
                Local.setListType(_type as ListType)
                this.refreshGrid()
            }),
        ]))
    }

    refreshGrid() {
        this.scrollArea?.delete()

        const objs: Obj[] = []
        this.getFilePaths().forEach(name => {
            const path = this.basePath + '/' + name;
            const group = name.includes('/')
                ? name.substring(name.lastIndexOf('/'))
                : null

            if (!name.endsWith('.png')
                && !name.endsWith('jpg')
                && !name.endsWith('jpeg')) {
                objs.push({ path, name, group })
            }

            const label = new QLabel();
            label.setFixedWidth(128);
            label.setFixedHeight(128);
            label.setScaledContents(true)
            const pixmap = new QPixmap();
            pixmap.load(path);
            label.setPixmap(pixmap);
            objs.push({ image: label, path, name, group })
        });

        const listType = Local.getListType();

        if (listType === 'Grid') {
            this.scrollArea = this.makeGrid(objs)
            this.add(this.scrollArea)
        }
        if (listType === 'List') {
            this.scrollArea = this.makeList(objs)
            this.add(this.scrollArea)
        }

    }

    onOpen(): void {
        this.refreshGrid()
    }

    private makeList(objs: Obj[]): ScrollArea {
        const list = new BoxV(objs.map(o => {

            return new BoxH(
                o.image
                    ? [o.image, new Label(o.name)]
                    : [new Label(o.name)]
            )
        }));

        return new ScrollArea(list)
    }

    private makeGrid(objs: Obj[]): ScrollArea {
        const grid = new Grid();

        const w = Math.min(10, Math.ceil(Math.sqrt(objs.length)))
        let x = 0, y = 0;
        objs.forEach(o => {
            grid.add(new BoxV(
                o.image
                    ? [o.image, new Label(o.name)]
                    : [new Label(o.name)]
            ).addSpacing(), x, y)
            x++;
            if (x >= w) {
                y++;
                x = 0;
            }
        });
        return new ScrollArea(grid);
    }

}