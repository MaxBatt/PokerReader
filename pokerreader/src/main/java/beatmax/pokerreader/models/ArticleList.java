package beatmax.pokerreader.models;

import java.util.ArrayList;

public class ArticleList {

	public ArticleList() {
		this.articleList = new ArrayList<Article>();
	}

	private ArrayList<Article> articleList;

	public ArrayList<Article> get() {
		return articleList;
	}

	public void set(ArrayList<Article> articleList) {
		this.articleList = articleList;
	}
}
