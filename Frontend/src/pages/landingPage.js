import BaseClass from "../util/baseClass.js";
import DataStore from "../util/DataStore.js";
import UserClient from "../api/userClient.js";
import ThreadClient from "../api/threadClient.js";
import MessageClient from "../api/messageClient.js";

class LandingPage extends BaseClass {

    constructor() {
        super();
        this.bindClassMethods(['onSearchUser','onCreateUser','onSearchThread','onCreateThread',
                               'renderThreadStore','onCreateMessage', 'renderUsers', 'onGetUsers',
                               'onDeleteUser','onDeleteThread', 'onGetThreads'], this);
        this.dataStoreUser = new DataStore();
        this.dataStoreThreads = new DataStore();
        this.dataStoreThreadDisplay = new DataStore();
        this.dataStoreMessage = new DataStore();
    }


    /**
     *
     *  Once the page has loaded, set up the event handlers
     *
     **/

    async mount() {
        let userSearchButton = document.getElementById('userSearchButton');
            userSearchButton.addEventListener('click', this.onSearchUser);
        let newUserButton = document.getElementById('newUser');
            newUserButton.addEventListener('click', this.onCreateUser);
        let userDeleteButton = document.getElementById('userDeleteButton');
            userDeleteButton.addEventListener('click', this.onDeleteUser);
        let newThreadButton = document.getElementById('newThread');
            newThreadButton.addEventListener('click', this.onCreateThread);
        let threadGetButton = document.getElementById('threadSearchButton');
            threadGetButton.addEventListener('click', this.onSearchThread);
        let threadDeleteButton = document.getElementById('threadDeleteButton');
            threadDeleteButton.addEventListener('click', this.onDeleteThread);
        let sendMessage = document.getElementById('newMessageSendButton');
            sendMessage.addEventListener('click', this.onCreateMessage);

        this.userClient = new UserClient();
        this.threadClient = new ThreadClient();
        this.messageClient = new MessageClient();

        // listener for displaying messages after you search for a thread
        this.dataStoreThreadDisplay.addChangeListener(this.renderThreadStore);
        //this.dataStoreMessage.addChangeListener(this.renderThreadStore);

        // adding function for "Get all users" button
        this.dataStoreUser.addChangeListener(this.renderUsers)
    }

                                    /**-----------  Render Methods  -----------**/

/**  User Render  ----------------------------------------------------------------------------------------------------*/

     async renderUsers() {
                let resultArea = document.getElementById("get-all-users-info");
                const users = this.dataStoreUser.get("users");
                if (users) {
                    let content = "";
                    for (let user of users) {
                          // Display User Name
                          content += `<h3>${user.name}</h3>`
                          content += `<div style="text-align:left;">
                                      <p>
                                      <h4>${"User Id:"}</h4>
                                      </p>
                                      </div>`
                          content += `<div style="text-align:center;">
                                      <p>
                                      ${user.userId}
                                      </p>
                                      </div>`
                          // Display Threads
                          content += `<div style="text-align:left;">
                                      <p>
                                      <h4>${"Threads:"}</h4>
                                      </p>
                                      </div>`
                          for(let thread of user.threads) {
                                  content += `<div style="text-align:center;">
                                              <p>
                                              ${thread}
                                              </p>
                                              </div>`
                          }
                          content += "<hr>"
                        }
                resultArea.innerHTML = content;
                } else {
                    resultArea.innerHTML = "No Users";
                }
            }

/**  Thread Render  --------------------------------------------------------------------------------------------------*/

            async renderThreadStore(event) {
                    let resultArea = document.getElementById('get-thread-message');
                    const threads = this.dataStoreThreadDisplay.get("threads");
                    const threadTitleSearch = this.dataStoreThreads.get("search");
                    const allThreads = await this.threadClient.getAllThreads();

                    if (threads) {
                    let content = "";
                    for(let oneThread of allThreads) {
                        if(oneThread.threadId == threadTitleSearch) {
                            content += `<div style="text-align:center;">
                                         <h4>${"Subject: " + oneThread.threadTitle}</h4>
                                         </div>`
                                     }
                                 }
                    content += "<ul>";
                    for (let message of threads) {
                        const returnedMessage = await this.messageClient.getMessage(message, this.errorHandler);
                        const senderUser = returnedMessage.sender;
                        const user = await this.userClient.getUser(senderUser, this.errorHandler);
                                content += "<li>";
                                // display User name
                                content +=  `<div style="text-align:left;">
                                             <h4>${user.name + ":"}</h4>
                                             </div>`
                                // display message
                                content +=  `<div style="text-align:center;">
                                             <h4>${returnedMessage.message}</h4>
                                             </div>`
                           }
                           content += "</ul>";
                           resultArea.innerHTML = content;
                    } else {
                        resultArea.innerHTML = "No Message to Display";
                    }
                }

                                /**----------- Event Handlers -----------**/

/**  User Events  ----------------------------------------------------------------------------------------------------*/

    //Uses client to make GET with userID, then puts in dataStore, and loads on page thanks to listeners

