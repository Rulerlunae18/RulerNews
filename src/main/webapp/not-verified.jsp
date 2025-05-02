<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Підтвердження пошти</title>
    <link rel="icon" type="image/png" href="favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/confirm.css">
</head>
<body>
<h2>Підтвердження пошти</h2>

<c:if test="${not empty param.error}">
    <p style="color: red;">
        <c:choose>
            <c:when test="${param.error == 'invalid_token'}">Недійсний токен підтвердження.</c:when>
            <c:when test="${param.error == 'token_not_found'}">Токен не знайдено або він недійсний.</c:when>
            <c:when test="${param.error == 'sql'}">Сталася помилка під час обробки запиту. Спробуйте пізніше.</c:when>
            <c:otherwise>Невідома помилка.</c:otherwise>
        </c:choose>
    </p>
</c:if>

<c:if test="${not empty emailMessage}">
    <p style="color: green;">${emailMessage}</p>
</c:if>

<p>Перевірте вашу пошту (можливо і папку "Спам").</p>

<h3>Надіслати лист знову</h3>
<form action="${pageContext.request.contextPath}/resendconfirmation" method="post">
    <label for="email">Введіть email:</label>
    <input type="email" id="email" name="email" required>
    <button type="submit">Надіслати ще раз</button>
</form>
</body>
</html>
