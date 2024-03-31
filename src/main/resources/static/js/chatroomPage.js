window.onload = function () {
    connect();
}

// Handle message send and/or file upload
async function handleSend(uid, senderName) {
    // Check if file is being uploaded
    let fileInput = document.getElementById('fileInput');
    if (fileInput.files.length > 0) {
        let file = fileInput.files[0];
        let formData = new FormData();
        var messageContent = document.getElementById("messageInput").value;
        formData.append('file', file);
        formData.append('message', messageContent);
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
    else {
        sendMessage(uid, senderName);
    }
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


// Display message in chat window
// Message is a JSON object with fields: uid, senderName, content, timeStampMilliseconds
function showMessage(message) {
    var messageElement = document.createElement('div');
    messageElement.appendChild(document.createTextNode(message.senderName + ": " + message.content));
    messageElement.classList.add("chat-message");
    document.getElementById("chatWindow").appendChild(messageElement);
    scrollToBottom();
}

// Display file in chat window
function showFile(content) {
    const fileUriString = content.fileDownloadUrl;
    const fileName = content.fileName;
    const svgString = `<svg xmlns="http://www.w3.org/2000/svg" style="margin-left: 10px;" width="16" height="16"fill="black" class="bi bi-download" viewBox="0 0 16 16">
                        <path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5"/>
                        <path d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708z"/>
                        </svg>`
    const div = document.createElement('div');
    const fileElement = document.createElement('a');
    const uri = document.createElement('span');
    uri.textContent = fileName;
    fileElement.href = fileUriString;
    fileElement.appendChild(uri);
    fileElement.innerHTML += svgString;
    const message = document.createElement('p');
    message.innerHTML = content.message.senderName + ": " + content.message.content;
    div.appendChild(message);
    div.appendChild(fileElement);
    div.classList.add("chat-message");
    document.getElementById("chatWindow").appendChild(div);
    document.getElementById("messageInput").value = '';
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
document.getElementById('fileInput').addEventListener('change', function () {
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


// Scroll to bottom of chat window

function scrollToBottom() {
    const chatContainer = document.getElementById('chatWindow');
    chatContainer.scrollTop = chatContainer.scrollHeight;
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
    stompClient.subscribe('/topic/output/message', (message) => {
        showMessage(JSON.parse(message.body));
    });
    stompClient.subscribe('/topic/output/file', (content) => {
        showFile(JSON.parse(content.body));
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};