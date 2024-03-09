// Temp code

async function sendMessage() {
    if (!stompClient.active) {
        console.log('Not connected');
        return;
    }
    stompClient.publish({
        destination: "/app/input/" + 1, // TODO: get chatroom id
        body: JSON.stringify({'content': $("#name").val()}) // TODO: Get value from html element
    });
}

async function handleUserLogIn() {
    const username = document.getElementById("uname").value;
    const password = document.getElementById("psw").value;
    if (await validCredentials(username, password)) {
        console.log('Login successful');
        let tmp = document.getElementById("loggedin");
        tmp.innerText = "Logged in as " + username;
        await connect();
        await subscribeToRooms(username);
    } else {
        console.log('Login failed');
    }
}

function appendMessage(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $( "form" ).on('submit', (e) => e.preventDefault());
    $( "#send" ).click(() => sendMessage());
});

const isLoggedIn = false;

async function validCredentials(username, password) {
    try {
        const response = await fetch('/user/validateUser', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });
        const text = await response.text();
        return text ? true : false;
    }
    catch (error) {
        console.error(error);
        return false;
    }
}




// functions for chatroom

async function fetchUserInfo(username) {
    try {
        const response = await fetch('/user/getUserInfo/' + username, {
            method: 'GET'
        });
        const text = await response.text();
        return text ? JSON.parse(text) : {};
    }
    catch (error) {
        console.error(error);
        return {};
    }
}

async function fetchChatRoomIds(username) {
    try {
        const response = await fetch('/chatroom/getChatroomIds/' + username, {
            method: 'GET'
        });
        const text = await response.text();
        return text ? JSON.parse(text) : [];
    }
    catch (error) {
        console.error(error);
        return [];
    }
}

async function subscribeToRooms(username) {
    const chatIds = await fetchChatRoomIds(username);
    chatIds.forEach(chatId => {
        stompClient.subscribe('/topic/output/' + chatId, (response) => {
            appendMessage(JSON.parse(response.body).content);
        });
    });
}

async function createChatRoom(username1, username2) {
    try {
        const response = await fetch('/chatroom/createChatroom', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username1: username1,
                username2: username2
            })
        });
    }
    catch (error) {
        console.error(error);
    }
}

// Input: chatRoomId (int or string), content (string), sender (string), recipient (string)
// Output: None
async function storeMessage(chatRoomId, content, sender) {
    try {
        const response = await fetch('/message/storeMessage', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                chatRoomId: chatRoomId,
                content: content,
                sender: sender,
                timeStampMilliseconds: new Date().getTime().toString()
            })
        });
    }
    catch (error) {
        console.error(error);
    }
}

// Input: chatRoomId (int or string)
// Output: Array of messages sorted from oldest to newest
async function fetchMessages(chatRoomId) {
    try {
        const response = await fetch('/message/getMessages/' + chatRoomId, {
            method: 'GET'
        });
        const text = await response.text();
        return text ? JSON.parse(text) : [];
    }
    catch (error) {
        console.error(error);
        return [];
    }
}



// Must call after logging in to connect user
async function connect() {
    await stompClient.activate();
}

async function disconnect() {
    await stompClient.deactivate();
    console.log("Disconnected");
}



// DO NOT CHANGE
const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/websocket',
});

stompClient.onConnect = (frame) => {
    console.log('Connected: ' + frame);
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};