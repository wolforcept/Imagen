import { BoxV } from "./components/Box";
import { Button } from "./components/Button";
import { Page } from "./components/Page";
import { Main } from "./Main";
import { ParamlistPage } from "./pages/ParamlistPage";
import { ParamlistSettingsPage } from "./pages/ParamlistSettingsPage";

export class Navbar extends BoxV {

    paramlistsWrapperWidget: BoxV
    paramlistsButtons: Button[] = []

    constructor(private main: Main, setPage: (page: Page) => void) {
        super()
        this.setFixedWidth(100)
        this.margin(10)

        this.paramlistsWrapperWidget = new BoxV();

        this.addLeftButton("Script", () => setPage(main.scriptPage))
        this.addLeftButton("Inputs", () => setPage(main.inputsPage))
        this.addSpacing(20)
        this.add(this.paramlistsWrapperWidget)
        this.addLeftButton("+", () => main.data.createParamlist())
        this.addSpacing();
        this.addLeftButton("Close", () => main.closeProject())

        this.refreshParamlistsWidgets()
    }

    addLeftButton(text: string, onClick: () => void) {
        this.add(new Button(text, onClick))
    }

    refreshParamlistsWidgets() {

        this.paramlistsButtons.forEach(b => b.delete());
        this.paramlistsButtons = []

        const paramlistsNames = this.main.data.getParamlists()
        for (let i = 0; i < paramlistsNames.length; i++) {
            const name = paramlistsNames[i].substring(0, paramlistsNames[i].length - 10)
            const b = new Button(name, () => this.openParamlist(name));
            this.paramlistsWrapperWidget.add(b)
            this.paramlistsButtons.push(b)
        }
    }

    openParamlist(paramlistName: string) {
        const currParamlistPage = (this.main.currentPage as ParamlistPage)
        if (currParamlistPage && currParamlistPage.paramlistName === paramlistName && !currParamlistPage.isSettings)
            this.main.setPage(new ParamlistSettingsPage(this.main, paramlistName))
        else
            this.main.setPage(new ParamlistPage(this.main, paramlistName))
    }

}