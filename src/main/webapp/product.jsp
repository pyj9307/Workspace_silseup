<%@ page contentType="text/html; charset=utf-8"%>
<%@ page import="dto.Product"%>
<%@ page import="dao.ProductRepository"%>
<%@ page errorPage="exceptionNoProductId.jsp"%>
<!-- sql 작업 위해서 해당 클래스들 전부 임포트 -->
<%@ page import="java.sql.*"%>
<html>
<head>
<link rel="stylesheet" href="./resources/css/bootstrap.min.css" />
<title>상품 상세 정보</title>
<script type="text/javascript">
	function addToCart() {
		if (confirm("상품을 장바구니에 추가하시겠습니까?")) {
			document.addForm.submit();
		} else {		
			document.addForm.reset();
		}
	}
</script>
</head>
<body>
	<jsp:include page="menu.jsp" />
	<div class="jumbotron">
		<div class="container">
			<h1 class="display-3">상품 정보</h1>
		</div>
	</div>
	<%
	// id 상품의 아이디입니다.
		String id = request.getParameter("id");
	
	// 클래스에
		/* ProductRepository dao = ProductRepository.getInstance();
		Product product = dao.getProductById(id); */
	%>
	<!-- 디비에 연결하기 위한 정보를 담아둔 페이지 -->
	<!-- 이 파일은 포함하면서 화면상 약간의 간격이 벌어지거나, 틀어지는 현상이 발생할수도 있음. 
	만약 이상해지면, 이 파일의 위치를 수정할 예정 -->
			<%@ include file="dbconn.jsp" %>
	<div class="container">
		<div class="row">
		
		<%
			// 동적쿼리, 해당 sql 문장을 전달할 떄 이용할 객체
				PreparedStatement pstmt = null;
			// 디비에서 조회된 정보들을 담을 객체.
				ResultSet rs = null;
				
			// 해당 상품의 정보를 가져오기 위한 쿼리 문장.
			// ? 의 위치값은 1부터 시작합니다.
					// ?,?,? : 1,2,3의 순서가 됩니다.
				String sql = "select * from product where p_id = ?";
			// 현재 작업 중.
			// sql 문장을 pstmt 객체에 담기.
				pstmt = conn.prepareStatement(sql);
			// 동적인 문장에 첫번쨰 파라미터에, 해당하는 문자열 값으로 상품의 아이디를 입력 로직.
				pstmt.setString(1, id);
			// 조회를 할 떄, executeQuery() 메소드를 호출
				rs = pstmt.executeQuery();
			// ResultSet rs 이라는 형식의 객체에 테이블 형식으로 값을 저장.
				while (rs.next()) { // 반복문을 통해서 해당 상품의 정보를 불러오면 됩니다.
			%>
			
			<div class="col-md-5">
			<!-- 상세페이지 부분에 사진 출력은 나중에 과제로 제시 예정. 검사 안함. -->
				<%-- <img src="c:/upload/<%=product.getFilename()%>" style="width: 100%" /> --%>
				<img src="./resources/images/<%=rs.getString("p_fileName")%>" style="width: 100%">
				<%-- <img src="C:/JSP_Workspace1/ch18_WebMarket_2/src/main/webapp/resources/images/<%=product.getFilename()%>" style="width: 100%" /> --%>
			</div>
			<div class="col-md-6">
				<h3><%=rs.getString("p_name")%></h3>
				<p><%=rs.getString("p_description")%>
				<p><b>상품 코드 : </b><a href="./product.jsp?id=<%=rs.getString("p_id")%>"class="btn btn-secondary" role="button">상세 정보 &raquo;></a>
				<p><b>제조사</b> : <%=rs.getString("p_manufacturer")%>
				<p><b>분류</b> : <%=rs.getString("p_category")%>
				<p><b>재고 수</b> : <%=rs.getString("p_unitsInStock")%>
				<h4><%=rs.getString("p_unitPrice")%>원</h4>
				<p><form name="addForm" action="./addCart.jsp?id=<%=rs.getString("p_id")%>" method="post">
					<a href="#" class="btn btn-info" onclick="addToCart()"> 상품 주문 &raquo;</a>
					<a href="./cart.jsp" class="btn btn-warning"> 장바구니 &raquo;</a> 
					<a href="./products.jsp" class="btn btn-secondary"> 상품 목록 &raquo;</a>
				</form>
			</div>
			<%
				}
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			%>
		</div>
		<hr>
	</div>
	<jsp:include page="footer.jsp" />
</body>
</html>
