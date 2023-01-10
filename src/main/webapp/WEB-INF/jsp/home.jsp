<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>진짜서울</title>
    <script async src="${pageContext.request.contextPath}/webjars/jquery/3.3.1/jquery.min.js"></script>
</head>

<body>
<header>
    <a href="/map-finder">지도찾기</a>
    <a href="/login-page">로그인</a>
</header>
<section>
    <div>
        <input type="text" name="keyword" placeholder="지도를 검색해보세요">
        <button type="submit" onclick="searchMapsByKeyword()">검색</button>
    </div>
    <div>
        <h3>열혈 큐레이터</h3>
        <h5>최근 1주일간 활발히 활동한 큐레이터를 소개합니다.</h5>
    </div>
    <div>
        <h3>요즘 뜨는 장소들</h3>
        <h5>진짜서울에서 사랑받는 장소들을 소개합니다.</h5>
    </div>
    <div>
        <h3>큐레이션 지도</h3>
        <h5>큐레이션 지도는 마음껏 장소를 추천할 수 있는 지도입니다. (장소 추천 횟수 제한이 없습니다)</h5>
    </div>
    <div>
        <h3>추천 테마지도</h3>
        <h5>특징이 명확하고 개성있는 테마들을 추천합니다.</h5>
    </div>
    <div>
        <h3>최근 만들어진 테마지도</h3>
        <h5>취향 가득한 큐레이터를 기다리고 있는 테마들입니다.</h5>
    </div>
    <div>
        <h3>인기있는 테마지도</h3>
    </div>
</section>
</body>

<script type="text/javascript">
    function searchMapsByKeyword() {
        let data = {
            keyword: $("#keyword").val(),
            place: null,
            somebody: null,
            something: null,
            characteristics: null,
            food: null,
            beverage: null,
            categories: null
        };

        $.ajax({
            type: "POST",
            url: "/api/maps/search",
            async: true,
            contentType: "application/json; charset=UTF-8",
            dataType: "text",
            data: JSON.stringify(data),
            success: function (response) {
                response = JSON.parse(response);
                let success = response.success;

                if (success) {
                    alert(JSON.stringify(response));
                    location.href = "/map-finder";
                } else {
                    alert("조회에 실패하였습니다.");
                    location.href = "redirect:/";
                }
            }
        });
    }
</script>