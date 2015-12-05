package beatmax.pokerreader.models;

public enum SitesE {
	HIGHSTAKESDB("highstakesdb.com"),
	POKERSTRATEGY("pokerstrategy.com"),
	POKERFIRMA("pokerfirma.com"),
	HOCHGEPOKERT("hochgepokert.com"),
	POKEROLYMP("pokerolymp.com"),
	POKERNEWS("pokernews.com");

	private SitesE(String value){
		this.value = value;
	}

	private final String value;

	public String getValue(){return value;}
}
