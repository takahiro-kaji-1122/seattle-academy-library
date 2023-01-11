<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<%@ page contentType="text/html; charset=utf8"%>
<%@ page import="java.util.*"%>
<html>
<head>
<title>ホーム｜シアトルライブラリ｜シアトルコンサルティング株式会社</title>
<link href="<c:url value="/resources/css/reset.css" />" rel="stylesheet" type="text/css">
<link href="https://fonts.googleapis.com/css?family=Noto+Sans+JP" rel="stylesheet">
<link href="<c:url value="/resources/css/default.css" />" rel="stylesheet" type="text/css">
<link href="https://use.fontawesome.com/releases/v5.6.1/css/all.css" rel="stylesheet">
<link href="<c:url value="/resources/css/home.css" />" rel="stylesheet" type="text/css">
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
                <li><a tabindex="-1" style="pointer-events: none">${username}</a><a href="<%=request.getContextPath()%>/logout">ログアウト</a></li>
            </ul>
        </div>
    </header>
    <main>
        <c:if test="${unknownError}">
            <label class="error">時間を置いてからもう一度お試しください。</label>
        </c:if>
        <c:if test="${isSuccessDelete}">
            <label class="success">書籍を削除しました</label>
        </c:if>
        <h1>Home</h1>
        <a href="<%=request.getContextPath()%>/addBook" class="btn_add_book">書籍の追加</a> <a href="<%=request.getContextPath()%>/addCollectiveBooks" class="btn_bulk_book">書籍の追加</a><a href="<%=request.getContextPath()%>/rentalHistory" class="btn_history_book">書籍の閲覧履歴</a>
        <form method="get" action="searchBook" class="search_book">
            <input type="text" class="search_box" id="searchedBookName" name="searchedBookName" required> <input type="submit" class="search1" value="検索">
        </form>
        <div class="content_body">
            <c:if test="${!empty resultMessage}">
                <div class="error_msg">${resultMessage}</div>
            </c:if>
            <div>
                <div class="booklist">
                    <c:if test="${unknownError}">
                        <label class="error">書籍を表示できません。時間を置いてからもう一度お試しください。</label>
                    </c:if>
                    <c:if test="${notExistBookData}">
                        <label class="error">書籍が登録されていません</label>
                    </c:if>
                    <c:forEach var="bookInfo" items="${bookList}">
                        <div class="books">
                            <form method="get" class="book_thumnail" action="<%=request.getContextPath()%>/details">
                                <a href="javascript:void(0)" onclick="this.parentNode.submit();"> <c:if test="${empty bookInfo.thumbnail}">
                                        <img class="book_noimg" src="resources/img/noImg.png">
                                    </c:if> <c:if test="${!empty bookInfo.thumbnail}">
                                        <img class="book_noimg" src="${bookInfo.thumbnail}">
                                    </c:if>
                                </a><input type="hidden" name="bookId" value="${bookInfo.bookId}">
                            </form>
                            <ul>
                                <li class="book_title">${bookInfo.title}</li>
                                <li class="book_author">${bookInfo.author}</li>
                                <li class="book_publisher">${bookInfo.publisher}</li>
                                <li class="book_publish_date">${bookInfo.publishDate}</li>
                            </ul>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </main>
</body>
</html>
