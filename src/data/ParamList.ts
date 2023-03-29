export interface ParamLine {
    values: string[]
}

export interface ParamList {
    name: string
    lines: ParamLine[]
    width: number
    height: number
    varNames: string[]
}

export const defaultParamlist: ParamList = {
    name: 'new paramlist',
    lines: [],
    varNames: ['var1', 'var2', 'var3'],
    width: 100,
    height: 100,
}