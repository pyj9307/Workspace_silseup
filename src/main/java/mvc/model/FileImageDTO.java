package mvc.model;

public class FileImageDTO {
	private int Fnum;
	private String fileName;
	private String regist_day;
	private int num;
	
	public int getFnum() {
		return Fnum;
	}
	public void setFnum(int fnum) {
		Fnum = fnum;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getRegist_day() {
		return regist_day;
	}
	public void setRegist_day(String regist_day) {
		this.regist_day = regist_day;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
}
