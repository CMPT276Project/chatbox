window.onload = async function () {
    connect();
    const messages = await getMessages();
    messages.forEach(message => appendMessage(message.content));
}

userId = 1;

async function sendMessage() {
    if (!stompClient.active) {
        console.log('Not connected');
        return;
    }
    stompClient.publish({
        destination: "/app/input",
        body: JSON.stringify(
            {
                uid: userId,
                content: $("#name").val(),
                timeStampMilliseconds: new Date().getTime()
            })
    });
}

async function getMessages() {
    if (!stompClient.active) {
        console.log('Not connected');
        return;
    }
    const result = await fetch('/message/getAll');
    return await result.json();
}

function appendMessage(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $("#send").click(() => sendMessage());
});




// Must call after logging in to connect user
function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
}

// DO NOT CHANGE
const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/websocket',
});

stompClient.onConnect = (frame) => {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/output', (message) => {
        appendMessage(JSON.parse(message.body).content);
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};