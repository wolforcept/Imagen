export interface ParamLine {
    values: string[]
}

export interface ParamVar {
    name: string
    x: number
    y: number
    w: number
    h: number
    type?: 'text' | 'longText'
}

export interface ParamList {
    name: string
    lines: ParamLine[]
    width: number
    height: number
    vars: ParamVar[]
}

export const defaultParamlist: ParamList = {
    name: 'new paramlist',
    lines: [],
    vars: [
        { x: 0, y: 0, w: 1, h: 1, name: 'var1' },
        { x: 1, y: 0, w: 1, h: 1, name: 'var2' },
        { x: 0, y: 1, w: 1, h: 2, name: 'var3' },
    ],
    width: 100,
    height: 100,
}