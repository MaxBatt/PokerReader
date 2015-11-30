package beatmax.pokerreader.models;


public class Article{


	private String id;
	private String createdAt;
	private String url;
	private String siteName;
	private String title;
	private String prevText;
	private String thumbUrl;
	private String articleHTML;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPrevText() {
		return prevText;
	}
	public void setPrevText(String prevText) {
		this.prevText = prevText;
	}
	public String getThumbUrl() {
		return thumbUrl;
	}
	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}
	public String getArticleHTML() {
		return articleHTML;
	}
	public void setArticleHTML(String articleHTML) {
		this.articleHTML = articleHTML;
	}

	
	
}