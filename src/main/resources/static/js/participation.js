async function findPartSign() {
    document.getElementById("allowArea").innerHTML = "";
    let response = await fetch("/api/v1/part/sign", {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
    })
    console.log("end");
    if (response.ok) {
        var json = await response.json();
        console.log(json);
        for (var i in json.result) {
            var str = "<input type='hidden' id= " + i + " name='" + json.result[i].joinUserName + "' value='" + json.result[i].crewId + "'>"
            str += "<li class='dropdown-item'>";
            // onclick='participate(" + i + ")'"+ "
            str += "CREW : " + json.result[i].crewId + " title: " + json.result[i].crewTitle + " userName : " + json.result[i].joinUserName;

            str += "<button class='btn btn-primary' id='successjoin' onclick='participate(" + i + ")'" + ">";
            str += "</button>"
            str += "<button class='btn btn-danger' id='rejectjoin' onclick='reject(" + i + ")'" + ">";
            str += "</button>"
            str += "</li>";
            console.log("findPartSign");
            console.log(str);
            document.getElementById("allowArea").innerHTML += str;
        }

    } else {
        var json = await response.json();
        console.log(document.getElementById("0"));
    }
}

async function reject(index) {
    console.log(document.getElementById(index));
    var crewId = document.getElementById(index).value;
    var userName = document.getElementById(index).name;
    let response = await fetch("/api/v1/part/reject", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify({
            crewId: crewId,
            userName: userName
        })
    })
    console.log("end");
    if (response.ok) {
        var json = await response.json();

        console.log("reject");
        console.log(json);
    } else {
        var json = await response.json();
        console.log(json);
    }
}


async function participate(index) {
    console.log(document.getElementById(index));
    var crewId = document.getElementById(index).value;
    var userName = document.getElementById(index).name;
    let response = await fetch("/api/v1/part", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify({
            crewId: crewId,
            userName: userName
        })
    })
    console.log("end");
    if (response.ok) {
        var json = await response.json();
        console.log("participate");
        console.log(json);
    } else {
        var json = await response.json();
        console.log(json);
    }
}