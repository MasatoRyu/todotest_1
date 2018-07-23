
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="test.Item" %>
<%
	Item item = (Item) request.getAttribute("item");
%>
<html>
    <head>
      <meta http-equiv="content-type" content="text/html; charset=utf-8">
      <title>削除確認</title>
      <link rel="STYLESHEET" href="todo.css" type="text/css">
    </head>
    <body>
        <h1>削除確認</h1>
        <hr>
        <div align="center">
            <table border="0">
                <form action="todo" method="post">
                    <input type="hidden" name="action" value="delete_action">
                    <input type="hidden" name="item_id" value="<%= item.getId() %>">
                    <tr>
                        <td class="add_field">
                            項目 <%= item.getName() %> を削除します。<br>
                            よろしいですか？
                        </td>
	            </tr>
                    <tr>
                        <td class="add_button">
                            <table border="0">
                                <tr>
                                    <td>
                                        <input type="submit" value="削除">
                                    </td>
                                    </form>
                                    <form action="todo" method="post">
                                    <input type="hidden" name="action" value="list">
                                    <td>
                                        <input type="submit" value="キャンセル">
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </form>
            </table>
         </div>
     </body>
</html>
