<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<meta charset="UTF-8">
<title>書籍の貸出履歴｜シアトルライブラリ｜シアトルコンサルティング株式会社</title>
<link href="<c:url value="/resources/bootstrap-5.0.2-dist/css/bootstrap.min.css" />" rel="stylesheet" type="text/css">
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
        <div class="container-fluid">
            <div class="row">
                <div class="col-md-3"></div>
                <div class="col-md-6">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th scope="col">書籍名</th>
                                <th scope="col">貸出日</th>
                                <th scope="col">返却日</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="latestBooksStatus" items="${latestBooksStatusList}">
                                <c:if test="${latestBooksStatus.ableLend}">
                                    <tr>
                                        <th scope="row"><a href="<%=request.getContextPath()%>/details?bookId=${latestBooksStatus.bookId}">${latestBooksStatus.title}</a></th>
                                        <td></td>
                                        <td>${latestBooksStatus.updDate}</td>
                                    </tr>
                                </c:if>
                                <c:if test="${!latestBooksStatus.ableLend}">
                                    <tr>
                                        <th scope="row"><a href="<%=request.getContextPath()%>/details?bookId=${latestBooksStatus.bookId}">${latestBooksStatus.title}</a></th>
                                        <td>${latestBooksStatus.updDate}</td>
                                        <td></td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="col-md-3"></div>
            </div>
        </div>
    </main>
</body>
</html>