    async onSearchUser(event) {
        event.preventDefault();

        let id = document.getElementById("userSearchField").value;
        this.dataStoreUser.set("user", null);

        const returnedUser = await this.userClient.getUser(id, this.errorHandler);
        this.dataStoreUser.set("user", returnedUser);

        if (result) {
            this.showMessage(`Got ${returnedUser.name}!`)
        } else {
            this.errorHandler("Could not locate user with that UserID. Please check your UserId and try again.");
        }
    }

    //Creates a New User, and once created, puts it in the appropriate dataStore, then loads it onto the page

    async onGetUsers() {
             let getAllUsersBox = await this.userClient.getAllUsers(this.errorHandler);
             this.dataStoreUser.set("users", getAllUsersBox);
         }

    async onCreateUser(event) {
        event.preventDefault();
        this.dataStoreUser.set("user", null);

        let name = document.getElementById("userSearchField").value;
        if (name == "") {
            this.showMessage(`Must enter a name!`)
            throw "Must enter a name!";
        }

        const createdUser = await this.userClient.createUser(name, this.errorHandler);
        this.dataStoreUser.set("user", createdUser);

        if (createdUser) {
            this.showMessage(`Created ${createdUser.name}!`)
        } else {
            this.errorHandler("Error creating!  Try again...");
        }
        this.onGetUsers();
    }

    //Deletes a user

    async onDeleteUser(event) {
        event.preventDefault();

        let id = document.getElementById('userSearchField').value;

        const deletedUser = await this.userClient.deleteUser(id, this.errorHandler);

        if (!deletedUser) {
            this.showMessage(`Deleted UserID ${id}!`)
        } else {
            this.errorHandler("Error deleting. Please check your UserId and try again.")
        }
        this.onGetUsers();
    }

/** Thread Events-----------------------------------------------------------------------------------------------------*/

    async onGetThreads(id) {
         let threadDisplay = await this.threadClient.getThread(id, this.errorHandler);
         this.dataStoreThreads.set("search", id);
         this.dataStoreThreadDisplay.set("threads", threadDisplay);
    }

    //Sends GET request with a threadId via a client, then displays it.
    async onSearchThread(event) {
        event.preventDefault();

        let id = document.getElementById('threadInputField').value;

        if(id == "") {
        this.showMessage(`Must fill out all fields!`);
        throw "Must fill out all fields!";
        }

        const returnedThread = await this.threadClient.getThread(id, this.errorHandler);
        this.dataStoreThreadDisplay.set("threads", returnedThread);
        this.dataStoreThreads.set("search", id);

        if (returnedThread) {
            this.showMessage(`Found thread: ${returnedThread.threadName}`);
        } else {
            this.errorHandler("Could not find thread with that ThreadID. Please check your ThreadID and try again.");
        }
        this.onGetThreads(id);
    }

    //Sends POST request with a thread via a client, then displays it.
    async onCreateThread(event) {
        event.preventDefault();

        let name = document.getElementById('createThreadNameField').value;
        let user1 = document.getElementById('createThreadUserOne').value;
        let user2 = document.getElementById('createThreadUserTwo').value;

        // error handling for empty fields or same user
        if (user1 == user2) {
        this.showMessage(`Must enter two unique userIds!`);
        throw "Must enter two unique userIds!";
        }
        if(name == "" || user1 == "" || user2 == "") {
        this.showMessage(`Must fill out all fields!`);
        throw "Must fill out all fields!";
        }

        const createdThread = await this.threadClient.createThread(name, user1, user2, this.errorHandler);
        this.dataStoreThreads.set("search", createdThread);

        if (createdThread) {
            this.showMessage(`Created thread ${createdThread.threadTitle}!`);
        } else {
            this.errorHandler("Error with POST request. Please try again.");
        }
        this.onGetUsers();
    }

    //Deletes thread with input id, via calling threadClient
    async onDeleteThread(event) {
        event.preventDefault();

        let id = document.getElementById('threadInputField').value;

        const deletedThread = await this.threadClient.deleteThread(id, this.errorHandler);

        if (!deletedThread) {
            this.showMessage(`Deleted ThreadID ${id}`);
        } else {
            this.errorHandler('Error deleting thread. Please check your ThreadID and try again.')
        }
        this.onGetUsers();
    }

/** Message Events ---------------------------------------------------------------------------------------------------*/

    //Sends POST request with threadId, userId, & content, via a client, then displays it.

    async onCreateMessage(event) {
        event.preventDefault();

        let threadId = document.getElementById('newMessageThreadId').value;
        let sender = document.getElementById('newMessageUserId').value;
        let content = document.getElementById('newMessageContent').value;

        if(threadId == "" || sender == "" || content == "") {
        this.showMessage(`Must fill out all message fields!`);
        throw "Must fill out all message fields!";
        }

        const createdMessage = await this.messageClient.createMessage(threadId, sender, content, this.errorHandler);
        this.dataStoreMessage.set("message", createdMessage);

        if (createdMessage) {
            this.showMessage(`Message Sent!`);
        } else {
            this.errorHandler("Error sending message. Please try again.");
        }
        this.onGetThreads(threadId);
        this.onGetThreads(threadId);
    }
}

/**
 *  Main method to run when the page contents have loaded.
 **/

const main = async () => {
    const landingPage = new LandingPage();
    landingPage.mount();
};

window.addEventListener('DOMContentLoaded', main);

