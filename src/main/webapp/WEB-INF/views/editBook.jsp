<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<meta charset="UTF-8">
<title>書籍の詳細｜シアトルライブラリ｜シアトルコンサルティング株式会社</title>
<link href="<c:url value="/resources/css/reset.css" />" rel="stylesheet" type="text/css">
<link href="https://fonts.googleapis.com/css?family=Noto+Sans+JP" rel="stylesheet">
<link href="<c:url value="/resources/css/default.css" />" rel="stylesheet" type="text/css">
<link href="https://use.fontawesome.com/releases/v5.6.1/css/all.css" rel="stylesheet">
<link href="<c:url value="/resources/css/home.css" />" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="resources/css/lightbox.css">
<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<script src="resources/js/lightbox.js" /></script>
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
        <form action="<%=request.getContextPath()%>/editBook" method="post" enctype="multipart/form-data" id="data_upload_form">
            <input type="hidden" name="bookId" value="${editBookDetailsInfo.bookId}">
            <h1>書籍編集</h1>
            <div class="content_body detail_book_content">
                <div class="content_left">
                    <span>書籍の画像</span>
                    <div class="book_thumnail">
                        <a href="${bookDetailsInfo.thumbnailUrl}" data-lightbox="image-1"> <c:if test="${empty bookDetailsInfo.thumbnailUrl}">
                                <img class="book_noimg" src="resources/img/noImg.png">
                            </c:if> <c:if test="${!empty bookDetailsInfo.thumbnailUrl}">
                                <img class="book_noimg" src="${bookDetailsInfo.thumbnailUrl}">
                            </c:if> <input type="hidden" name="bookId" value="${bookDetailsInfo.bookId}">
                        </a>
                    </div>
                </div>
                <div class="content_right">
                    <c:if test="${requiredItemCheckNG}">
                        <label class="error">必須項目は全て入力してください</label>
                    </c:if>
                    <c:if test="${publishDateCheckNG}">
                        <label class="error">出版日はYYYYMMDD形式入力してください</label>
                    </c:if>
                    <c:if test="${isbmCheckNG}">
                        <label class="error">ISBNは半角数字で10桁もしくは13桁で入力してください</label>
                    </c:if>
                    <div>
                        <span>書籍名</span><span class="care care2">必須</span> <input type="text" name="title" value="${editBookDetailsInfo.title}">
                    </div>
                    <div>
                        <span>著者名</span><span class="care care2">必須</span> <input type="text" name="author" value="${editBookDetailsInfo.author}">
                    </div>
                    <div>
                        <span>出版社</span><span class="care care2">必須</span> <input type="text" name="publisher" value="${editBookDetailsInfo.publisher}">
                    </div>
                    <div>
                        <span>出版日</span><span class="care care2">必須</span> <input type="text" name="publishDate" value="${editBookDetailsInfo.publishDate}">
                    </div>
                    <div>
                        <span>説明</span><span class="care care1">任意</span> <input type="text" name="description" value="${editBookDetailsInfo.description}">
                    </div>
                    <div>
                        <span>ISBN</span><span class="care care1">任意</span> <input type="text" name="isbn" value="${editBookDetailsInfo.isbn}">
                    </div>
                </div>
            </div>
            <div class="addBookBtn_box">
                <button type="submit" id="add-btn" class="btn_addBook">登録</button>
            </div>
        </form>
    </main>
</body>
</html>
