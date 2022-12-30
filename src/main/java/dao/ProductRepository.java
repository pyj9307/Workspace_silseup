package dao;

import java.util.ArrayList;

import dto.Product;

public class ProductRepository {
	// 샘플 상품들을 해당 클래스에서 등록하고 있습니다.
	// listOfProducts : Product 클래스 형 여러 개를 담을 수 있는 컬렉션입니다.
	private ArrayList<Product> listOfProducts = new ArrayList<Product>();
	
	// private static : 외부에서 볼 수 없고, 대신에 static 클래스명으로 접근가능.(getter/setter로 해도 되는데 싱글톤패턴 방식에서 이걸 좋아함)
	// instance : 싱글톤패턴 방식으로 미리 하나를 만들어 둡니다.
	private static ProductRepository instance = new ProductRepository();

	// 호출은 getInstance() 메서드를 호출하면, 리턴의 타입이 ProductRepository 형.
	// 객체를 이렇게 호출하는 방식을 : 싱글톤패턴.
	public static ProductRepository getInstance(){
		return instance;
	} 
	// 상품 목록 부분은 해당 디비 서버에 연결이 되어서, 불러오게 연동이 되었다면,
	// 상품의 상세페이지 부분인데, 해당 클래스 객체에 기본값을 불러오는 부분입니다.
	// 과제, 여러분 상세페이지를 디비에 연결해서 가져오는 연습하는 부분.
	public ProductRepository() {
		// 이렇게 작업해서 보여주는 이유는
		// ProductRepository 클래스를 이용해 데이터를 입력한다.
		// DTO -> 데이터를 전달하기 위한 객체 형식.
		Product phone = new Product("P1234", "iPhone 6s", 800000);
		phone.setDescription("4.7-inch, 1334X750 Renina HD display, 8-megapixel iSight Camera");
		phone.setCategory("Smart Phone");
		phone.setManufacturer("Apple");
		phone.setUnitsInStock(1000);
		phone.setCondition("New");
		phone.setFilename("P1234.png");

		Product notebook = new Product("P1235", "LG PC �׷�", 1500000);
		notebook.setDescription("13.3-inch, IPS LED display, 5rd Generation Intel Core processors");
		notebook.setCategory("Notebook");
		notebook.setManufacturer("LG");
		notebook.setUnitsInStock(1000);
		notebook.setCondition("Refurbished");
		notebook.setFilename("P1235.png");

		Product tablet = new Product("P1236", "Galaxy Tab S", 900000);
		tablet.setDescription("212.8*125.6*6.6mm,  Super AMOLED display, Octa-Core processor");
		tablet.setCategory("Tablet");
		tablet.setManufacturer("Samsung");
		tablet.setUnitsInStock(1000);
		tablet.setCondition("Old");
		tablet.setFilename("P1236.png");
		
		// 상품등록 부분 연습하기. 이미지 파일 위치는 c:/upload
		// 상품의 예는 디비에 있는 내용을 기반으로 등록함.
		// P123456/ test/ 1000원/ test : 설명/ test : 카테고리
		// 수량 : 11 / 상품상태 : new / 이미지파일이름 : 라바1.jpg
		Product testlarva = new Product("P123456", "test", 1000);
		testlarva.setDescription("test");
		testlarva.setCategory("test");
		testlarva.setManufacturer("test");
		testlarva.setUnitsInStock(11);
		testlarva.setCondition("New");
		testlarva.setFilename("라바1.jpg");

		Product testlarva2 = new Product("P123123", "test2", 900000);
		testlarva2.setDescription("test");
		testlarva2.setCategory("test");
		testlarva2.setManufacturer("test");
		testlarva2.setUnitsInStock(11);
		testlarva2.setCondition("New");
		testlarva2.setFilename("라바1.jpg");
		
		listOfProducts.add(phone);
		listOfProducts.add(notebook);
		listOfProducts.add(tablet);
		listOfProducts.add(testlarva);
		listOfProducts.add(testlarva2);
	}

	public ArrayList<Product> getAllProducts() {
		return listOfProducts;
	}
	
	// 해당 상품의 아이디와 일치하는지를 컬렉션 내부에서 반복자 패턴으로 
	// 찾아서, 해당 아이디를 반환하는 메서드.
	public Product getProductById(String productId) {
		Product productById = null;

		for (int i = 0; i < listOfProducts.size(); i++) {
			Product product = listOfProducts.get(i);
			if (product != null && product.getProductId() != null && product.getProductId().equals(productId)) {
				productById = product;
				break;
			}
		}
		return productById;
	}
	
	// 컬렉션 : Product 상품을 담는 객체
	// 컬렉션 상품을 추가하는 메서드.
	public void addProduct(Product product) {
		listOfProducts.add(product);
	}
}
