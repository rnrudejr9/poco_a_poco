
document.getElementById("comment_btn").addEventListener('click',makeComments);

async function makeComments() {
    var url = "/api/v1/crews/" + +document.getElementById("commentid").value + "/comments";
    let response = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": localStorage.getItem('jwt'),
            "Authentication": localStorage.getItem('jwt')
        },
        body: JSON.stringify({
            comment: document.getElementById("comment").value,
        })
    });
    if(response.ok){
        let json = await response.json();
        document.getElementById("area_comment").innerHTML = json.result.message;
    }else{
        let json = await response.text();
        document.getElementById("area_comment").innerHTML = json;
    }
}
