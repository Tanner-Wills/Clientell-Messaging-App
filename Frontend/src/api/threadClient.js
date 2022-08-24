import BaseClass from '../util/baseClass';
import axios from 'axios'

export default class ThreadClient extends BaseClass {

    constructor(props = {}) {
        super();
        const methodsToBind = ['clientLoaded', 'createThread','getThread','deleteThread', 'getAllThreads'];
        this.bindClassMethods(methodsToBind, this);
        this.props = props;
        this.clientLoaded(axios);
    }

    clientLoaded(client) {
        this.client = client;
        if (this.props.hasOwnProperty('onReady')) {
            this.props.onReady();
        }
    }

/** REST API Request methods -----------------------------------------------------------------------------------------*/

    //POST request to create a new thread. Requires title and two users. ThreadId is auto-generated UUID from backend.
    async createThread(name, user1, user2, errorCallback) {
        try {
            const response = await this.client.post(`/Threads`,
                {
                    "threadTitle": name,
                    "users": [user1, user2]
                }
            );
            return response.data;
        } catch (error) {
            this.handleError("createThread", error, errorCallback);
        }
    }

    //GET request to return a thread. Requires a threadId.
    async getThread(id, errorCallback) {
        try {
            const response = await this.client.get(`/Threads/${id}`);
            return response.data;
        } catch (error) {
            this.handleError('getThread', error, errorCallback);
        }
    }

    //GET request to return all thread. Requires a threadId.
        async getAllThreads(errorCallback) {
            try {
                const response = await this.client.get(`/Threads`);
                return response.data;
            } catch (error) {
                this.handleError('getAllThreads', error, errorCallback);
            }
        }

    //DELETE request to delete a thread. Requires a thread Id. Will also delete messages attached to thread.
    async deleteThread(id, errorCallback) {
        try {
            const response = await this.client.delete(`/Threads/${id}`);
            return response.data;
        } catch (error) {
            this.handleError('deleteThread', error, errorCallback);
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
