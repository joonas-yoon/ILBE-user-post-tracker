package ilbe.tracer;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class tracer extends JFrame {

	private static final long serialVersionUID = 1573L;

	public tracer(){
		super("일베 유저 게시물 추적기");
		
		setBounds(100, 100, 300, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container contentPane = this.getContentPane();
		JPanel pane = new JPanel();
		final JButton buttonStart = new JButton("Start");
		buttonStart.setMnemonic('S');
		
		JTextField textPeriod = new JTextField(5);
		JLabel labelPeriod = new JLabel("Input hash ");
		pane.add(labelPeriod);
		pane.add(textPeriod);
		
		buttonStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("OK! with "+ textPeriod.getText());
			}
		});

		pane.add(buttonStart);
		contentPane.add(pane);
		
		//setSize(500, 500);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		JFrame t = new tracer();
		parse("143c23c425cafb37cd0c704309a53eedb79232dfad91974c6fb2c573c665bef2");
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
			conn.timeout(5000);
			
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
			
			for(Element elmt : articleList){
				String num = elmt.select(".num").text();
				String title = elmt.select(".title").text();
				String author = elmt.select(".author").text();
				String date = elmt.select(".date").text();
				System.out.println("[ "+ num +" "+ title +" "+ author +" "+ date +" ]");
			}
		}
	}
}
