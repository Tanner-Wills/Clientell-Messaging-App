import BaseClass from "../util/baseClass.js";
import axios from 'axios';

export default class MessageClient extends BaseClass {

    constructor(props ={})  {
        super();
        const methodsToBind = ['clientLoaded', 'createMessage', 'getMessage'];
        this.bindClassMethods(methodsToBind, this);
        this.props = props;
        this.clientLoaded(axios);
    }

    clientLoaded(client) {
        this.client = client;
        if (this.props.hasOwnProperty("onReady")) {
            this.props.onReady();
        }
    }

    //POST request to create a new message. Requires threadId, sender, & content of message
    async createMessage(threadId, sender, content, errorCallback) {
        try {
            const response = await this.client.post(`/Messages`, {
                    "threadId": threadId,
                    "sender": sender,
                    "message": content
            });
            return response.data;
        } catch (error) {
            this.showMessage(`User is not part of the thread!`);
            this.handleError("createMessage", error, errorCallback);
        }

    }

    //GET request to return a message. Requires messageId
    async getMessage(id, errorCallback) {
        try {
            const response = await this.client.get(`/Messages/${id}`);
            return response.data;
        } catch (error) {
            this.handleError('getMessage', error, errorCallback);
        }
    }



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
