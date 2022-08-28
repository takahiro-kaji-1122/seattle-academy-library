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
        <form action="<%=request.getContextPath()%>/insertBook" method="post" enctype="multipart/form-data" id="data_upload_form">
            <h1>書籍の追加</h1>
            <div class="content_body add_book_content">
                <c:if test="${unknownError}">
                    <label class="error">時間を置いてからもう一度お試しください。</label>
                </c:if>
                <div>
                    <span>書籍の画像</span> <span class="care care1">任意</span>
                    <div class="book_thumnail">
                        <img class="book_noimg" src="resources/img/noImg.png">
                    </div>
                    <input type="file" accept="image/*" name="thumbnail" id="thumbnail">
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
                        <span>書籍名</span><span class="care care2">必須</span>
                        <c:if test="${!empty newBookDetailsInfo}">
                            <input type="text" name="title" value="${newBookDetailsInfo.title}">
                        </c:if>
                        <c:if test="${empty newBookDetailsInfo}">
                            <input type="text" name="title" autocomplete="off">
                        </c:if>
                    </div>
                    <div>
                        <span>著者名</span><span class="care care2">必須</span>
                        <c:if test="${!empty newBookDetailsInfo}">
                            <input type="text" name="author" value="${newBookDetailsInfo.author}">
                        </c:if>
                        <c:if test="${empty newBookDetailsInfo}">
                            <input type="text" name="author" autocomplete="off">
                        </c:if>
                    </div>
                    <div>
                        <span>出版社</span><span class="care care2">必須</span>
                        <c:if test="${!empty newBookDetailsInfo}">
                            <input type="text" name="publisher" value="${newBookDetailsInfo.publisher}">
                        </c:if>
                        <c:if test="${empty newBookDetailsInfo}">
                            <input type="text" name="publisher">
                        </c:if>
                    </div>
                    <div>
                        <span>出版日</span><span class="care care2">必須</span>
                        <c:if test="${!empty newBookDetailsInfo}">
                            <input type="text" name="publishDate" value="${newBookDetailsInfo.publishDate}">
                        </c:if>
                        <c:if test="${empty newBookDetailsInfo}">
                            <input type="text" name="publishDate">
                        </c:if>
                    </div>
                    <div>
                        <span>説明</span><span class="care care1">任意</span>
                        <c:if test="${!empty newBookDetailsInfo}">
                            <input type="text" name="description" value="${newBookDetailsInfo.description}">
                        </c:if>
                        <c:if test="${empty newBookDetailsInfo}">
                            <input type="text" name="description">
                        </c:if>
                    </div>
                    <div>
                        <span>ISBN</span><span class="care care1">任意</span>
                        <c:if test="${!empty newBookDetailsInfo}">
                            <input type="text" name="isbn" value="${newBookDetailsInfo.isbn}">
                        </c:if>
                        <c:if test="${empty newBookDetailsInfo}">
                            <input type="text" name="isbn">
                        </c:if>
                    </div>
                    <input type="hidden" id="bookId" name="bookId" value="${newBookDetailsInfo.bookId}">
                </div>
            </div>
            <div class="addBookBtn_box">
                <button type="submit" id="add-btn" class="btn_addBook">登録</button>
            </div>
        </form>
    </main>
</body>
</html>