<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>로그인 페이지 - 진짜서울</title>
    <script async src="${pageContext.request.contextPath}/webjars/jquery/3.3.1/jquery.min.js"></script>
</head>

<body>
<a href="/sign-up">회원가입</a>
<a href="/sign-in">로그인</a><br><br>
<a href="/oauth2/authorization/google">구글 로그인</a><br>
<a href="/oauth2/authorization/kakao">카카오 로그인</a><br>
<a href="/oauth2/authorization/naver">네이버 로그인</a>
</body>