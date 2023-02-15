var roomId = document.getElementById("roomId").value;
var crewId = document.getElementById("crewId").value;
var sockJs;
var stomp;
var line;

window.addEventListener('beforeunload', () => {
    // 명세에 따라 preventDefault는 호출해야하며, 기본 동작을 방지합니다.
    var username = document.getElementById("myName").innerHTML;
    stomp.send('/pub/chat/out', {}, JSON.stringify({roomId: roomId, writer: username, stat: 0}));
    readSave();
});


//나가기 전 알림박스

window.addEventListener('unload', () => {

})
//나갔을때 이벤트 발생

//로드가 완료되었을때


document.addEventListener("DOMContentLoaded", function () {
    console.log("DOMContentLoaded..");
    // findMember();
    findMember();
    loadFetcher();
    var username = document.getElementById("myName").innerHTML;

    sockJs = new SockJS("/stomp/chat", null, {transports: ["websocket", "xhr-streaming", "xhr-polling"]});
    //1. SockJS를 내부에 들고있는 stomp를 내어줌
    stomp = Stomp.over(sockJs);

    //2. connection이 맺어지면 실행
    stomp.connect({}, function () {
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

            if (writer === username) {
                str = "<div class='chatbox__messages__user-message'>";
                str += "<div style='float: right;' class='chatbox__messages__user-message--ind-message'>";
                str += "<p style='color: #6c757d;  size: 3em' >" + dateTime + "</p>";
                str += "<p className=\"name\">" + writer + "</p>";
                str += "<br/>"
                str += "<span className=\"message\">" + content.message + "</span>";
                str += "</div>";
                str += "</div>";
                $("#messagearea").append(str);
                // $('#messagearea').scrollTop($('#messagearea')[0].scrollHeight);
            } else {
                str = "<div class='chatbox__messages__user-message'>";
                str += "<div style='float: left' class='chatbox__messages__user-message--ind-message'>";
                str += "<p style='color: #6c757d;  size: 3em' >" + dateTime + "</p>";
                str += "<p className=\"name\">" + writer + "</p>";
                str += "<br/>"
                str += "<span className=\"message\">" + content.message + "</span>";
                str += "</div></div>";
                $("#messagearea").append(str);
                // $('#messagearea').scrollTop($('#messagearea')[0].scrollHeight);
            }

            findMember(content);


            if (content.state === 1) {
                $('#live_' + content.writer).attr('class', 'chatbox__user--busy');
            }

            // if(content.stat === 1){
            //     $('#live_'+writer).attr('class','chatbox__user--active');
            // }
            //
            // if(content.stat === 0){
            //     $('#live_'+writer).attr('class','chatbox__user--busy');
            // }
        });
        stomp.send('/pub/chat/enter', {}, JSON.stringify({roomId: roomId, writer: username}))


        //3. send(path, header, message)로 메세지를 보낼 수 있음
    });
    console.log("DOMContentLoaded..END");

});

$("#button-send").on("click", function (e) {
    var userName = document.getElementById("myName").innerHTML;
    var msg = document.getElementById("msg");
    if (msg.value.trim() == '') {
        console.log("공백");
        return;
    }
    fetcher();
    stomp.send('/pub/chat/message', {}, JSON.stringify({roomId: roomId, message: msg.value, writer: userName}));
    msg.value = '';
});
$(document).keyup(function (event) {
    var userName = document.getElementById("myName").innerHTML;
    var msg = document.getElementById("msg");
    if (event.keyCode === 13) {

        if (msg.value.trim() == '') {
            console.log("공백");
            return;
        }
        fetcher();
        stomp.send('/pub/chat/message', {}, JSON.stringify({roomId: roomId, message: msg.value, writer: userName}));
        msg.value = '';
    }
});


async function readFetch() {
    console.log("readFetch");
    var userName = document.getElementById("myName").innerHTML;
    let response = await fetch("/api/v1/chat/check/" + roomId + "/" + userName, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
    })
    if (response.ok) {
        let json = await response.json();

        var index = json.result.index;
        if (index === 0) {
            document.getElementsByClassName("chatbox__messages__user-message--ind-message").item(index).innerHTML =
                "<p id='thisisfo' style='color: #09f3a7;  size: 3em' > 처음이시군요! </p>" + document.getElementsByClassName("chatbox__messages__user-message--ind-message").item(index).innerHTML;
        } else {
            index = index - 1;
            document.getElementsByClassName("chatbox__messages__user-message--ind-message").item(index).innerHTML =
                "<p id='thisisfo' style='color: #09f3a7;  size: 3em' > 여기까지 읽었어요! </p>" + document.getElementsByClassName("chatbox__messages__user-message--ind-message").item(index).innerHTML;
            line = index;
        }
        var viewpoint = document.getElementById('thisisfo');
        viewpoint.scrollIntoView();
    }

    console.log("readFetch END");
}


