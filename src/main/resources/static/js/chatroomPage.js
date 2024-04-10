let username = document.body.getAttribute('name');

window.onload = function () {
    connect();
    document.getElementById('messageForm').addEventListener('submit', function (event) {
        event.preventDefault();
        document.getElementById('sendButton').click();
    });
}

window.onbeforeunload = function() {
    stompClient.publish({destination: '/app/stopTyping', body: JSON.stringify({username: username})});
};

// Language objects for dropdown menu

const enObj = {
    fullName: 'English',
    countryCode: 'EN',
    flagEmoji: '🇬🇧',
}

const esObj = {
    fullName: 'Spanish',
    countryCode: 'ES',
    flagEmoji: '🇪🇸',
}

const frObj = {
    fullName: 'French',
    countryCode: 'FR',
    flagEmoji: '🇫🇷',
}

const itObj = {
    fullName: 'Italian',
    countryCode: 'IT',
    flagEmoji: '🇮🇹',
}

const languages = [enObj, esObj, frObj, itObj];

function initializeDropdownMenu(){

    // Allows the loop to differentiate between source language button and target language button
    let targetFlag = 0;

    const dropdownMenus = document.querySelectorAll(".dropdownMenu");
    dropdownMenus.forEach(() => {
        for (const language of languages) {
            const newListElement = document.createElement("li");
            newListElement.textContent = language.flagEmoji + " " + language.fullName;
            let languageObject = language;
            switch(targetFlag){
                case 0:
                    newListElement.addEventListener('click', () => updateSourceLanguage(languageObject))
                    break;
                case 1:
                    newListElement.addEventListener('click', () => updateTargetLanguage(languageObject))
                    break;
            }
            dropdownMenus[targetFlag].appendChild(newListElement);
            
        }
        targetFlag += 1;
    })
}

// Always runs on page load
initializeDropdownMenu();

function updateSourceLanguage(languageObj){
    document.querySelector(".sourceLang").textContent = languageObj.countryCode;
    document.querySelector(".sourceFlag").textContent = languageObj.flagEmoji;
}

function updateTargetLanguage(languageObj){
    document.querySelector(".targetLang").textContent = languageObj.countryCode;
    document.querySelector(".targetFlag").textContent = languageObj.flagEmoji;
}


// Handle message send and/or file upload
async function handleSend(uid, senderName) {
    var messageContent = document.getElementById("messageInput").value;
    // Translate message if source and target languages are different
    
    const sourceLanguage = document.querySelector(".sourceLang").textContent.toLowerCase(); // Get source language from dropdown
    const targetLanguage = document.querySelector(".targetLang").textContent.toLowerCase(); // Get target language from dropdown

    if (sourceLanguage !== targetLanguage && messageContent !== '') {
        messageContent = await translateText(messageContent, targetLanguage, sourceLanguage);
    }
    // End typing indicator
    clearInterval(typingTimeout);
    stopTyping(senderName);
    // Check if file is being uploaded
    let fileInput = document.getElementById('fileInput');
    if (fileInput.files.length === 0) {
        sendMessage(uid, senderName, messageContent);
    }
    else {
        let formData = new FormData();
        formData.append('file', fileInput.files[0]);
        formData.append('content', messageContent);
        formData.append('uid', uid);
        formData.append('senderName', senderName);
        formData.append('timeStampMilliseconds', new Date().getTime().toString());
        const res = await fetch('/upload', {
            method: 'POST',
            body: formData
        });
        if (res.ok) {
            console.log('File uploaded');
            removeFile();
        } else {
            console.error('Error uploading file');
        }
    }
}

function sendMessage(uid, senderName, text) {
    if (text) {
        stompClient.publish({
            destination: "/app/input",
            body: JSON.stringify(
                {
                    uid: uid,
                    senderName: senderName,
                    content: text,
                    timeStampMilliseconds: new Date().getTime()
                })
        });
        document.getElementById("messageInput").value = '';
    }
}

// Message is a JSON object with fields: uid, senderName, content, timeStampMilliseconds
function showMessage(message) {
    var messageElement = document.createElement('div');
    const date = new Date(parseInt(message.timeStampMilliseconds));
    const timeString = date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: true });
    messageElement.appendChild(document.createTextNode(timeString + ' ' + message.senderName + ': ' + message.content));
    document.getElementById("chatWindow").appendChild(messageElement);
}


// Translation API
async function translateText(text, targetLanguage, sourceLanguage) {
    const options = {
        method: "POST",
        url: "https://api.edenai.run/v2/translation/automatic_translation",
        headers: {
            authorization: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMzQzY2VkNDYtNDJjOC00M2Q3LWJmOTktMGM5MTlkMjhiMGNiIiwidHlwZSI6ImFwaV90b2tlbiJ9.5jLh4anDFvejYDlUIg0ml5JKaJfJHpCJc3ppsh1ekLQ",
        },
        data: {
            providers: "phedone",
            text: text,
            source_language: sourceLanguage,
            target_language: targetLanguage,
            fallback_providers: "",
        },
    };
    try {
        const response = await axios.request(options);
        return response.data.phedone.text;
    } catch (error) {
        console.error(error);
    }
}


// Display message in chat window
function showMessage(content) {
    const message = document.createElement('span');
    message.textContent = content.senderName + ": " + content.content;
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
    document.getElementById("chatMessages").appendChild(div);
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


// Typing indicator
let typingTimeout;

function startTyping(username) {
    clearTimeout(typingTimeout);
    stompClient.publish({
        destination: "/app/typing",
        body: JSON.stringify({ 'username': username })
    });
    typingTimeout = setTimeout(() => {
        stopTyping(username);
    }, 4000);
};

function stopTyping(username) {
    stompClient.publish({
        destination: "/app/stopTyping",
        body: JSON.stringify({ 'username': username })
    });
}

function showTypingIndicator(userList) {
    if (userList.length > 0) {
        document.getElementById('typingIndicator').textContent = userList.join(', ') + ' is typing...';
        document.getElementById('typingIndicator').style.display = 'block';
    } else {
        document.getElementById('typingIndicator').textContent = '';
        document.getElementById('typingIndicator').style.display = 'none';
    }
    scrollToBottom();
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
    stompClient.subscribe('/topic/typing', function (data) {
        showTypingIndicator(JSON.parse(data.body));
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};