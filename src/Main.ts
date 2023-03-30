import { INDEX } from '.';
import { BoxH, BoxV } from './components/Box';
import { Page } from './components/Page';
import { Data } from './Data';
import { Local } from './Local';
import { Navbar } from './Navbar';
import { InputsPage } from './pages/InputsPage';
import { ParamlistPage } from './pages/ParamlistPage';
import { ScriptPage } from './pages/ScriptPage';
import { Renderer } from './Renderer';
import { Watcher } from './Watcher';

export class Main extends BoxH {

    data: Data
    renderer: Renderer
    watcher: Watcher

    navbar: Navbar
    content: BoxV
    currentPage: Page

    inputsPage: InputsPage
    scriptPage: ScriptPage
    paramlistsPages: Map<string, ParamlistPage>

    constructor(baseDir: string) {
        super();
        this.data = new Data(baseDir)
        this.renderer = new Renderer(this, baseDir)
        this.watcher = new Watcher(baseDir,
            {
                onInputsChanged: () => console.log('inputs changed'),
                onParamlistsChanged: () => this.navbar.refreshParamlistsWidgets(),
                onScriptChanged: () => { this.scriptPage.updateScript(); this.renderer.renderLast() }
            }
        )

        this.navbar = new Navbar(this, (page) => this.setPage(page));
        this.scriptPage = new ScriptPage(this);
        this.inputsPage = new InputsPage(this);
        this.paramlistsPages = new Map<string, ParamlistPage>()

        this.content = new BoxV();
        this.currentPage = this.scriptPage
        this.content.add(this.scriptPage)

        this.add(this.navbar);
        this.add(this.content);
        this.class('main')

        // this.showMaximized();
    }

    setPage(page: Page) {
        this.currentPage.hide()
        this.currentPage = page
        this.content.add(page)
        page.show()
        page.onOpen()
    }

    closeProject() {
        this.watcher.stop()
        Local.clearCurrentProjectLocation();
        INDEX.init()
        // (global as any).win = new OpenProjectWindow();
    }

}