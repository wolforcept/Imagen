import { LocalStorage } from "node-localstorage";

export const currentProjectLocation = 'currentProjectLocation'

const appDataLocation = process.env.APPDATA
const _local = new LocalStorage(appDataLocation ? appDataLocation + '/Imagen/' : './config')

export const Local = {
    setCurrentProjectLocation: (projLoc: string) => _local.setItem(currentProjectLocation, projLoc),
    getCurrentProjectLocation: () => _local.getItem(currentProjectLocation),
    clearCurrentProjectLocation: () => _local.removeItem(currentProjectLocation),
}