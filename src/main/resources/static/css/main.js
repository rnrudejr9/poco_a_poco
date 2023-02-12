document.getElementById("btn-s").addEventListener('click', crewMake2);

async function crewMake2() {
    let response = await fetch("/view/v1/crews", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": localStorage.getItem('jwt'),
            "Authentication": localStorage.getItem('jwt')
        },
        body: JSON.stringify({
            content: document.getElementById("content").value,
            title: document.getElementById("title").value
        })
    })
    if(response.ok){
        let json = await response.json()
        console.log(json);
        document.getElementById("area_crew").innerHTML = json.result.crewId + " 모임생성 " + json.result.message;
    }else{
        let json = await response.text();
        document.getElementById("area_crew").innerHTML = json;
    }
}