package beatmax.pokerreader.models;

public class FavoriteSites {
	public boolean pokerstrategy = false;
	public boolean pokerfirma = false;
	public boolean hochgepokert = false;
	public boolean pokerolymp = false;
	public boolean pokernews = false;


	public boolean hasFavorites(){
		
		if(pokerstrategy || pokerfirma || hochgepokert || pokerolymp || pokernews){
			return true;
		}
		else{
			return false;
		}
	}
}


