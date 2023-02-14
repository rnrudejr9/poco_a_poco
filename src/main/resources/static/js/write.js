

async function chatRoomMake(crewId) {
    let response = await fetch("/api/v1/room", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify({
            crewId: crewId,
            name: document.getElementById("title").value
        })
    })
    console.log(response);
    if (response.ok) {
        let json = await response.json()
        console.log(json);
        alert("채팅등록");
    } else {
        let json = await response.text();
        console.log(json);
        alert("실패");
    }
}

async function crewMake() {
    let choose = document.querySelector('input[type=radio][name=sportradio]:checked').value;

    if (choose === null) {
        console.log("error not choose");
        return;
    }
    console.log("start");
    let response = await fetch("/api/v1/crews", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify({
            strict: document.getElementById("address_detail").value,
            crewLimit: document.getElementById("myRange").value,
            content: document.getElementById("content").value,
            title: document.getElementById("title").value,
            datepick: document.getElementById("datepicker").value,
            timepick: document.getElementById("timepicker").value,
            imagePath: document.getElementById("imagePath").value,
            chooseSport: choose
        })

    })
    console.log(response);
    if (response.ok) {
        let json = await response.json()
        console.log(json);
        alert("모임등록성공!");
        let crewId = json.result.crewId;
        // chatRoomMake(crewId);
        joinCrewAwait(crewId);
        location.href = "/view/v1/crews";
    } else {
        let json = await response.text();
        console.log(json);
    }
}

async function joinCrewAwait(crewId) {
    var crewId = crewId;
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
    if (response.ok) {
        var json = await response.json();
        alert(json.result);
        console.log(json);
    } else {
        var json = await response.json();
        console.log(json);
    }
}

function profileUpload() {

    let uuidString = Math.random().toString(20).substr(2,8);

    const ACCESS_KEY = 'AKIA44KXB4KHWH22K4HD';
    const SECRET_ACCESS_KEY = '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';
    const REGION = 'ap-northeast-2';
    const BUCKET_NAME = 'poco-bucket-2';
    const BUCKET_DIRECTORY = '/crewimages';
    const S3_BUCKET = BUCKET_NAME + BUCKET_DIRECTORY;

    // AWS ACCESS KEY를 세팅합니다.
    AWS.config.update({
        accessKeyId: ACCESS_KEY,
        secretAccessKey: SECRET_ACCESS_KEY
    });

    // 버킷에 맞는 이름과 리전을 설정합니다.
    const myBucket = new AWS.S3({
        params: { Bucket: S3_BUCKET},
        region: REGION,
    });



    let files = document.getElementById('imageFile').files;
    let file = files[0];

    let fileName = uuidString + '-' + file.name.replace(' ', '');


    const params = {
        ACL: 'public-read',
        Body: file,
        Bucket: S3_BUCKET,
        Key: fileName
    };


    myBucket.putObject(params)
        .on('httpUploadProgress', (evt) => {
            alert("SUCCESS");
            console.log(fileName);
            $('#imagePath').val(fileName);
        })
        .send((err) => {
            if (err) console.log(err)
        })

}