var roomId = document.getElementById("roomId").value;
var crewId = document.getElementById("crewId").value;
var sockJs;
var stomp;
var line;

window.addEventListener('beforeunload', (event) => {
    // 명세에 따라 preventDefault는 호출해야하며, 기본 동작을 방지합니다.
    event.preventDefault();
    event.returnValue = '';
});
//나가기 전 알림박스

window.addEventListener('unload',()=>{
    var username = document.getElementById("myName").innerHTML;
    readSave();
    stomp.send('/pub/chat/out', {}, JSON.stringify({roomId: roomId, writer: username}));
    stomp.disconnect();
})
//나갔을때 이벤트 발생


document.addEventListener("DOMContentLoaded",function(){

    findMember();
    loadFetcher();
    var username = document.getElementById("myName").innerHTML;

    sockJs = new SockJS("/stomp/chat", null, {transports: ["websocket", "xhr-streaming", "xhr-polling"]});
    //1. SockJS를 내부에 들고있는 stomp를 내어줌
    stomp = Stomp.over(sockJs);

    stomp.heartbeat.outgoing = 1000;
    stomp.heartbeat.incoming = 1000;

    //2. connection이 맺어지면 실행
    stomp.connect({}, function (){
        console.log("STOMP Connection")
        //4. subscribe(path, callback)으로 메세지를 받을 수 있음
        stomp.subscribe("/sub/chat/room/" + roomId, function (chat) {

            var content = JSON.parse(chat.body);
            var writer = content.writer;
            const d = new Date();
            const date = d.toISOString().split('T')[0];
            const time = d.toTimeString().split(' ')[0];
            var dateTime = date + ' ' + time;
            var str = '';

            if(writer === username){
                str = "<div class='chatbox__messages__user-message'>";
                str += "<div style='float: right;' class='chatbox__messages__user-message--ind-message'>";
                str += "<span style='color: #6c757d;  size: 3em' >" + dateTime + "</span>";
                str += "<p className=\"name\">" + writer + "</p>";
                str +=  "<br/>"
                str += "<p className=\"message\">" + content.message + "</p>";
                str += "</div>";
                str += "</div>";
                $("#messagearea").append(str);
                $('#messagearea').scrollTop($('#messagearea')[0].scrollHeight);
            }
            else{
                str = "<div class='chatbox__messages__user-message'>";
                str += "<div style='float: left' class='chatbox__messages__user-message--ind-message'>";
                str += "<span style='color: #6c757d;  size: 3em' >" + dateTime + "</span>";
                str += "<p className=\"name\">" + writer + "</p>";
                str +=  "<br/>"
                str += "<p className=\"message\">" + content.message + "</p>";
                str += "</div></div>";
                $("#messagearea").append(str);
                $('#messagearea').scrollTop($('#messagearea')[0].scrollHeight);
            }
        });
        stomp.send('/pub/chat/enter', {}, JSON.stringify({roomId: roomId, writer: username}))
        //3. send(path, header, message)로 메세지를 보낼 수 있음
    });

    $("#button-send").on("click", function(e){
        var msg = document.getElementById("msg");
        console.log(username + ":" + msg.value);
        fetcher();
        stomp.send('/pub/chat/message', {}, JSON.stringify({roomId: roomId, message: msg.value, writer: username}));
        msg.value = '';
    })
});


async function readFetch(){
    var userName = document.getElementById("myName").innerHTML;
    let response = await fetch("/api/v1/chat/check/"+roomId+ "/" + userName,{
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
    })
    if(response.ok){
        let json = await response.json();

        var index= json.result.index;
        if(index === 0){
            document.getElementsByClassName("chatbox__messages__user-message--ind-message").item(index).innerHTML =
                "처음이시군요!" + document.getElementsByClassName("chatbox__messages__user-message--ind-message").item(index).innerHTML;
        }else{
            index = index - 1;
        document.getElementsByClassName("chatbox__messages__user-message--ind-message").item(index).innerHTML =
            "<span id='thisisfo' style='color: #09f3a7;  size: 3em' > 여기까지 읽었어요! </span>" + document.getElementsByClassName("chatbox__messages__user-message--ind-message").item(index).innerHTML;

            line = index;
        }


    }
}


async function readSave(){
    let response = await fetch("/api/v1/chat/check",{
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
            userName: document.getElementById("myName").innerHTML,
            roomId: roomId
        })
    })
    if(response.ok){
        let json = await response.json();
        console.log(json);
    }
}


async function loadFetcher(){
    let response = await fetch("/api/v1/chat/" + roomId,{
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: 'include'
    })
    if(response.ok){
        let json = await response.json();
        console.log(json);
        for(var i = 0;i<json.result.length;i++){
            if(json.result[i].writer === document.getElementById("myName").innerHTML ){
                str = "<div class='chatbox__messages__user-message'>";
                str += "<div style='float: right' class='chatbox__messages__user-message--ind-message'>";
                str += "<span style='color: #6c757d;  size: 3em' >" + json.result[i].createdAt + "</span>";
                str += "<p className=\"name\">" + json.result[i].writer + "</p>";
                str +=  "<br/>"
                str += "<p className=\"message\">" + json.result[i].message + "</p>";
                str += "</div></div>";
                $("#messagearea").append(str);
                $('#messagearea').scrollTop($('#messagearea')[0].scrollHeight);
            }else{
                str = "<div class='chatbox__messages__user-message'>";
                str += "<div style='float: left' class='chatbox__messages__user-message--ind-message'>";
                str += "<span style='color: #6c757d;  size: 3em' >" + json.result[i].createdAt + "</span>";
                str += "<p className=\"name\">" + json.result[i].writer + "</p>";
                str +=  "<br/>"
                str += "<p className=\"message\">" + json.result[i].message + "</p>";
                str += "</div></div>";
                $("#messagearea").append(str);
                $('#messagearea').scrollTop($('#messagearea')[0].scrollHeight);
            }
        }
        readFetch();
    }else{
        let json = await response.json();
        console.log(json.result.message);
    }
}

async function fetcher() {
    let response = await fetch("/api/v1/chat", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify({
            writer: document.getElementById("myName").innerHTML,
            message: document.getElementById("msg").value,
            roomId: roomId
        })
    })
    console.log("end");
    console.log(response);
    $('#messagearea').scrollTop($('#messagearea')[0].scrollHeight);
}

async function findMember(){
    console.log(crewId);
    let response = await fetch("/api/v1/part/members/"+crewId, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    })

    if(response.ok){
        var json = await response.json();
        console.log(json.result);
        var str = "<h1>User list</h1>";
        for (var i in json.result) {
            str += "<div class='chatbox__user--active'>"
            str += "<p>"
            str += json.result[i].joinUserName;
            str += "</p>"
            str += "</div>"
        }

        console.log(str);
        document.getElementById("userArea").innerHTML = str;
    }
}


// document.getElementById('messagearea').scrollBy(0,document.getElementById("thisisfo").getBoundingClientRect().top);