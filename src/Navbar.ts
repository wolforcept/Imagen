import { BoxV } from "./components/Box";
import { Button, ButtonH } from "./components/Button";
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

        const scriptButton = new ButtonH("Script", () => setPage(main.scriptPage))
        this.add(scriptButton)
        this.add(new ButtonH("Inputs", () => setPage(main.inputsPage)))
        this.add(new ButtonH("Outputs", () => setPage(main.outputsPage)))
        this.addSpacing(20)
        this.add(this.paramlistsWrapperWidget)
        this.add(new Button("+", () => main.data.createParamlist()))
        this.addSpacing();
        this.add(new Button("explore", () => main.data.openFolder()))
        this.add(new Button("close", () => main.closeProject()))

        scriptButton.highlight()
        this.refreshParamlistsWidgets()
    }

    refreshParamlistsWidgets() {

        this.paramlistsButtons.forEach(b => b.delete());
        this.paramlistsButtons = []

        const paramlistsNames = this.main.data.getParamlists()
        for (let i = 0; i < paramlistsNames.length; i++) {
            const name = paramlistsNames[i].substring(0, paramlistsNames[i].length - 10)
            const b = new ButtonH(name, () => this.openParamlist(name));
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