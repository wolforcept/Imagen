// import chokidar from 'chokidar';

import fs, { readdirSync } from 'fs'
import { defaultParamlist, ParamLine, ParamList, ParamVar } from './data/ParamList';
import csv from 'csv'
import { parse } from 'csv-parse/sync';


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
        this.saveParamlist(paramlist)
        // fs.writeFileSync(`${this.baseDir}/${newName}.paramlist`, JSON.stringify(paramlist, null, 2));
    }

    openParamlist(paramlistName: string): ParamList {
        const data = fs.readFileSync(`${this.baseDir}/${paramlistName}.paramlist`, 'utf8');
        return JSON.parse(data) as ParamList
    }

    async importCsvIntoParamlist(paramlistPath: string, paramlist: ParamList) {

        // const paramlistPath = "C:/Users/Miguel/Pictures/Imagen/lolgame/cards.paramlist.csv";
        const data = fs.readFileSync(paramlistPath, 'utf8');
        const parsedData: string[][] = parse(data, {
            delimiter: ';',
            escape: false,
            quote: null,
            relaxQuotes: true,
        })
        paramlist.lines = parsedData.map(line => ({ values: line } as ParamLine))
        this.saveParamlist(paramlist)
    }

    saveParamlist(paramlist: ParamList) {
        const paramlistString = JSON.stringify(paramlist, null, 2)
        fs.writeFileSync(`${this.baseDir}/${paramlist.name}.paramlist`, paramlistString)
    }

    //

    getAllInputPaths() {
        return fs.readdirSync(this.inputFilesLocation())
    }

    getAllOutputPaths() {
        return fs.readdirSync(this.outputFilesLocation())
    }

    openFolder(): void {
        require('child_process').exec(`start "" "${this.baseDir}"`);
    }

    scriptFileLocation() {
        return `${this.baseDir}/script.js`
    }

    inputFilesLocation() {
        return `${this.baseDir}/inputs`
    }

    outputFilesLocation() {
        return `${this.baseDir}/outputs`
    }
}
