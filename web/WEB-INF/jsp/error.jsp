
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<html>
    <head>
      <meta http-equiv="content-type" content="text/html; charset=utf-8">
      <title>エラー</title>
      <link rel="STYLESHEET" href="todo.css" type="text/css">
    </head>
    <body>
        <h1>エラー</h1>
        <hr>
        <div align="center">
            <table border="0">
                <form action="todo" method="get">
                	<input type="hidden" name="action" value="login">
                    <tr>
                        <td class="add_field">
                            エラーが発生しました。<br>
                            内容: <%= request.getAttribute("message") %>
                        </td>
	            </tr>
                    <tr>
                        <td class="add_button">
                            <input type="submit" value="戻る">
                        </td>
                    </tr>
                </form>
            </table>
         </div>
     </body>
</html>
