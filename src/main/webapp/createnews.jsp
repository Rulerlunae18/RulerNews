<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Створити новину</title>
    <link rel="icon" type="image/png" href="favicon.png">
    <link rel="stylesheet" href="css/main1.css">
</head>
<body>

<div class="header">
    <div class="logo">
        <a href="home">📏 RULER NEWS</a>
        <div class="logo-subtext"><i>Додайте щось потужне💥</i></div>
    </div>
</div>

<div class="layout-with-sides">
    <div class="side-banner">
        <h4>💡 Підказки:</h4>
        <ul>
            <li><code>&lt;b&gt;текст&lt;/b&gt;</code>  <b>жирний</b></li>
            <li><code>&lt;i&gt;текст&lt;/i&gt;</code>  <i>курсив</i></li>
            <li><code>&lt;u&gt;текст&lt;/u&gt;</code>  <u>підкреслення</u></li>
            <li><code>&lt;h3&gt;Заголовок&lt;/h3&gt;</code></li>
            <li><code>&lt;br&gt;</code>  новий рядок</li>
            <li><code>&lt;img src=""&gt;</code>  зображення</li>
        </ul>
    </div>

    <div class="main">
        <h2>📝 Створення новини</h2>
        <form action="${pageContext.request.contextPath}/createnews" method="post" enctype="multipart/form-data">
            <label>Заголовок:</label><br/>
            <input type="text" name="title" required><br/><br/>

            <label>Контент:</label><br/>
            <textarea name="content" rows="6" cols="60" required></textarea><br/><br/>

            <label>Категорія:</label><br/>
            <select name="category" required>
                <option value="Україна">Україна</option>
                <option value="Спорт">Спорт</option>
                <option value="Ігри">Ігри</option>
                <option value="Політика">Політика</option>
                <option value="Кулінарія">Кулінарія</option>
                <option value="Мистецтво">Мистецтво</option>
            </select><br/><br/>

            <label>Зображення:</label><br/>
            <input type="file" name="image"><br/><br/>

            <button type="submit">✅ Опублікувати новину</button>
        </form>
    </div>

    <div class="side-banner">
        <h4>Оформи свій текст</h4>
        <p class="subtitle">Зроби читання приємним і зрозумілим!</p>
    </div>

</div>

</body>
</html>
