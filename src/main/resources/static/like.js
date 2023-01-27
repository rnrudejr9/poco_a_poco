
async function likeCrew (crewId) {
    const con_check = confirm("좋아요를 누르시겠습니까?");
    const url = "/crews/" + crewId + "/like";
    if(con_check === true) {
        let response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": localStorage.getItem('jwt'),
                "Authentication": localStorage.getItem('jwt')
            },
        })
        if (response.ok) {
            console.log(response);
            window.location.href = '/crews/' + crewId;
        } else {
            console.log("좋아요 인증 과정 중 에러가 발생했습니다.");
        }
    }
}