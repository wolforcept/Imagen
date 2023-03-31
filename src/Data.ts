// import chokidar from 'chokidar';

import fs, { readdirSync } from 'fs'
import { defaultParamlist, ParamList } from './data/ParamList';

const verbose = false;

export class Data {

    constructor(private baseDir: string) { }

    //
    // SCRIPT

    getScript(): string {
        try {
            const data = fs.readFileSync(this.scriptFileLocation(), 'utf8');
            if (verbose)
                console.log(data);
            return data;
        } catch (err) {
            console.error(err);
            this.saveScript(`
const avatar = await loadImage('avatar.png')
const font = loadFont('Makepf.ttf', 20)

const { color } = varsObj

setFontColor(color)
drawImage(avatar, 0, 0)
drawText(font, 40, 60, ('' + color).toUpperCase())`
            )
            return ''
        }
    }

    saveScript(data: string) {
        fs.writeFileSync(this.scriptFileLocation(), data);
    }

    //
    // PARAMLISTS

    getParamlists(): string[] {
        return fs.readdirSync(this.baseDir).filter(x => x.endsWith('.paramlist'))
    }

    createParamlist() {

        const names = this.getParamlists().map(x => x.substring(0, x.length - 10));
        let newName = 'new paramlist'
        let i = 2
        while (names.find(x => x === newName))
            newName = 'new paramlist ' + i++

        const paramlist = { ...defaultParamlist, name: newName }

        fs.writeFileSync(`${this.baseDir}/${newName}.paramlist`, JSON.stringify(paramlist));
    }

    openParamlist(paramlistName: string): ParamList {
        const data = fs.readFileSync(`${this.baseDir}/${paramlistName}.paramlist`, 'utf8');
        return JSON.parse(data) as ParamList
    }

    saveParamlist(paramlist: ParamList) {
        const paramlistString = JSON.stringify(paramlist)
        fs.writeFileSync(`${this.baseDir}/${paramlist.name}.paramlist`, paramlistString)
    }

    // 
    // INPUTS

    getAllInputPaths() {
        return fs.readdirSync(this.inputFilesLocation()).filter(x => x.endsWith('.paramlist'))
    }

    //

    scriptFileLocation() {
        return `${this.baseDir}/script.js`
    }

    inputFilesLocation() {
        return `${this.baseDir}/inputs`
    }
}