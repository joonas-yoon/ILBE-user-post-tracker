package ilbe.tracer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class tracer extends JFrame implements TreeSelectionListener {

	private static final long serialVersionUID = 1573L;

	private static DefaultMutableTreeNode root = new DefaultMutableTreeNode(
			"게시판 목록");
	private static JTree tree = new JTree(root);
	private static JScrollPane scroll = new JScrollPane(tree);
	private static JPanel panel = new JPanel();
	private static JPanel resultPanel = new JPanel();
	private static JLabel resultLabel = new JLabel("선택한 항목이 없습니다.");
	private static JButton resultButton = new JButton("이동");

	public tracer() {
		super("일베 유저 게시물 추적기");
		// Container contentPane = this.getContentPane();
		// panel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 40));

		// 각 컴포넌트 선언 및 설정
		final JButton buttonStart = new JButton("검색");
		JTextField textPeriod = new JTextField(20);
		JLabel labelPeriod = new JLabel("해쉬값 입력");
		buttonStart.setMnemonic('S');
		buttonStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = textPeriod.getText();
				if (text.isEmpty()) {
					JOptionPane.showMessageDialog(null, "해쉬값을 입력하세요", "잠깐!",
							JOptionPane.WARNING_MESSAGE);
				} else {
					parse(text);
				}
			}
		});
		resultButton.setEnabled(false);
		tree.setVisibleRowCount(10);

		// 각 패널에 컴포넌트 장착
		panel.add(labelPeriod);
		panel.add(textPeriod);
		panel.add(buttonStart);
		resultPanel.add(resultLabel);
		resultPanel.add(resultButton);

		// 프레임에 패널 장착
		add(panel, "North");
		add(scroll, "Center");
		add(resultPanel, "South");

		// 프레임 기본설정
		setBounds(100, 100, 300, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		pack();
		setSize(400, 500);
		setMinimumSize(new Dimension(400, 400));

		tree.addTreeSelectionListener(this);
	}

	public static void main(String[] args) {
		new tracer();
	}

	public static void parse(String userHash) {

		// searchNick should be not BLANK or NULL!
		String searchNick = "sth";

		String boardUrls[] = { "ilbe", "gae", "suggestion", "jjal", "logo",
				"tip", "sangdam", "university", "stock", "military", "bike",
				"car", "free", "fear", "celeb", "book", "drama", "lanhist",
				"recipe", "animation", "movie", "music", "fashion", "animal",
				"camera", "politics", "gameall", "lol", "smartgame", "df",
				"diablo3", "skyrim", "rhythm", "black", "mabinogi", "bns",
				"wot", "devilmaker", "ma", "smartphone", "computer",
				"baseball", "football", "sports" };
		String boardNames[] = { "일간 베스트", "개드립", "문의", "짤방", "로고", "일베 지식인",
				"고민상담", "대학", "주식", "밀리터리", "자전거", "자동차", "잡담", "공포,미스터리",
				"걸그룹,연예인", "도서", "TV프로그램", "랜선역사", "요리", "애니메이션", "영화", "음악",
				"패션,미용", "동물", "카메라", "정치", "게임", "LoL", "스마트폰게임", "던전앤파이터",
				"블리자드", "베데스다", "리듬게임", "검은사막", "마비노기", "블레이드앤소울", "워게이밍넷",
				"데빌메이커", "밀리언 아서", "스마트폰", "컴퓨터", "야구", "축구", "스포츠" };

		root.removeAllChildren();
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		String findSiteUrl = "http://www.ilbe.com/";
		resultButton.setEnabled(false);

		ExecutorService executorService = Executors.newFixedThreadPool(5);
		List<Future<Void>> handles = new ArrayList<Future<Void>>();
		Future<Void> handle;

		for (int i = 0; i < boardUrls.length; ++i) {
			String findQuery = "index.php?mid=" + boardUrls[i]
					+ "&search_target=nick_name&search_keyword=" + searchNick
					+ "&target_srl=" + userHash;
			String boardName = boardNames[i];
			handle = executorService.submit(new Callable<Void>() {
				public Void call() throws Exception {

					Document doc = null;
					Connection conn = Jsoup.connect(findSiteUrl + findQuery);
					// conn.timeout(3000);

					try {
						doc = conn.get();
					} catch (IOException e1) {
						// e1.printStackTrace();
					}

					Elements articleList = doc
							.select(".boardList .bg1, .boardList .bg2");

					if (articleList.isEmpty())
						return null;

					System.out.println(">> " + boardName + " 게시판");

					DefaultMutableTreeNode newBrach = new DefaultMutableTreeNode(
							boardName + " 게시판");
					root.add(newBrach);

					for (Element elmt : articleList) {
						// String num = elmt.select(".num").text();
						String title = elmt.select(".title").text();
						String author = elmt.select(".author").text();
						String date = elmt.select(".date").text();
						String docSrl = elmt.select(".title a").attr("href");
						// System.out.println(docSrl);

						ArticleInfo article = new ArticleInfo(title, author,
								date);
						article.setUrl(findSiteUrl + docSrl);
						newBrach.add(new DefaultMutableTreeNode(article));
					}

					// 변경된 내용을 재구성 한다
					model.reload(newBrach);
					return null;
				}
			});
			handles.add(handle);
		}

		for (Future<Void> h : handles) {
			try {
				h.get();
			} catch (Exception ex) {
				// ex.printStackTrace();
			}
		}
		executorService.shutdownNow();

		tree.expandRow(0);
		model.reload();

		if (tree.getRowCount() < 2) {
			// counting from root as 1
			JOptionPane.showMessageDialog(null, "검색 결과가 없습니다.", null,
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, "검색을 마쳤습니다.", null,
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		resultLabel.setText("선택한 항목이 없습니다.");
		resultButton.setEnabled(false);
		if (e.getSource() == tree) {
			DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			if (selNode == null || selNode.isLeaf() == false) {
				// 아무 항목도 선택되지 않으면 종료한다
				return;
			}
			ArticleInfo article = (ArticleInfo) selNode.getUserObject();
			resultLabel.setText(article.toString());
			System.out.println(article.getUrl());
			resultButton.setEnabled(true);
		}
	}
}
