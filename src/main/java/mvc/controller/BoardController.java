package mvc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.model.BoardDAO;
import mvc.model.BoardDTO;

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
		} else if (command.equals("/BoardWriteForm.do")) { // 글 등록 페이지 출력하기
				requestLoginName(request);
				RequestDispatcher rd = request.getRequestDispatcher("./board/writeForm.jsp");
				rd.forward(request, response);				
		} else if (command.equals("/BoardWriteAction.do")) {// 새로운 글 등록하기
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
	public void requestLoginName(HttpServletRequest request){
					
		String id = request.getParameter("id");
		
		BoardDAO  dao = BoardDAO.getInstance();
		
		String name = dao.getLoginNameById(id);		
		
		request.setAttribute("name", name);									
	}
	// 새로운 글 등록하기
	// 게시판 글쓰기 로직.
	public void requestBoardWrite(HttpServletRequest request){
					
		// dao 게시판에 관련된 crud 메서드들이 있다.
		// 싱글톤 패턴.
		BoardDAO dao = BoardDAO.getInstance();		
		
		// 사용자가 작성한 글의 내용을 담을 임시 객체.
		// 임시 객체는 해당 DB에 전달할 형식(DTO)
		BoardDTO board = new BoardDTO();
		
		// 사용자로부터 입력받은 내용을 임시 객체에 담아두는 작업.
		board.setId(request.getParameter("id"));
		board.setName(request.getParameter("name"));
		board.setSubject(request.getParameter("subject"));
		board.setContent(request.getParameter("content"));	
		
		// 콘솔 상에 출력하기(해당 값을 잘 받아 오고 있는지 여부를 확인하는 용도.)
		System.out.println(request.getParameter("name"));
		System.out.println(request.getParameter("subject"));
		System.out.println(request.getParameter("content"));
		//게시글의 등록 날짜 형식부분.
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd(HH:mm:ss)");
		String regist_day = formatter.format(new java.util.Date()); 
		
		// 해당 게시글의 조회수 설정 0
		board.setHit(0);
		// 등록날짜
		board.setRegist_day(regist_day);
		// 등록 아이피 설정
		board.setIp(request.getRemoteAddr());			
		
		dao.insertBoard(board);								
	}
	//선택된 글 상세 페이지 가져오기
	public void requestBoardView(HttpServletRequest request){
					
		BoardDAO dao = BoardDAO.getInstance();
		int num = Integer.parseInt(request.getParameter("num"));
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));	
		
		BoardDTO board = new BoardDTO();
		board = dao.getBoardByNum(num, pageNum);		
		
		request.setAttribute("num", num);		 
   		request.setAttribute("page", pageNum); 
   		request.setAttribute("board", board);   									
	}
	//선택된 글 내용 수정하기
	public void requestBoardUpdate(HttpServletRequest request){
					
		int num = Integer.parseInt(request.getParameter("num"));
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));	
		
		BoardDAO dao = BoardDAO.getInstance();		
		
		BoardDTO board = new BoardDTO();		
		board.setNum(num);
		board.setName(request.getParameter("name"));
		board.setSubject(request.getParameter("subject"));
		board.setContent(request.getParameter("content"));		
		
		 java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd(HH:mm:ss)");
		 String regist_day = formatter.format(new java.util.Date()); 
		 
		 board.setHit(0);
		 board.setRegist_day(regist_day);
		 board.setIp(request.getRemoteAddr());			
		
		 dao.updateBoard(board);								
	}
	//선택된 글 삭제하기
	public void requestBoardDelete(HttpServletRequest request){
					
		int num = Integer.parseInt(request.getParameter("num"));
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));	
		
		BoardDAO dao = BoardDAO.getInstance();
		dao.deleteBoard(num);							
	}	
}
