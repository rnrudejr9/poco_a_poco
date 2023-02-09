function randomMatch() {

    let username = document.getElementById("myName").innerText;
    console.log(username);



    $.ajax({
        type: "POST",
        url: '/api/v1/match/random'+ "?username=" + username,
        success: function (data) {
            let listCnt = data;
            if (listCnt > 0) {
                Swal.fire({
                    icon: 'success',
                    title: '랜덤매칭이 시작되었습니다.\n' +
                        '매칭이 될때까지 대기해 주세요👍\n' +
                        '3명이 대기열에 들어오면 매칭됩니다',
                });
                // $("#randomCnt").empty();
                // $("#randomCnt").append('현재 대기중인 인원 :' + listCnt);
            }
        },
        error: function (request, status, error) {
            alert("로그인 후 랜덤매칭이 가능합니다.")
            // alert(request.responseText);
        }
    });

    // if (username.length > 0) {
    //     const randomEventSource = new EventSource("/sse/random")
    //     randomEventSource.onmessage(event => {
    //         const p = document.createElement("p")
    //         p.innerText = event.data;
    //         console.log(p);
    //         document.getElementById("messages").appendChild(p);
    //
    //
    //         let listCnt = event.data;
    //         console.log('데이터 = ' + listCnt);
    //         $("randomCnt").empty();
    //         $("randomCnt").append('현재 대기중인 인원 :' + listCnt);
    //     });
    // }



}