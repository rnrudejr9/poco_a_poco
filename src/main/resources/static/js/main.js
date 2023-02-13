

function changeID(checkID) {
    document.getElementById("join").value = checkID;
}

async function findPart(crewId) {
    var crewId = crewId;
    let response = await fetch("/api/v1/part/find/" + crewId, {
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
        var str = "<b>" + json.result.now + "</b>";
        document.getElementById(crewId).innerHTML = str;
    } else {
        var json = await response.json();
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

async function profileGet(imgKey, imgId) {

    // let uuidString = Math.random().toString(20).substr(2,8);

    const ACCESS_KEY = document.getElementById("awsAccessKey").value;
    const SECRET_ACCESS_KEY = document.getElementById("awsSecretAccessKey").value;
    const REGION = document.getElementById("awsRegion").value;
    const BUCKET_NAME = document.getElementById("awsBucketName").value;
    const BUCKET_DIRECTORY = document.getElementById("awsBucketDirectory").value;
    const S3_BUCKET = BUCKET_NAME + BUCKET_DIRECTORY;


    // AWS ACCESS KEY를 세팅합니다.

    AWS.config.update({
        accessKeyId: ACCESS_KEY,
        secretAccessKey: SECRET_ACCESS_KEY
    });

    var s3 = new AWS.S3({
        region: REGION
    });

    // var imgKey = $('input[name=img-key]').val();

    if (!imgKey) {
        imgKey = '4f9e2be2-7e04-4aa8-bde0-ac2c4df64fcd-blank-profile-picture-g7d902ce67_1280.png'
    }

    // 'e44091b4-4c0a-439c-98b1-086773f65f85-lighthouse.png'


    const params = {
        Bucket: S3_BUCKET,
        Key: imgKey
    };

    var url = s3.getSignedUrl(
        "getObject", params
    );

    // const img1 = document.getElementById('left-image');
    // img1.src = url;

    console.log("imageId:{ }", imgId);

    document.getElementById(imgId).src = url;

}
