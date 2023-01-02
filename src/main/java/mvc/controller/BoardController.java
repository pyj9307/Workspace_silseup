package mvc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import mvc.model.BoardDAO;
import mvc.model.BoardDTO;
import mvc.model.FileImageDTO;

public class BoardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// 해당 게시판의 페이징 처리하기위한 상수값 -> 목록에 보여주는 갯수.
	static final int LISTCOUNT = 5;

	// get 으로 전송되어도, post 방식으로 다 처리하는 로직으로 예제 구성이 되어있음.
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	// 보드 관련된 모든 처리를 다하는 로직이라서 게시판에 접속만 하더라도 콘솔 창에서 확인 가능함.
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// RequestURI 의 주소 부분에서
		// contextPath 프로젝트명 부분을 자르기를 하고서
		// command : /BoardListAction.do 이런 형식으로 가져오기 위해서
		String RequestURI = request.getRequestURI();
		/* System.out.println("RequestURI의 값 : " + RequestURI); */
		String contextPath = request.getContextPath();
		/* System.out.println("contextPath의 값 : " + contextPath); */
		String command = RequestURI.substring(contextPath.length());
		/* System.out.println("command의 값 : " + command); */
		
		response.setContentType("text/html; charset=utf-8");
		request.setCharacterEncoding("utf-8");
	
		// 게시판을 클릭시, 여기 첫번째 조건문에서 처리하는 과정을 보자.
		if (command.equals("/BoardListAction.do")) {//등록된 글 목록 페이지 출력하기
			// 게시판의 페이지 정보랑, 게시물 정보등을 불러와서, 
			// 해당 request 객체에 담아두는역할.
			
			// 게시판의 목록에 관련된 비지니스 로직.
			requestBoardList(request);
			
			RequestDispatcher rd = request.getRequestDispatcher("./board/list.jsp");
			rd.forward(request, response);
		} else if (command.equals("/BoardWriteForm.do")) { // 글 등록 페이지 출력하기, 글쓰기 폼
				requestLoginName(request);
				RequestDispatcher rd = request.getRequestDispatcher("./board/writeForm.jsp");
				rd.forward(request, response);				
		} else if (command.equals("/BoardWriteAction.do")) {// 새로운 글 등록하기, 글쓰기 폼에서 입력 후 처리하는 로직
			// 여기서 글쓰기 작성 시 필요한 로직
			// 여기안에 이미지를 등록하는 메서드를 추가할 예정
				requestBoardWrite(request);
				RequestDispatcher rd = request.getRequestDispatcher("/BoardListAction.do");
				rd.forward(request, response);						
		} else if (command.equals("/BoardViewAction.do")) {//선택된 글 상세 페이지 가져오기
				requestBoardView(request);
				RequestDispatcher rd = request.getRequestDispatcher("/BoardView.do");
				rd.forward(request, response);						
		} else if (command.equals("/BoardView.do")) { //글 상세 페이지 출력하기
				RequestDispatcher rd = request.getRequestDispatcher("./board/view.jsp");
				rd.forward(request, response);	
		} else if (command.equals("/BoardUpdateAction.do")) { //선택된 글의 조회수 증가하기
				requestBoardUpdate(request);
				RequestDispatcher rd = request.getRequestDispatcher("/BoardListAction.do");
				rd.forward(request, response);
		}else if (command.equals("/BoardDeleteAction.do")) { //선택된 글 삭제하기
				requestBoardDelete(request);
				RequestDispatcher rd = request.getRequestDispatcher("/BoardListAction.do");
				rd.forward(request, response);				
		} 
	}
	//등록된 글 목록 가져오기	
	public void requestBoardList(HttpServletRequest request){
		
		// 임시로 게시판 목록 화면에 출력하기 위해서, 해당 정보들의 변수를 선언 및 재할당.
		// 만약 이 정보를 계속 사용하겠다고 하면, 위에서 전역 또는 선언만 하고 재할당해서 이용 가능.
		int listCount = BoardController.LISTCOUNT;
		String RequestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = RequestURI.substring(contextPath.length());
		
		// 게시판에서 해당 DB에 접근을 하기 위한 sql 문장이 모라져있다.
		// 싱글톤 패턴으로 하나의 객체를 이용을 하고 있음.
		// 게시판에 글쓰기, 수정하기, 삭제하기, 리스트 가져오기 등 여러 메서드들이 dao라는 객체에 담아져있다.
		BoardDAO dao = BoardDAO.getInstance();
		// 컬렉선 : 게시판의 글들을 담아두는 역할
		// 게시판에 하나의 글들은 각각 BoardDTO타입의 객체입니다.
		// boardlist -> 게시판의 각각의 게시글을 담아 둘 여정.
		// DB에 연결해서, 해단 게시글 목록들을 받아 둘 임시 저장 매체로서 사용.
		List<BoardDTO> boardlist = new ArrayList<BoardDTO>();
		
	  	int pageNum=1;
	  	// 목록 게시판에 보여줄 갯수 5개
		int limit=LISTCOUNT;
		
		// 만약 내장객체 request에 담겨진 페이지 정보가 null이 아니면, 해당 페이지 정보를 문자열에서 정수로 변환하겠다.
		if(request.getParameter("pageNum")!=null)
			pageNum=Integer.parseInt(request.getParameter("pageNum"));
		// items 는 게시판 화면 하단에 검색하는 창에서, 본문 검색, 글쓴이 검색, 작성자 검색 등. 조건.
		String items = request.getParameter("items");
		// text 는 해당 검색하기 위한 검색어.
		String text = request.getParameter("text");
		
		// dao 게시판에 관련된 메서드들이 다 있고, 전달 할 때, 해당 검색어 항목, 검색할 내용등을 같이 전달.
		// DB에 연결해서, 해당 게시글의 모든 갯수를 가져오는 역할.
		int total_record=dao.getListCount(items, text);
		
		// 실제적인 페이징 처리가 된 결과를 담을 컬렉션이라 보면 됩니다.
		boardlist = dao.getBoardList(pageNum, limit, items, text); 
		
		// total_page 선언만 실제 페이지 계산의 아래에 었음.
		int total_page;
		
		if (total_record % limit == 0){     
	     	total_page =total_record/limit;
	     	Math.floor(total_page);  
		}
		/*
		 * else{ total_page = total_record/limit; // 예) 11/5 = 2.2
		 * Math.floor(total_page); // 2.2 -> 2 total_page = total_page + 1; // 2 + 1 ->
		 * 3 }
		 */
		
		else{
			   total_page = total_record/limit; // 예) 11/5 = 2.2
			  double total_page_test =  Math.floor(total_page); // 2.2 -> 2
			  System.out.println("total_page의 값 찍어보기" + total_page);
			  total_page_test = total_page_test + 1; // 2 + 1 -> 3
			  System.out.println("total_page_test의 값 찍어보기 : " + total_page_test);
			}		
	   
   
		// 헤당 RequestURI, contextPath, command 를 해당 뷰에 데이터를 전달함.
		// 해당 뷰에서, 키이름으로 해당 값을 불러와서 사용할 예정.
		request.setAttribute("RequestURI", RequestURI);
		request.setAttribute("contextPath", contextPath);
		request.setAttribute("command", command);
		
		request.setAttribute("listCount", listCount);
   		request.setAttribute("pageNum", pageNum);
   		request.setAttribute("total_page", total_page);   
		request.setAttribute("total_record",total_record); 
		request.setAttribute("boardlist", boardlist);								
	}
	//인증된 사용자명 가져오기
	//해당 로그인 아이디로 
	public void requestLoginName(HttpServletRequest request){
				
		String id = request.getParameter("id");
		
		BoardDAO  dao = BoardDAO.getInstance();
		
		// 로그인한는 아이디가 DB에 있는지 검사하는 메서드.
		String name = dao.getLoginNameById(id);	
		
		// 해당 아이디가 있다면 request진행
		request.setAttribute("name", name);									
	}
	// 새로운 글 등록하기
	// 게시판 글쓰기 로직.
	// 추가로 이미지를 등록하는 메서드를 따로 분리해서 작업 후, 여기안에 해당 메서드를 호출 할 계획
	public void requestBoardWrite(HttpServletRequest request){
					
		// dao 게시판에 관련된 crud 메서드들이 있다.
		// 싱글톤 패턴.
		// BoardDAO dao = BoardDAO.getInstance();		
		
		// 사용자가 작성한 글의 내용을 담을 임시 객체.
		// 임시 객체는 해당 DB에 전달할 형식(DTO)
		// BoardDTO board = new BoardDTO();
		
		String filename = "";
		// String realFolder = "C:/upload"; //웹 어플리케이션상의 절대 경로
		// 해당 프로젝트의 특정 폴더의 위치를 정대경로로 알려줘서 상품 등록시 이미지의 저장경로.
		String realFolder = "C:\\JSP_Workspace1\\ch18_WebMarket_2\\src\\main\\webapp\\resources\\board_images";
		String encType = "utf-8"; //인코딩 타입
		int maxSize = 10 * 1024 * 1024; //최대 업로드될 파일의 크기10Mb
		
		MultipartRequest multi;
		
		try {
			multi = new MultipartRequest(request, realFolder, maxSize, encType, new DefaultFileRenamePolicy());
			//multi 관련 객체 샘플로 사용하기위해서 가지고 옴. 
			//String productId = multi.getParameter("productId");		
			
			// dao 게시판에 관련된 crud 메서드들이 있다.
			// 싱글톤 패턴.
			BoardDAO dao = BoardDAO.getInstance();		
			
			// 사용자가 작성한 글의 내용을 담을 임시 객체.
			// 임시 객체는 해당 DB에 전달할 형식(DTO)
			BoardDTO board = new BoardDTO();
			FileImageDTO fileDTO = new FileImageDTO();
		
		// 사용자로부터 입력받은 내용을 임시 객체에 담아두는 작업.
		board.setId(multi.getParameter("id"));
		board.setName(multi.getParameter("name"));
		board.setSubject(multi.getParameter("subject"));
		board.setContent(multi.getParameter("content"));	
		
		// 콘솔 상에 출력하기(해당 값을 잘 받아 오고 있는지 여부를 확인하는 용도.)
		System.out.println(multi.getParameter("name"));
		System.out.println(multi.getParameter("subject"));
		System.out.println(multi.getParameter("content"));
		//게시글의 등록 날짜 형식부분.
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd");
		String regist_day = formatter.format(new java.util.Date()); 
		
		// 해당 게시글의 조회수 설정 0
		board.setHit(0);
		// 등록날짜
		board.setRegist_day(regist_day);
		// 등록 아이피 설정
		board.setIp(request.getRemoteAddr());			
		
		// 임시 객체 board(DTO) 사용자가 글쓰기 시 입력한 정보와 보이지 않는 정보를 같이 전달함.
		// 글만 작성.
		dao.insertBoard(board);	
		// 해당 이미지를 저장하는 메서드를 만들기.
		// 매개변수에는 해당 게시글의 번호를 넣을 예정.
		// 하나의 게시글에 첨부된 이미지들의 목록도 있음.
		
		// board에서 이미지를 넣는 경우.
		// 1) 한개, 2) 두개 이상이 들어갈수도 있음
		// 3) 파일이미지가 없는 경우
		if(board.getFileList() != null) {
		dao.insertImage(board,fileDTO);
		}
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	//선택된 글 상세 페이지 가져오기
	// 해당 게시글의 상세 글 보기
	public void requestBoardView(HttpServletRequest request){
					
		// dao DB연결을 위한 객체 및 다수의 DB 연결 메소드.
		BoardDAO dao = BoardDAO.getInstance();
		// num : 해당 게시글의 글번호
		int num = Integer.parseInt(request.getParameter("num"));
		// pageNum : 페이징 처리에서 해당 페이지 번호.
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));	
		
		// 임시로 해당 게시글을 담은 객체(DTO)
		BoardDTO board = new BoardDTO();
		// dao에서 해당 글번호의 내용을 가져오는 메서드getBoardByNum(num, pageNum).
		// 이 메서드 안에 조회수를 중가하는 메서드가 포함되어 있다.
		board = dao.getBoardByNum(num, pageNum);		
		
		// 내장 객체에 선택된 하나의 게시글의 번호인 num
		// board : 하나의 선택된 게시글의 객체.
		request.setAttribute("num", num);		 
   		request.setAttribute("page", pageNum); 
   		request.setAttribute("board", board);   									
	}
	// 선택된 글 내용 수정하기
	// 게시판 수정하기
	public void requestBoardUpdate(HttpServletRequest request){
					
		// 문자열 형식의 게시글 번호를 int 형으로 변환하는 작업. parse
		int num = Integer.parseInt(request.getParameter("num"));
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));	
		
		BoardDAO dao = BoardDAO.getInstance();		
		
		// 임시로 해당 게시글을 담은 객체(DTO)
		BoardDTO board = new BoardDTO();	
		
		board.setNum(num);
		board.setName(request.getParameter("name"));
		board.setSubject(request.getParameter("subject"));
		board.setContent(request.getParameter("content"));		
		
		// 날짜 형식 지정하는 포맷을 잘 정리.
		 java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd(HH:mm:ss)");
		 String regist_day = formatter.format(new java.util.Date()); 
		 
		 // 게시글을 수정시 조회수를 0으로 초기화함.
		 board.setHit(0);
		 board.setRegist_day(regist_day);
		 board.setIp(request.getRemoteAddr());			
		
		 dao.updateBoard(board);								
	}
	//선택된 글 삭제하기
	// 삭제, 삭제를 생각했던, DB에서 트리거 작업으로
	// 삭제된 회원, 게시들 등 지운 내용을 새로운 테이블레 옮기는 작업도 가능.
	// 이부분은 DB 상에서 처리도 가능하고, 해당 서비스에도 따로 기능을 만듷어서 구현 가능.
	public void requestBoardDelete(HttpServletRequest request){
					
		int num = Integer.parseInt(request.getParameter("num"));
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));	
		
		BoardDAO dao = BoardDAO.getInstance();
		dao.deleteBoard(num);							
	}	
}
