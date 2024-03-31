window.onload = function () {
    connect();
    document.getElementById('messageForm').addEventListener('submit', function(event) {
        event.preventDefault();
        document.getElementById('sendButton').click();
    });
}


// Handle message send and/or file upload

async function handleSend(uid, senderName) {
    // Check if file is being uploaded
    let fileInput = document.getElementById('fileInput');
    if (fileInput.files.length === 0) {
        sendMessage(uid, senderName);
        return;
    }
    let file = fileInput.files[0];
    let formData = new FormData();
    var messageContent = document.getElementById("messageInput").value;
    formData.append('file', file);
    formData.append('content', messageContent);
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

function showMessage(content) {
    const message = document.createElement('span');
    message.innerHTML = content.senderName + ": " + content.content;
    const div = document.createElement('div');
    div.classList.add('chat-message');
    div.appendChild(message);
    if (content.fileId != -1) {
        const fileUriString = `/download/${content.fileId}`;
        const fileName = content.fileName;
        const svgString = `<svg xmlns="http://www.w3.org/2000/svg" style="margin-left: 10px;" width="16" height="16"fill="black" class="bi bi-download" viewBox="0 0 16 16">
                            <path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5"/>
                            <path d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708z"/>
                            </svg>`
        const fileElement = document.createElement('a');
        const uri = document.createElement('span');
        uri.textContent = fileName;
        fileElement.href = fileUriString;
        fileElement.appendChild(uri);
        fileElement.innerHTML += svgString;
        fileElement.classList.add('d-flex', 'align-items-center', 'mt-3')
        div.appendChild(fileElement);
    }
    document.getElementById("chatWindow").appendChild(div);
    document.getElementById("messageInput").value = '';
    scrollToBottom();
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