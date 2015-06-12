package ilbe.tracer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class tracer extends JFrame implements TreeSelectionListener {

	private static final long serialVersionUID = 1573L;
	
	private static DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
	private static JTree tree = new JTree(root);
	private JScrollPane scroll = new JScrollPane(tree);
	private JPanel panel = new JPanel();
	private JPanel resultPanel = new JPanel();
	private JTextField resultTextField = new JTextField(10);
	
	public tracer(){
		super("일베 유저 게시물 추적기");
		
		// 각 컴포넌트 선언 및 설정
		final JButton buttonStart = new JButton("검색");		
		JTextField textPeriod = new JTextField(20);
		JLabel labelPeriod = new JLabel("해쉬값 입력");
		buttonStart.setMnemonic('S');
		buttonStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				parse(textPeriod.getText());
			}
		});
		tree.setVisibleRowCount(10);
		
		// 각 패널에 컴포넌트 장착
		panel.add(labelPeriod);
		panel.add(textPeriod);
		panel.add(buttonStart);
		resultPanel.add(new JLabel("선택 항목"));
		resultPanel.add(resultTextField);
		
		// 프레임에 패널 장착
		add(panel, "North");
		add(scroll, "Center");
		add(resultPanel, "South");
		
		// 프레임 기본설정
		setBounds(100, 100, 300, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		pack();
		setSize(400, 700);
		setResizable(false);
		
		tree.addTreeSelectionListener(this);
	}
	
	public static void main(String[] args) {
		JFrame t = new tracer();
	}
	
	public static void parse(String userHash) {
		
		// searchNick should be not BLANK or NULL!
		String searchNick = "sth";

		String boardUrls[]={"ilbe","gae","suggestion","jjal","logo","tip","sangdam","university","stock","military","bike","car","free","fear","celeb","book","drama","lanhist","recipe","animation","movie","music","fashion","animal","camera","politics","gameall","lol","smartgame","df","diablo3","skyrim","rhythm","black","mabinogi","bns","wot","devilmaker","ma","smartphone","computer","baseball","football","sports"};
		String boardNames[]={"일간 베스트","개드립","문의","짤방","로고","일베 지식인","고민상담","대학","주식","밀리터리","자전거","자동차","잡담","공포,미스터리","걸그룹,연예인","도서","TV프로그램","랜선역사","요리","애니메이션","영화","음악","패션,미용","동물","카메라","정치","게임","LoL","스마트폰게임","던전앤파이터","블리자드","베데스다","리듬게임","검은사막","마비노기","블레이드앤소울","워게이밍넷","데빌메이커","밀리언 아서","스마트폰","컴퓨터","야구","축구","스포츠"};
		
		Connection conn;
		Document doc = null;
		
		for(int i=0; i<boardUrls.length; ++i){
			String findQuery = "index.php?mid="+ boardUrls[i] +"&search_target=nick_name&search_keyword="+searchNick+"&target_srl=" + userHash;
			conn = Jsoup.connect("http://www.ilbe.com/"+findQuery);
			// timeout에 대해 알아볼 것
			//conn.timeout(1000);
			
			try {
				doc = conn.get();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			Elements articleList = doc.select(".boardList .bg1, .boardList .bg2");

			// We have to find better solution for delay pre-working
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(articleList.isEmpty()) continue;
			System.out.println(">> "+ boardNames[i] +" 게시판");
			
			DefaultMutableTreeNode newBrach = new DefaultMutableTreeNode(boardNames[i] +" 게시판");
			root.add(newBrach);
            
			for(Element elmt : articleList){
				String num = elmt.select(".num").text();
				String title = elmt.select(".title").text();
				String author = elmt.select(".author").text();
				String date = elmt.select(".date").text();
				
				newBrach.add(new DefaultMutableTreeNode(title));
			}

			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		
			//변경된 내용을 재구성 한다
			model.reload(newBrach);
		}
		
		tree.expandRow(0);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if(e.getSource() == tree) {
			DefaultMutableTreeNode selNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			if(selNode == null) {
				//아무 항목도 선택되지 않으면 종료한다
				return;
			}
			resultTextField.setText(selNode.toString());
		}
	}
}
