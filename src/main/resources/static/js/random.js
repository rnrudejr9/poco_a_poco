function randomMatch() {

    let username = document.getElementById("myName").innerText;
    $.ajax({
        type: "POST",
        url: '/api/v1/match/random'+ "?username=" + + username,
        success: function (data) {
            let listCnt = data;
            if (listCnt != 0) {
                Swal.fire({
                    icon: 'success',
                    title: '랜덤매칭이 시작되었습니다.\n' +
                        '매칭이 될때까지 대기해 주세요👍\n' +
                        '3명이 대기열에 들어오면 매칭됩니다',
                });
                $("#randomCnt").empty();
                $("#randomCnt").append('현재 대기중인 인원 :' + listCnt);
            } else if (listCnt == 0) {
                alert("좋아요를 취소하시겠습니까?")
                // $("#likeCnt").empty();
                // $("#likeCnt").append(data.count);

            }
        },
        error: function (request, status, error) {
            alert("로그인 후 랜덤매칭이 가능합니다.")
            // alert(request.responseText);
        }
    });
}