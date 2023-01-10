<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>로그인 - 진짜서울</title>
    <script async src="${pageContext.request.contextPath}/webjars/jquery/3.3.1/jquery.min.js"></script>
</head>

<body>
<div class="wrapper">
    <div class="title"><h1 style="font-size: 21px;">로그인</h1></div>
    <div class="email">
        <input id="email" type="text" placeholder="이메일을 입력해 주세요.">
        <div id="emailError" class="error"></div>
    </div>
    <div class="password">
        <input id="password" type="password" placeholder="비밀번호를 입력해 주세요.">
        <div id="passwordError" class="error"></div>
    </div>
    <div class="line">
        <hr>
    </div>
    <div class="signUp">
        <button id="btn-login" onclick="signIn()">로그인</button>
    </div>
</div>
</body>

<script type="text/javascript">
    function signIn() {
        let data = {
            email: $("#email").val(),
            password: $("#password").val()
        };

        $.ajax({
            type: "POST",
            url: "/api/auth/login",
            async: true,
            contentType: "application/json; charset=utf-8",
            dataType: "text",
            data: JSON.stringify(data),
            success: function (response) {
                response = JSON.parse(response);
                let success = response.success;

                if (success) {
                    alert("환영합니다!")
                    location.href = "/";
                } else {
                    alert("아이디 또는 비밀번호가 잘못되었습니다.");
                    location.href = "redirect:/sign-in";
                }
            }
        });
    }
</script>