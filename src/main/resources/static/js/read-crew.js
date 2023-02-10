


async function findMember(){
    var crewId = document.getElementById('crewIdJoin').value;
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
        var str = "<ul class='list-group'>";
        for (var i in json.result) {
            str += "<li class='list-group-item'>"
            str += "<span>"
            str += "참여자 : " + json.result[i].joinUserName;
            str += "</span>"
            str += "</li>"
        }
        str += "</ul>"

        document.getElementById("members").innerHTML = str;
    }
}
async function enterCheck(){
    var crewId = document.getElementById('crewIdJoin').value;
    console.log(crewId);
    let response = await fetch("/api/v1/part/"+crewId, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    })
    if(response.ok){
        var json = await response.json();
        if(json.result.status === 0){
            document.getElementById("sendtogle").style.display = "block";
            document.getElementById("notallowed").style.display = "none";
            document.getElementById("signed").style.display = "none";
            document.getElementById("chatroom").style.display = "none";
            document.getElementById("members").style.display = "none";
        }
        if(json.result.status === 1) {
            document.getElementById("sendtogle").style.display = "none";
            document.getElementById("notallowed").style.display = "block";
            document.getElementById("signed").style.display = "none";
            document.getElementById("chatroom").style.display = "none";
            document.getElementById("members").style.display = "none";
        }
        if(json.result.status === 2){
            document.getElementById("sendtogle").style.display = "none";
            document.getElementById("signed").style.display = "block";
            document.getElementById("chatroom").style.display = "block";
            findMember();
            document.getElementById("members").style.display = "block";
        }
        console.log(json);
        console.log("enterCheck");
    }else{
        var json = await response.json();
        console.log(json);
    }
}



async function joinCrewAwait(){
    var crewId = document.getElementById('crewIdJoin').value;
    console.log(crewId);
    let response = await fetch("/api/v1/part/gen", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify({
            crewId: crewId
        })
    })
    console.log("end");
    if(response.ok){
        var json = await response.json();
        alert(json.result);
        console.log(json);
        enterCheck();
    }else{
        var json = await response.json();
        console.log(json);
    }
}