async function readSave() {

    console.log("readSave");
    let response = await fetch("/api/v1/chat/check", {
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
    if (response.ok) {
        let json = await response.json();
        console.log(json);
    }
    console.log("readSave END");
}


async function loadFetcher() {

    console.log("loadFetcher");
    let response = await fetch("/api/v1/chat/" + roomId, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: 'include'
    })
    if (response.ok) {
        let json = await response.json();
        console.log(json);
        for (var i = 0; i < json.result.length; i++) {
            if (json.result[i].writer === document.getElementById("myName").innerHTML) {
                str = "<div class='chatbox__messages__user-message'>";
                str += "<div style='float: right' class='chatbox__messages__user-message--ind-message'>";
                str += "<p style='color: #6c757d;  size: 3em' >" + json.result[i].createdAt + "</p>";
                str += "<p className=\"name\">" + json.result[i].writer + "</p>";
                str += "<br/>"
                str += "<span className=\"message\">" + json.result[i].message + "</span>";
                str += "</div></div>";
                $("#messagearea").append(str);
            } else {
                str = "<div class='chatbox__messages__user-message'>";
                str += "<div style='float: left' class='chatbox__messages__user-message--ind-message'>";
                str += "<p style='color: #6c757d;  size: 3em' >" + json.result[i].createdAt + "</p>";
                str += "<p className=\"name\">" + json.result[i].writer + "</p>";
                str += "<br/>"
                str += "<span className=\"message\">" + json.result[i].message + "</span>";
                str += "</div></div>";
                $("#messagearea").append(str);
            }
        }
        readFetch();
    } else {
        let json = await response.json();
        console.log(json.result.message);
    }
    console.log("loadFetcher END");
}

async function fetcher() {
    console.log("fetcher");
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

    console.log("fetcher end");
}

// 참여자들 찾는 로직
async function findMember() {
    console.log("findMember");
    let response = await fetch("/api/v1/part/members/" + crewId, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    })

    if (response.ok) {
        var json = await response.json();
        console.log(json.result);
        var str = "<h1>User list</h1>";
        for (var i in json.result) {
            str += "<div class='chatbox__user--busy' id='" + "live_" + json.result[i].joinUserName + "'>"
            str += "<p>"
            str += json.result[i].joinUserName;
            str += "</p>"
            str += "</div>"
        }
        document.getElementById("userArea").innerHTML = str;
    }
    console.log("findMember END");
}

// 참여자들 찾는 로직
async function findMember(content) {
    console.log("findMember Content");
    let response = await fetch("/api/v1/part/members/" + crewId, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    })

    if (response.ok) {
        var json = await response.json();
        console.log(json.result);
        var str = "<h1>User list</h1>";
        for (var i in json.result) {
            str += "<div class='chatbox__user--busy' id='" + "live_" + json.result[i].joinUserName + "'>"
            str += "<p>"
            str += json.result[i].joinUserName;
            str += "</p>"
            str += "</div>"
        }
        document.getElementById("userArea").innerHTML = str;
        for (const user of content.userList) {
            $('#live_' + user).attr('class', 'chatbox__user--active');
        }
    }
    console.log("findMember Content END");
}

// 크루상세내역 조회
async function findCrewInfo() {
    console.log("findCrewInfo");
    let response = await fetch("/api/v1/crews/" + crewId, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    })

    if (response.ok) {
        var json = await response.json();
        console.log(json.result);
        document.getElementById("crewTitle").innerHTML = json.result.title;
        document.getElementById("crewContent").innerHTML = "내용<br>" + json.result.content;
        document.getElementById("crewSport").innerHTML = json.result.sportEnum;
        document.getElementById("crewLimit").innerHTML = "모집인원 : " + json.result.crewLimit;
        document.getElementById("createdAt").innerHTML = "모임일자 : " + json.result.date;
        document.getElementById("crewStrict").innerHTML = json.result.strict;

        mapRender();
    }
    {
        console.log("findCrewInfo end");
    }


// 카카오맵 렌더링
    function mapRender() {
        var mapContainer = document.getElementById('map'), // 지도를 표시할 div
            mapOption = {
                center: new kakao.maps.LatLng(33.450701, 126.570667), // 지도의 중심좌표
                level: 3 // 지도의 확대 레벨
            };

        // 지도를 생성합니다
        var map = new kakao.maps.Map(mapContainer, mapOption);

        // 주소-좌표 변환 객체를 생성합니다
        var geocoder = new kakao.maps.services.Geocoder();

        var district = document.getElementById('crewStrict').innerHTML.trim();
        console.log(district);

        // 주소로 좌표를 검색합니다
        geocoder.addressSearch(district, function (result, status) {

            // 정상적으로 검색이 완료됐으면
            if (status === kakao.maps.services.Status.OK) {

                var coords = new kakao.maps.LatLng(result[0].y, result[0].x);

                // 결과값으로 받은 위치를 마커로 표시합니다
                var marker = new kakao.maps.Marker({
                    map: map,
                    position: coords
                });

                // 인포윈도우로 장소에 대한 설명을 표시합니다
                var infowindow = new kakao.maps.InfoWindow({
                    content: '<div style="width:150px;text-align:center;padding:6px 0;">여기서 만나요!</div>'
                });
                infowindow.open(map, marker);

                // 지도의 중심을 결과값으로 받은 위치로 이동시킵니다
                map.setCenter(coords);
            }
        });
    }
}

// document.getElementById('messagearea').scrollBy(0,document.getElementById("thisisfo").getBoundingClientRect().top);
