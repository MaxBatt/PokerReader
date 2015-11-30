package beatmax.pokerreader.models;

public enum ThemesE {
	BLUE("#66CDAA"),
	GREEN("#2E8B57"),
	RED("#DC143C"),
	ORANGE("#D2691E"),
	PURPLE("#8A2BE2"),
	BLACK("#000000");

	private ThemesE(String value){
		this.value = value;
	}

	private final String value;

	public String getValue(){return value;}
}
