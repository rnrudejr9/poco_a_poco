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
                let randomMatchCancelBtn = document.getElementById("randomMatchCancel_btn");
                let randomMatchBtn = document.getElementById("randomMatch_btn");
                randomMatchCancelBtn.style.display = 'block';
                randomMatchBtn.style.display = 'none';
            }
        },
        error: function (request, status, error) {
            alert("로그인 후 랜덤매칭이 가능합니다.")
        }
    });
}

function randomMatchCancel() {

    let username = document.getElementById("myName").innerText;
    console.log(username);


    $.ajax({
        type: "POST",
        url: '/api/v1/match/random/cancel'+ "?username=" + username,
        success: function (data) {
            let listCnt = data;
            if (listCnt > 0) {
                Swal.fire({
                    icon: 'success',
                    title: '랜덤매칭이 취소되었습니다✔\n',
                });
                let randomMatchCancelBtn = document.getElementById("randomMatchCancel_btn");
                let randomMatchBtn = document.getElementById("randomMatch_btn");
                randomMatchCancelBtn.style.display = 'none';
                randomMatchBtn.style.display = 'block';
            }
        },
        error: function (request, status, error) {
            alert("로그인 후 랜덤매칭이 가능합니다.")
            // alert(request.responseText);
        }
    });
}