
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="test.User" %>
<%@ page import="test.Item" %>
<%@ page import="java.util.Calendar" %>
<%
	Item item = (Item) request.getAttribute("item");
	User[] users = (User[]) request.getAttribute("users");
%>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8">
        <title>作業更新</title>
        <link rel="STYLESHEET" href="todo.css" type="text/css">
    </head>
    <body>
        <h1>作業更新</h1>
        <hr>
        <div align="center">
            <table border="0">
                <form action="todo" method="post">
                    <input type="hidden" name="action" value="edit_action">
                    <input type="hidden" name="item_id" value="<%= item.getId() %>">
                    <tr>
                        <th class="add_field">
                            項目名
                        </th>
                        <td class="add_field">
                            <input type="text" name="name" value="<%= item.getName() %>" size="24">
                        </td>
                    </tr>
                    <tr>
                        <th class="add_field">
                            担当者
                        </th>
                        <td class="add_field">
                            <select name="user_id" size="1">
                            <%
                                for(int i = 0; i < users.length; i ++) {
                                    User user = users[i];
                            %>
                                    <option value="<%= user.getId() %>" <%
                                    if(user.getId().equals(item.getUser().getId())) {
                                        %>selected<%
                                    }
                                %>><%= user.getName() %></option>
                            <%
                                }
                            %>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th class="add_field">
                            期限
                        </th>
                        <td class="add_field">
                        <%
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(item.getExpireDate());
                        %>
                            <input type="text" name="year" value="<%= calendar.get(Calendar.YEAR) %>" size="8">/<input type="text" name="month" value="<%= calendar.get(Calendar.MONTH) + 1 %>" size="4">/<input type="text" name="day" value="<%= calendar.get(Calendar.DAY_OF_MONTH) %>" size="4">
                        </td>
                    </tr>
                    <tr>
                        <th class="add_field">
                            完了
                        </th>
                        <td class="add_field">
                            <input type="checkbox" name="finished" value="true" size="8"<%
                                if(item.getFinishedDate() != null) {
                                    %>checked<%
                                }
                            %>>完了した
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2" class="add_button">
                            <table border="0">
                                <tr>
                                    <td>
                                        <input type="submit" value="更新">
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
