<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<meta charset="UTF-8">
<title>書籍の追加｜シアトルライブラリ｜シアトルコンサルティング株式会社</title>
<link href="<c:url value="/resources/css/reset.css" />" rel="stylesheet" type="text/css">
<link href="https://fonts.googleapis.com/css?family=Noto+Sans+JP" rel="stylesheet">
<link href="<c:url value="/resources/css/default.css" />" rel="stylesheet" type="text/css">
<link href="https://use.fontawesome.com/releases/v5.6.1/css/all.css" rel="stylesheet">
<link href="<c:url value="/resources/css/home.css" />" rel="stylesheet" type="text/css">
<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<script src="resources/js/thumbnail.js"></script>
</head>
<body class="wrapper">
    <header>
        <div class="left">
            <img class="mark" src="resources/img/logo.png" />       
            <div class="logo">Seattle Library</div>
        </div>
        <div class="right">
            <ul>
                <li><a href="<%=request.getContextPath()%>/home" class="menu">Home</a></li>
                <li><a href="<%=request.getContextPath()%>/">ログアウト</a></li>
            </ul>
        </div>
    </header>
    <main>
        <h1>書籍の一括登録</h1>
        <label class=top_line>CSVファイルをアップロードすることで書籍を一括で登録できます。</label>
        <form action="<%=request.getContextPath()%>/insertCollectiveBooks" method="post" enctype="multipart/form-data" id="data_upload_form">
            <div class="content_body add_book_content">
                <input type="file" name="file">
            </div>
            <c:if test="${!empty errorMsg}">
                <label class="error" style="text-align: center;">${errorMsg}</label>
            </c:if>
            <c:forEach var="bookInfo" items="${fileErrorInfoList}">
                <div class="error" style="text-align: center;">${fileErrorInfoList.rowNum}：{fileErrorInfoList.errorContent}</div>
            </c:forEach>
            <div class="addBookBtn_box">
                <button class=btn_bulkRegist type="submit" name="upload_file">一括登録</button>
            </div>
        </form>
    </main>
</body>
</html>