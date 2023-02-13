function randomMatch() {

    Swal.fire({
        icon: 'question',
        title: '실시간 매칭',
        html: "원하시는 종목을 입력해 주세요<br>종목 입력 후 실력을 선택해 주세요! <br> 3명이 대기열에 들어온다면 매칭이 성사됩니다.",
        input: 'select',
        inputOptions: {
            '축구':'축구',
            '족구':'족구',
            '농구':'농구',
            '야구':'야구',
            '탁구':'탁구',
            '볼링':'볼링',
            '배드민턴':'배드민턴',
            '테니스':'테니스',
            '골프':'골프',
            '조깅':'조깅',
            '산책':'산책',
            '등산':'등산',
            '자전거':'자전거',
            '수영':'수영',
            '스노클링':'스노클링',
            '스쿠버다이빙':'스쿠버다이빙',
            '수상스키':'수상스키',
            '스키':'스키',
            '스노우보드':'스노우보드',
            '스케이트보드':'스케이트보드',
            '요가':'요가',
            '필라테스':'필라테스',
            '패러글라이딩':'패러글라이딩',
            '바둑':'바둑',
            '장기':'장기',
            '체스':'체스',
        },
        showDenyButton: true,
        confirmButtonText: '선택 완료😁',
        denyButtonText: '매칭 취소😎',
    })
        .then((result) => {
            // console.log(result)

            let sport = result.value;

                if (result.isConfirmed) {
                    if (checkSport(sport)) checkLevel(sport);
                }

                else if (result.isDenied) {
                    Swal.fire({
                        icon: 'success',
                        title: sport,
                        html: "매칭이 취소되었습니다.",
                    })
                }
        });
}

function checkLevel(sport) {
    Swal.fire({
        icon: 'question',
        title: '실력 선택',
        html: "실력이 어떻게 되시나요?<br> 실력을 선택해주세요! <br> 3명이 대기열에 들어온다면 매칭이 성사됩니다.",
        input: 'select',
        inputOptions: {
            '비기너': '비기너😁',
            '중급자': `중급자😉`,
            '고수': '고수😎'
        },
        showDenyButton: true,
        confirmButtonText: '매칭 시작🔥',
        denyButtonText: '매칭 취소😎',
    })
        .then((result) => {
            // console.log(result)
            let level = result.value;
            if (result.isConfirmed) {
                startMatching(level, sport)
            }
            else if (result.isDenied) {
                Swal.fire({
                    icon: 'success',
                    title: sport,
                    html: "매칭이 취소되었습니다.",
                })
            }
        });
}

//실제 매칭을 잡는 로직
function startMatching(level, sport) {
    let username = document.getElementById("myName").innerText;
    // console.log(username);
    // console.log(sport)
    console.log('랜덤매칭 시작')
    $.ajax({
        type: "POST",
        url: '/api/v1/match/live' + "?username=" + username + "&sport=" + sport,
        success: function (data) {
            let listCnt = data;
            if (listCnt > 0) {
                Swal.fire({
                    icon: 'success',
                    title: '실시간 매칭이 시작되었습니다🔥\n\n Level : '+ level,
                    html:  '매칭이 성사될때까지 대기해 주세요👍<br> 3명이 대기열에 들어오면 매칭됩니다',
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

function checkSport(sport) {
    const sports = ['축구', '족구', '농구', '야구', '탁구', '볼링', '배드민턴', '테니스', '골프', '조깅',
        '산책', '등산', '자전거', '수영', '스노클링', '스쿠버다이빙', '수상스키', '스키', '스노우보드',
        '스케이트보드', '요가', '필라테스', '패러글라이딩', '바둑', '장기', '체스']
    // console.log('sport = '+sport);

    for (let i = 0; i < sports.length; i++) {
        if (sports[i] === sport) return true;
        // console.log(sports[i])
    }
    Swal.fire({
        icon: 'success',
        title: '종목 선택 오류',
        html: "종목을 정확히 입력해 주세요 <br> ex) 축구, 탁구, 조깅, 스키",
    })
    return false;
}

//매칭 취소
function randomMatchCancel() {

    let username = document.getElementById("myName").innerText;
    console.log(username);


    $.ajax({
        type: "POST",
        url: '/api/v1/match/live/cancel' + "?username=" + username,
        success: function (data) {
            let listCnt = data;
            if (listCnt > 0) {
                Swal.fire({
                    icon: 'success',
                    title: '실시간 매칭이 취소되었습니다✔\n',
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