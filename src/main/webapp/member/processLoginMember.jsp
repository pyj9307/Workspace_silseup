<%@ page contentType="text/html; charset=utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%
// 모델 1으로 구성되어있다.
// 모델과 컨트롤러 구성이 같이 되어있다.
// 해당 request 객체에 들어온 정보를 UTF-8로 설정
	request.setCharacterEncoding("UTF-8");
// 해당 로그인 창에서 입력된 정보를 불러와 재할당.
	String id = request.getParameter("id");
	String password = request.getParameter("password");
%>
<!-- JSTL에서 사용하는 사용자 정의 태그 -> 모델 -->
<sql:setDataSource var="dataSource"
	url="jdbc:mysql://localhost:3306/WebMarketDB"
	driver="com.mysql.jdbc.Driver" user="root" password="1234" />

<sql:query dataSource="${dataSource}" var="resultSet">
   SELECT * FROM member WHERE ID=? and password=?  
   <sql:param value="<%=id%>" />
	<sql:param value="<%=password%>" />
</sql:query>
<!-- 나중에는 디비를 처리하는 역할 부분을 드러내서 디비만 처리하는 로지긍로 합닏.
지금은, 모델 1을 예제로 사용 중이라서, 같이 있습니다. -->
<!-- JSTL에서 사용하는 사용자 정의 태그 -->

<%-- 컨트롤러의 역할이 들어가 있다.
원래 처음에는 스크립틀릿 태그를 통해서 자바의 조건문, 반복문이 자바의 로직을 처리했음.
-? 자바의 문법이 길다보니, 가독성이 떨어져서 이걸 간단히 표현할 방법이 없나? JSTL(사용자 정의 태그)
<c:> 이 태그를 통해서 표현을 하게 됨 C(Controller) --%>

<c:forEach var="row" items="${resultSet.rows}">
	<%
	// session 객체에 로그인한 정보의 아이디를 등록하는 알고리즘.
		session.setAttribute("sessionId", id);
	%>
	<c:redirect url="resultMember.jsp?msg=2" />
</c:forEach>

<c:redirect url="loginMember.jsp?error=1" />
