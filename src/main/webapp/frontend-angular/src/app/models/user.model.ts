
export interface IAuthResponse {
    token?: string | null;
}

export interface IUser {
    id?: number;
    login?: string | null;
    password?: string | null;
    firstName?: string | null;
    lastName?: string | null;
    email?: string | null;
    activated?: boolean;
    langKey?: string | null;
}

export class User implements IUser {
    constructor(
        public id?: number,
        public login?: string | null,
        public password?: string | null,
        public firstName?: string | null,
        public lastName?: string | null,
        public email?: string | null,
        public activated?: boolean,
        public langKey?: string | null
    ) {}
}