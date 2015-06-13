package ilbe.tracer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class loadThread extends Thread {

	private String findSiteUrl;
	private String findQuery;
	private String boardName;
	Document doc = null;
	Connection conn = null;

	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;

	public loadThread(DefaultMutableTreeNode root, DefaultTreeModel model) {
		this.model = model;
		this.root = root;
	}

	public void setUrlInfo(String url, String query, String board_name) {
		findSiteUrl = url;
		findQuery = query;
		boardName = board_name;
	}

	public void run() {

		// 변경된 내용을 재구성 한다
		model.reload();

		// 1초 내에 패킷을 날리면 비정상적인 접속으로 일베에서 차단함
		try {
			sleep(1000);
		} catch (InterruptedException e) {
		}

		URL url = null;
		try {
			url = new URL(findSiteUrl + findQuery);
		} catch (MalformedURLException e) {
		}
		try {
			doc = Jsoup.parse(url, 1000);
		} catch (IOException e) {
		}

		Elements articleList = doc.select(".boardList .bg1, .boardList .bg2");

		if (articleList.isEmpty())
			return;

		System.out.println(">> " + boardName + " 게시판");

		DefaultMutableTreeNode newBrach = new DefaultMutableTreeNode(boardName
				+ " 게시판");
		root.add(newBrach);

		for (Element elmt : articleList) {
			String title = elmt.select(".title").text();
			String author = elmt.select(".author").text();
			String date = elmt.select(".date").text();
			String docSrl = elmt.select(".title a").attr("href");
			// System.out.println(docSrl);

			ArticleInfo article = new ArticleInfo(title, author, date);
			article.setUrl(docSrl);
			newBrach.add(new DefaultMutableTreeNode(article));
		}

		// 변경된 내용을 재구성 한다
		model.reload(newBrach);

		interrupt();
	}
}
