<%@ page contentType="text/html; charset=utf-8"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="dto.Product"%>
<%@ page import="dao.ProductRepository"%>
<%
	request.setCharacterEncoding("UTF-8");

	// 해당 웹브라우저마다 하나의 세션의 고유 아이디 정보를, 장바구니 아이디로 재사용 중.
	String cartId = session.getId();

	String shipping_cartId = "";
	String shipping_name = "";
	String shipping_shippingDate = "";
	String shipping_country = "";
	String shipping_zipCode = "";
	String shipping_addressName = "";
	
	// 쿠키에 등록된 데이터를 복수개 가져와서, 쿠키 타입으로 배열 형식으로 받습니다.
	Cookie[] cookies = request.getCookies();

	// 널 체크, 널이 아니면 작업함.
	if (cookies != null) {
		for (int i = 0; i < cookies.length; i++) {
			// 배열에서 각 인덱스 번호 차례로 불러오기.
			Cookie thisCookie = cookies[i];
			
			// 쿠키라는 저장소에서 해당 키의 이름을 가져옴
			String n = thisCookie.getName();
			// 가져온 이름의 값이 조건문에 해당하면, 내보낼 때 전달하기 편하게 하기 위해 URLEncoder로 한부분을 역으로 URLDecoder 작업을 한다.
			if (n.equals("Shipping_cartId"))
				// thisCookie.getValue() 해당 쿠키라는 객체의 값을 가져와서, utf-8 형식으로 담아두게 됨.
				shipping_cartId = URLDecoder.decode((thisCookie.getValue()), "utf-8");
			if (n.equals("Shipping_name"))
				shipping_name = URLDecoder.decode((thisCookie.getValue()), "utf-8");
			if (n.equals("Shipping_shippingDate"))
				shipping_shippingDate = URLDecoder.decode((thisCookie.getValue()), "utf-8");
			if (n.equals("Shipping_country"))
				shipping_country = URLDecoder.decode((thisCookie.getValue()), "utf-8");
			if (n.equals("Shipping_zipCode"))
				shipping_zipCode = URLDecoder.decode((thisCookie.getValue()), "utf-8");
			if (n.equals("Shipping_addressName"))
				shipping_addressName = URLDecoder.decode((thisCookie.getValue()), "utf-8");
		}
	}
%>
<html>
<head>
<link rel="stylesheet" href="./resources/css/bootstrap.min.css" />
<title>주문 정보</title>
</head>
<body>
	<jsp:include page="menu.jsp" />
	<div class="jumbotron">
		<div class="container">
			<h1 class="display-3">주문 정보</h1>
		</div>
	</div>
	<div class="container col-8 alert alert-info">
		<div class="text-center ">
			<h1>영수증</h1>
		</div>
		<div class="row justify-content-between">
			<div class="col-4" align="left">
				<strong>배송 주소</strong> <br> 성명 : <% out.println(shipping_name); %><br> 
				우편번호 : <% 	out.println(shipping_zipCode);%><br> 
				주소 : <%	out.println(shipping_addressName);%>(<%	out.println(shipping_country);%>)<br>
			</div>
			<div class="col-4" align="right">
				<p>	<em>배송일: <% out.println(shipping_shippingDate);	%></em>
			</div>
		</div>
		<div>
			<table class="table table-hover">			
			<tr>
				<th class="text-center">도서</th>
				<th class="text-center">#</th>
				<th class="text-center">가격</th>
				<th class="text-center">소계</th>
			</tr>
			<%
				int sum = 0;
				ArrayList<Product> cartList = (ArrayList<Product>) session.getAttribute("cartlist");
				if (cartList == null)
					cartList = new ArrayList<Product>();
				for (int i = 0; i < cartList.size(); i++) { // 상품리스트 하나씩 출력하기
					Product product = cartList.get(i);
					int total = product.getUnitPrice() * product.getQuantity();
					sum = sum + total;
			%>
			<tr>
				<td class="text-center"><em><%=product.getPname()%> </em></td>
				<td class="text-center"><%=product.getQuantity()%></td>
				<td class="text-center"><%=product.getUnitPrice()%>원</td>
				<td class="text-center"><%=total%>원</td>
			</tr>
			<%
				}
			%>
			<tr>
				<td> </td>
				<td> </td>
				<td class="text-right">	<strong>총액: </strong></td>
				<td class="text-center text-danger"><strong><%=sum%> </strong></td>
			</tr>
			</table>			
				<a href="./ShippingInfo.jsp?cartId=<%=shipping_cartId%>"class="btn btn-secondary" role="button"> 이전 </a>
				<a href="./thankCustomer.jsp"  class="btn btn-success" role="button"> 주문 완료 </a>
				<a href="./checkOutCancelled.jsp" class="btn btn-secondary"	role="button"> 취소 </a>			
		</div>
	</div>	
</body>
</html>
