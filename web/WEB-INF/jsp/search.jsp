
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="test.User" %>
<%@ page import="test.Item" %>
<%@ page import="java.util.Calendar" %>
<%
	User currentUser = (User) request.getSession().getAttribute("currentUser");
%>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8">
        <title>検索結果</title>
        <link rel="STYLESHEET" href="todo.css" type="text/css">
    </head>
    <body>
        <h1>検索結果</h1>
        <hr>
        <table border="0">
            <tr>
                <form action="todo" method="post">
                    <input type="hidden" name="action" value="list">
                    <td>
                        <input type="submit" value="戻る">
                    </td>
                </form>
            </tr>
        </table>
        <%
           Item[] items = (Item[]) request.getAttribute("items");
           if(items.length == 0) {
               // アイテムが存在しない場合
        %>
        <div align="center">該当する項目はありません。</div>
        <%
           }else{
        %>
        <table border="0" width="90%" class="list">
            <tr>
                <th>
                    項目名
                </th>
                <th>
                    担当者
                </th>
                <th>
                    期限
                </th>
                <th>
                    完了
                </th>
                <th colspan="3">
                    操作
                </th>
            </tr>
            <%
                    // 現在時刻を取得(期限比較用: 分以降は0にリセット)
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    long currentTime = calendar.getTimeInMillis();
                    
                    // アイテムを出力
                    for(int i = 0; i < items.length; i ++) {
                        Item item = items[i];
                        String styleAttr = "";	
                        if(item.getFinishedDate() != null) {
                            // 完了
                            styleAttr = " style=\"background-color: #cccccc;\"";
                        }else if(item.getUser().getId().equals(currentUser.getId())) {
                            // 自分の作業
                            styleAttr = " style=\"background-color: #ffbbbb;\"";
                        }
						if(item.getExpireDate().getTime() < currentTime && item.getFinishedDate() == null){
							// 期限切れかつ未完了
                            styleAttr += " style=\"color: #ff0000;\"";
                        }
            %>
            <tr>
                <td <%= styleAttr %>>
                    <%= item.getName() %>
                </td>
                <td <%= styleAttr %>>
                    <%= item.getUser().getName() %>
                </td>
                <td <%= styleAttr %>>
                    <%= item.getExpireDate() %>
                </td>
                <td <%= styleAttr %>><%
                        if(item.getFinishedDate() != null) {
                   	        %><%= item.getFinishedDate() %><%
                        }else{
                            %>未<%
                        }
                %></td>
                <form action="todo" method="post">
        	        <input type="hidden" name="action" value="finish">
                    <input type="hidden" name="item_id" value="<%= item.getId() %>">
                    <input type="hidden" name="keyword" value="<%= new String(request.getParameter("keyword").getBytes("iso-8859-1"), "Shift_JIS") %>">
                    <td <%= styleAttr %> align="center">
                        <%
                            if(item.getFinishedDate() != null) {
                        %>
                        <input type="submit" value="未完了">
                        <%
                            }else{
                        %>
                        <input type="submit" value="完了">
                        <%
                            }
                        %>
                    </td>
                </form>
                <form action="todo" method="post">
        	        <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="item_id" value="<%= item.getId() %>">
                    <td <%= styleAttr %> align="center">
                        <input type="submit" value="更新">
                    </td>
                </form>
                <form action="todo" method="post">
        	        <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="item_id" value="<%= item.getId() %>">
                    <td <%= styleAttr %> align="center">
                        <input type="submit" value="削除">
                    </td>
                </form>
            </tr>
            <%
                    }
            %>
        </table>
        <%
                }
        %>
        <table border="0">
            <tr>
                <form action="todo" method="post">
                    <input type="hidden" name="action" value="list">
                    <td>
                        <input type="submit" value="戻る">
                    </td>
                </form>
            </tr>
        </table>
    </body>
</html>