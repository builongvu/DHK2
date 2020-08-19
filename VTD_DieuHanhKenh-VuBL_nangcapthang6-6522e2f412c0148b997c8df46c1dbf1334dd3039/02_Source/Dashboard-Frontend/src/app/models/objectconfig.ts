export class ObjectConfig {
    functionType: number;
    id: number;
    objectCode: string;
    objectIcon: string;
    objectImg: string;
    objectName: string;
    objectNameI18N: string;
    objectType: string;
    objectUrl: string;
    ord: number;
    parentId: number;
    status: number;
}

export class ObjectConfigModel {
    functionType: number;
    id: number;
    objectCode: string;
    objectIcon: string;
    objectImg: string;
    objectName: string;
    objectNameI18N: string;
    objectType: string;
    objectUrl: string;
    ord: number;
    parentId: number;
    status: number;
    check: boolean;
    create: boolean;
    read: boolean;
    update: boolean;
    delete: boolean;
    children: ObjectConfigModel[];
}

