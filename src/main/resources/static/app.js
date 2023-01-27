
document.getElementById("login").addEventListener('click',loginTest);
document.getElementById("join").addEventListener('click',joinTest);
document.getElementById("logout").addEventListener('click',logout);

async function joinTest() {
    let response = await fetch("/api/v1/users/join", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            userId: document.getElementById( "userid").value,
            userName: document.getElementById("username").value,
            password: document.getElementById("password").value,
            passwordConfirm: document.getElementById("password2").value,
            address: "hello"
        })
    });
    if(response.ok){
        let json = await response.json();
        document.getElementById("area").innerHTML = json.result.message;
    }else{
        let json = await response.text();
        document.getElementById("area").innerHTML = json;
    }

}

async function loginTest() {
    let response = await fetch("/api/v1/users/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            userId: document.getElementById("username1").value,
            password: document.getElementById("password1").value
        })
    })
    if(response.ok){
        let json = await response.json();
        document.getElementById("area_login").innerHTML = json.resultCode;
        localStorage.setItem("jwt",'Bearer ' + json.result.accessToken);
        console.log(localStorage.getItem("jwt"));

    }else{
        let json = await response.text();
        document.getElementById("area_login").innerHTML = json;
    }
    ;
}

async function logout() {
    localStorage.clear();
    document.getElementById("area_logout").innerHTML = "로그아웃 성공";
}







