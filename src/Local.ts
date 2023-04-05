import { LocalStorage } from "node-localstorage";
import { ListType } from "./pages/InputsPage";

export const localId_currentProjectLocation = 'currentProjectLocation'
export const localId_listType = 'listType'

const appDataLocation = process.env.APPDATA
const _local = new LocalStorage(appDataLocation ? appDataLocation + '/Imagen/' : './config')

export const Local = {
    setCurrentProjectLocation: (projLoc: string) => _local.setItem(localId_currentProjectLocation, projLoc),
    getCurrentProjectLocation: () => _local.getItem(localId_currentProjectLocation),
    clearCurrentProjectLocation: () => _local.removeItem(localId_currentProjectLocation),
    setListType: (t: ListType) => { if (t) _local.setItem(localId_listType, t) },
    getListType: () => ((_local.getItem(localId_listType) as ListType) ?? 'List') as ListType,
}