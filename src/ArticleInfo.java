package ilbe.tracer;

import javax.swing.tree.DefaultMutableTreeNode;

public class ArticleInfo extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	private String title = null;
	@SuppressWarnings("unused")
	private String author = null;
	private String date = null;
	private String url = null;

	public ArticleInfo() {
		title = null;
		author = null;
		date = null;
		url = null;
	}

	public ArticleInfo(String title, String author, String date) {
		this.title = title;
		this.author = author;
		this.date = date;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public String toString() {
		return "[" + date + "] " + title;
	}
}
