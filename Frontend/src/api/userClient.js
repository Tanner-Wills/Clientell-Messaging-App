import BaseClass from '../util/baseClass';
import axios from 'axios'


export default class UserClient extends BaseClass {

    constructor(props = {}) {
        super();
        const methodsToBind = ['clientLoaded', 'createUser','getUser', 'getAllUsers','deleteUser'];
        this.bindClassMethods(methodsToBind, this);
        this.props = props;
        this.clientLoaded(axios);
    }


    clientLoaded(client) {
        this.client = client;
        if (this.props.hasOwnProperty("onReady")){
            this.props.onReady();
        }
    }

/** REST API Request methods -----------------------------------------------------------------------------------------*/

    //POST request to create a new user. Requires a name. UserId is auto-generated from random UUID in backend.
    async createUser(name, errorCallback) {
        try {
        const response = await this.client.post(`/Users`,
        {
            "name": name
        });
            return response.data;
        } catch (error) {
              this.showMessage(`User already exists!`)
              throw "User already exists!";
              this.handleError('getUser', error, errorCallback);
        }
    }

    //GET request to get a user. Requires a userId.
    async getUser(id, errorCallback) {
        try {
            const response = await this.client.get(`/Users/${id}`);
            return response.data;
        } catch (error) {
            this.handleError('getUser', error, errorCallback);
        }
    }

    //GET request to get all users. No requirements.
    async getAllUsers(errorCallback) {
        try {
            const response = await this.client.get(`/Users`);
            return response.data;
        } catch (error) {
            this.handleError('getUser', error, errorCallback);
        }
    }

    //DELETE request to delete a user. Requires a userId.
    async deleteUser(id, errorCallback) {
        try {
            const response = await this.client.delete(`/Users/${id}`);
            return response.data;
        } catch (error) {
            this.handleError('deleteUser', error, errorCallback);
        }
    }

/** Error Handler ----------------------------------------------------------------------------------------------------*/

    handleError(method, error, errorCallback) {
        console.error(method + " failed - " + error);
        if (error.response.data.message !== undefined) {
            console.error(error.response.data.message);
        }
        if (errorCallback) {
            errorCallback(method + " failed - " + error);
        }
    }
}
