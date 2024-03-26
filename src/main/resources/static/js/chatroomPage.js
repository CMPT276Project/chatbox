window.onload = function () {
    connect();
}

function scrollToBottom() {
    const chatContainer = document.getElementById('chatWindow');
    chatContainer.scrollTop = chatContainer.scrollHeight;
}

function sendMessage(uid, senderName) {
    var messageContent = document.getElementById("messageInput").value;
    if (messageContent) {
        stompClient.publish({
            destination: "/app/input",
            body: JSON.stringify(
                {
                    uid: uid,
                    senderName: senderName,
                    content: messageContent,
                    timeStampMilliseconds: new Date().getTime()
                })
        });
        document.getElementById("messageInput").value = '';
    }
}

// Message is a JSON object with fields: uid, senderName, content, timeStampMilliseconds
function showMessage(message) {
    var messageElement = document.createElement('div');
    messageElement.appendChild(document.createTextNode(message.senderName + ": " + message.content));
    messageElement.classList.add("chat-message");
    document.getElementById("chatWindow").appendChild(messageElement);
    scrollToBottom();
}

// Get array of messages from server sorted by oldest to newest
async function getMessages() {
    if (!stompClient.active) {
        console.log('Not connected');
        return;
    }
    const result = await fetch('/message/getAll');
    return await result.json();
}




//  Websocket connection

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
}

const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/websocket',
});

stompClient.onConnect = (frame) => {
    stompClient.subscribe('/topic/output', (message) => {
        showMessage(JSON.parse(message.body));
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};