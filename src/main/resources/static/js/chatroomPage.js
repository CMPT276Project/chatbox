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

// Image uploading

const displayUploadContainer = document.getElementById('display-upload-container');
document.getElementById('fileInput').addEventListener('change', function() {
    var fileInput = document.getElementById('fileInput');
    var fileNameDisplay = document.getElementById('fileName');

    if (fileInput.files.length > 0) {
        var fileName = fileInput.files[0].name;
        fileNameDisplay.textContent = fileName;
        displayUploadContainer.style.display = 'block';
    }
});

function removeFile() {
    var fileInput = document.getElementById('fileInput');
    var fileNameDisplay = document.getElementById('fileName');

    fileInput.value = '';
    fileNameDisplay.textContent = '';
    displayUploadContainer.style.display = 'none';
}

// Handle send

async function handleSend(uid, senderName) {
    // Check if file is being uploaded
    let fileInput = document.getElementById('fileInput');
    if (fileInput.files.length > 0) {
        let file = fileInput.files[0];
        let formData = new FormData();
        formData.append('file', file);
        formData.append('uid', uid);
        formData.append('senderName', senderName);
        formData.append('timeStampMilliseconds', new Date().getTime().toString());
        const res = await fetch('/upload', {
            method: 'POST',
            enctype: 'multipart/form-data',
            body: formData
        });
        if (res.ok) {
            console.log('File uploaded');
            removeFile();
        } else {
            console.error('Error uploading file');
        }
    }
    // Always send text message
    sendMessage(uid, senderName);
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