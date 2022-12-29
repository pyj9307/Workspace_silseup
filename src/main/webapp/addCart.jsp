<%@ page contentType="text/html; charset=utf-8"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="dto.Product"%>
<%@ page import="dao.ProductRepository"%>
<%
// 해당 프로젝트의 기본 구성.
// 상품의 목록 화면은 디비 연동, 상품의 상세페이지는 ProductRepository 클래스에 기본값으로 입력.
// 샘플 상품. 핸드폰, 탭, 그램. 예제로 다른 상품을 추가했습니다.


	String id = request.getParameter("id");
// id : 상품의 아이디
//
	if (id == null || id.trim().equals("")) {
		response.sendRedirect("products.jsp");
		return;
	}

	// ProductRepository 형으로 객체를 호출했음.
	// 보통은 해당 객체를 new 연산잘르 통해서 기본 생성자 또는 매개변수가 있는 생성자 호출해서 객체 만들지만,
	// 이 방식은 싱글톤 패턴 방식으로 객체를 생성했습니다.
	ProductRepository dao = ProductRepository.getInstance();
	// dao 객체 (수납도구) -> 구성품은 뭐가 있지?
	// 컬렉션, Product 형의 객체를 여러 개 가지는 컬렉션.
	// getInstance() 해당 자기 객체를 호출(생성)
	// 생플 상품이 4개나 등록이 되어있는 상태.
	// 컬렉션을 호출하거나, 상품을 추가하는 기능, 하나의 상품아이디를 찾는 기능.
	
	// dao 객체에서 해당 id에 해당하는 상품을 찾아서 다시 재할당
	Product product = dao.getProductById(id);
	if (product == null) {
		// 상품이 없다면, 없다는 예외 페이지로 강제 이동
		response.sendRedirect("exceptionNoProductId.jsp");
	}

	// 해당 상품이 null이 아닌 경우
	
	// dao 객체에 이미 들어있는 상품의 모든 정보를 불러와서 다시 컬렉션에 재할당하는 과정.
	ArrayList<Product> goodsList = dao.getAllProducts();
	
	// Product 형으로 객체를 하나 생성합니다. goods
	Product goods = new Product();
	
	// goodslist는 dao 객체에 이미 등록된 샘플 상품 4개와 동일.
	for (int i = 0; i < goodsList.size(); i++) {
		// 해당 컬렉션에 각 원소의 값을 하나씩 꺼내겠다.
		// 꺼내고 나서 봤더니, Product형.
		goods = goodsList.get(i);
		
		// goods : 이미 등록된 샘플 상품 4개를 장바구니에 
		// 추가할려는 상품을 아이디 :id
		// 2개가 일치한다면 나가고
		// 아니면 계속 반복
		if (goods.getProductId().equals(id)) { 			
			break;
		}
	}
	
	// 해당 세션에 cartlist 이름으로 세션의 값을 불러 오려고 하는데
	// 만약 널 체크를 해서 해당 이름으로 정보가 없다면, 만들겠다.
	
	ArrayList<Product> list = (ArrayList<Product>) session.getAttribute("cartlist");
	if (list == null) { 
		list = new ArrayList<Product>();
		session.setAttribute("cartlist", list);
	}

	// 수량을 체크 하기위한 변수.
	int cnt = 0;
	
	// goodsQnt 상품을 임시로 저장하기 위한 객체(DTO처럼 사용 중.)
	Product goodsQnt = new Product();
	
	// list : 장바구니 컬렉션
	for (int i = 0; i < list.size(); i++) {
		// 컬렉션 각 요소가 형이 Product 이므로 받을 때도 같은 형으로 받자.
		goodsQnt = list.get(i);
		
		// 장바구니에 등록된 상품의 아이디를 꺼내서, id와 비교해 동일한 제품이라면,
		// cnt라는 변수에 수량을 증가시켜주고 있습니다.
		if (goodsQnt.getProductId().equals(id)) {
			cnt++;
			// 해당 상품의 객체(Product형의 goodsQnt라는 객체의 수량을 증가)
			int orderQuantity = goodsQnt.getQuantity() + 1;
			// orderQuantity 라는 int 형 변수를 해당 객체에 set 메서드롳 추가합니다.
			goodsQnt.setQuantity(orderQuantity);
		}
	}

	if (cnt == 0) { 
		// 만약에 수량이 0이면, set 메서드를 통해서 해당 상품의 수량을 1개로 하고.
		// 해당 장바구니 컬렉션 list에 객체를 추가합니다.
		goods.setQuantity(1);
		list.add(goods);
	}

	// 장바구니를 추가하고 나서, 해당 상품의 상세페이지로 강제 이동합니다.
	// id -> 상품의 아이디입니다. 유효성 체크로 인해서 P~~~~ 형식으로 되어 있음.
	response.sendRedirect("product.jsp?id=" + id);
%>