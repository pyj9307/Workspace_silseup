<%@ page contentType="text/html; charset=utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%
// 해당 request 객체에 들어온 정보를 UTF-8로 설정
	request.setCharacterEncoding("UTF-8");
// 해당 로그인 창에서 입력된 정보를 불러와 재할당.
	String id = request.getParameter("id");
	String password = request.getParameter("password");
%>
<!-- JSTL에서 사용하는 사용자 정의 태그 -->
<sql:setDataSource var="dataSource"
	url="jdbc:mysql://localhost:3306/WebMarketDB"
	driver="com.mysql.jdbc.Driver" user="root" password="1234" />

<sql:query dataSource="${dataSource}" var="resultSet">
   SELECT * FROM member WHERE ID=? and password=?  
   <sql:param value="<%=id%>" />
	<sql:param value="<%=password%>" />
</sql:query>

<c:forEach var="row" items="${resultSet.rows}">
	<%
	// session 객체에 로그인한 정보의 아이디를 등록하는 알고리즘.
		session.setAttribute("sessionId", id);
	%>
	<c:redirect url="resultMember.jsp?msg=2" />
</c:forEach>

<c:redirect url="loginMember.jsp?error=1" />
