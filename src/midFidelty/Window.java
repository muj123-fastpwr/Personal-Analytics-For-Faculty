package midFidelty;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;


import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.asprise.util.pdf.PDFReader;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;

/*
 * @author: Mujtaba Ali
 */

public class Window {
	private String[] apps;
	private String[] wins;
	private GridBagConstraints gbc_label;
	private PreProcessing pre = new PreProcessing();
	private Analysis anal;
	public boolean startStop, admit;
	
	
	public Window(Connection cn) throws ClassNotFoundException, IOException{
		anal = new Analysis();
		anal.train();
	}
	
	public Window(){
		apps =null;
		wins = null;
	}
	
	
	
	public void openWindows(){
		apps = null;
		wins = null;
		String[] a = new String[100];
		String[] w = new String[100];
		
		try {
			String process;
			
			int i=0;
			Process p = Runtime.getRuntime().exec("tasklist /v /fo csv /nh");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		//	System.out.println(input.read());// total number of lines
			while ((process = input.readLine()) != null) {
				process = process.replaceAll("\"", "");
				String[]str = process.split(",");
				if(str.length == 11){
					if(!str[10].equals("N/A")){
			//			System.out.println(str[0]+"__"+str[10]);
						a[i] = str[0];
						w[i] = str[10];
					}	
				}
				else if(str.length == 10){
					if(!str[9].equals("N/A")){
			//			System.out.println(str[0]+"__"+str[9]);
						a[i] = str[0];
						w[i] = str[9];
					}	
				}
				else if(str.length==9){
					if(!str[8].equals("N/A")){
			//			System.out.println(str[0]+"__"+str[8]);
						a[i] = str[0];
						w[i] = str[8];
					}
				}
					i++;				
			}
			input.close();
			
		} catch (Exception err) {
			err.printStackTrace();
		}
		int count=0;
		for(int j=0;j<a.length;j++){
			if(a[j]!=null){
				count++;
			}
		}
		String[]ap = new String[count];
		String[]win = new String[count];
		count=0;
		for(int j=0;j<a.length;j++){
			if(a[j]!=null){
				if(w[j].matches("(.*) - (.*)")){	
					ap[count]=a[j];
					win[count]=w[j];
					count++;
				}
			}
		}
		
		apps = ap;
		wins = win;
	}

	
	
	public void currentApps(JTextArea activeWindow, JPanel CurrentApps, GridBagLayout gbl_CurrentApps){
		CurrentApps.removeAll();
		gbl_CurrentApps.columnWidths = new int[]{89, 0};
		gbl_CurrentApps.rowHeights = new int[]{14, 14, 14, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_CurrentApps.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_CurrentApps.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		CurrentApps.setLayout(gbl_CurrentApps);
		
		
		for(int i=0;i<apps.length-1;i++){
			
			JLabel appLabel = new JLabel(apps[i]);
			appLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
			appLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					
					String temp="";
					String label = appLabel.getText();
					for(int j=0;j<apps.length;j++){
						if(label.equals(apps[j])){
							temp = wins[j]+"\n";
							break;
						}
					}
					activeWindow.setText(temp);
					
				}
				@Override
				public void mouseEntered(MouseEvent arg0) {
					appLabel.setForeground(Color.GRAY);
					appLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
				@Override
				public void mouseExited(MouseEvent e) {
					appLabel.setForeground(Color.BLACK);
				}
			});
			
			gbc_label = new GridBagConstraints();
			gbc_label.anchor = GridBagConstraints.WEST;
			gbc_label.insets = new Insets(0, 0, 5, 0);
			gbc_label.gridx = 0;
			gbc_label.gridy = i;
			CurrentApps.add(appLabel, gbc_label);
		}
		CurrentApps.repaint();
		
	}
	
	
	
	
	public void focusedWindow(Connection cn, Connection c) throws InterruptedException, HeadlessException, SQLException, IOException, ClassNotFoundException{
		startStop = true;
		admit = true;
		char[] buffer = new char[200];
		String newWindowTitle="",oldWindowTitle="";
		String text = "", type="";
		int time=0;
		int h1,m1,s1;
		Communication com = new Communication(cn,c);
		
		
		while(true){
			
			if(!startStop)
				System.out.println("Monitoring stopped");
			
			if(startStop){
				System.out.println("Monitoring started");
		        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
		       
		        User32.INSTANCE.GetWindowText(hwnd, buffer, 200);
		        newWindowTitle = Native.toString(buffer);
		        
		        GregorianCalendar gcalendar1 = new GregorianCalendar();
				h1 = gcalendar1.get(Calendar.HOUR);
				m1 = gcalendar1.get(Calendar.MINUTE);
				s1 = gcalendar1.get(Calendar.SECOND);
				time = h1 * 3600 + m1 *60 + s1;
			
				
				
				if((compareStrings(newWindowTitle, oldWindowTitle) < 0.95)){
					
					if(newWindowTitle.matches("(.*).pdf(.*)")){
						String wnd="";
		
						if(com.ifWindowExists(newWindowTitle)){
													
							System.out.println("window exists");
							
							com.updateData(time);
							
						}
						
						else{
							System.out.println("pdf reading");
							type = contentExtractionFromPdf( wnd =(newWindowTitle.replace(" - Foxit Reader", "")) );
							if(!type.equals(""))
								com.newEntry(newWindowTitle, type, time);
						}
					
					}
					
					
					else if(newWindowTitle.matches("(.*).docx(.*)")){
						String wnd ="";
						if(com.ifWindowExists(newWindowTitle)){
							
							com.updateData(time);
						}
						
						else{
							
							type = contentExtractionFromDocx( wnd = (newWindowTitle.replace(" - Microsoft Word", "")) );
							if(!type.equals(""))
								com.newEntry(newWindowTitle, type, time);
							
						}
					}
					
					
					else if(newWindowTitle.matches("(.*).txt(.*)")){
						String wnd ="";
						if(com.ifWindowExists(newWindowTitle)){
							com.updateData(time);
						}
						else{
							type = contentExtractionFromTxt( wnd = (newWindowTitle.replace(" - Notepad", "")) );
							if(!type.equals(""))
								com.newEntry(newWindowTitle, type, time);
						
						}
					}
					
					
					else if(newWindowTitle.endsWith(" - Google Chrome") || newWindowTitle.endsWith(" - Mozilla Firefox")){
						if(com.ifWindowExists(newWindowTitle)){
							com.updateData(time);
						}
						else{
							
							
							
							if(com.ifExists(newWindowTitle)){
								String url = com.getUrl(newWindowTitle);
								type = contentExtractionFromWebPage(url);
								if(!type.equals(""))
									com.newEntry(newWindowTitle, type, time);
							}
						
						}
						
					}
					
					else{
						if(com.ifWindowExists(newWindowTitle)){
							com.updateData(time);
						}
						else{
							com.newEntry(newWindowTitle, "O", time);
						}
					}
					if(admit)
						oldWindowTitle = newWindowTitle;
					
				}
				
			//	com.autoUpdate(newWindowTitle=newWindowTitle.trim(),time);
		        
		        gcalendar1 = null;
		        System.gc();
			}
			
			Thread.sleep(1000);
			
		}
		
		
	}
	
	public double compareStrings(String stringA, String stringB) {
	    JaroWinkler algorithm = new JaroWinkler();
	    return algorithm.getSimilarity(stringA, stringB);
	}
	
	public String contentExtractionFromPdf(String fileName) throws ClassNotFoundException{
		String [] bagOfWords = null;
		String text = fileName+":\n",type="";
		 try {
			 	System.setProperty("java.util.Arrays.useLegacyMergeSort","true");
			 	
			 	System.out.println(fileName);
				String path = getFilePath("D:\\1_University",fileName);
				PDFReader reader = new PDFReader(new File(path));
				reader.open(); // open the file. 
				int pages = reader.getNumberOfPages();
				for(int i=0; i < pages; i++) {
				   text =text +" "+ reader.extractTextFromPage(i);
				  
				}
				
				
				type = anal.classify2(text);
			/*	
				bagOfWords = pre.clean(text);
				bagOfWords = pre.removeStopWords(bagOfWords);
				System.out.println(fileName);
				type = anal.classify(bagOfWords);
			*/
			//	text = pre.stem(bagOfWords);
			
				reader.close();
				admit = true;
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				admit = false;
				System.out.println("File not found");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("File not found");
				admit = false;
				e.printStackTrace();
			}
		 return type;
	}
	
	
	public String contentExtractionFromDocx(String fileName){
		String text=fileName+":\n", type="";
		String [] bagOfWords = null;
		fileName = fileName.trim();
		
		
		String path = getFilePath("D:\\1_University",fileName);
		 try {
				XWPFDocument docx = new XWPFDocument(new FileInputStream(path));
				//using XWPFWordExtractor Class
				XWPFWordExtractor we = new XWPFWordExtractor(docx);
				text = we.getText();
				//System.out.println(we.getText());
				
				type = anal.classify2(text);
			/*	
				bagOfWords = pre.clean(text);
				bagOfWords = pre.removeStopWords(bagOfWords);
				type = anal.classify(bagOfWords);
			*/	
			//	text = pre.stem(bagOfWords);
				
				
			/*	
				POIXMLTextExtractor txt = we.getMetadataTextExtractor();
				System.out.println("IMPORTANT : "+txt.getText());
				ExtendedProperties cpro = we.getExtendedProperties();
				System.out.println("MORE IMPORTANT : "+cpro.getApplication());
				*/
				we.close();
				admit = true;
			} catch (FileNotFoundException e) {
				admit = false;
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				admit = false;
				e.printStackTrace();
			}
		 return type;
	}
	
	public String contentExtractionFromTxt(String fileName) throws IOException{
		String text=fileName+"\n", type="";
		String [] bagOfWords = null;
		fileName = fileName.trim();
		
		
		String path = getFilePath("D:\\1_University",fileName);
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = "";
		 while ((line = reader.readLine()) != null)
		    {
		      text +=" "+ line;
		    }
		    reader.close();
		    
		    type = anal.classify2(text);
		 /*   
			bagOfWords = pre.clean(text);
			bagOfWords = pre.removeStopWords(bagOfWords);
			System.out.println(fileName);
			type = anal.classify(bagOfWords);
			
		*/	
			//	text = pre.stem(bagOfWords);
				
				
			
		 return type;
	}
	
	public String contentExtractionFromWebPage(String URL){
		URL = URL.trim();
		System.out.println("Content Extraction from : "+URL);
		String type = "";
        try {
			URL url = new URL(URL); 
			Document doc = Jsoup.parse(url, 5*1000);
			
	/*		
			for(Element meta : doc.select("meta[name=keywords]")) {
			//    System.out.println("Name: " + meta.attr("name") + " - Content: " + meta.attr("content"));
			    System.out.println("Content: " + meta.attr("content"));
			}
			
		*/	
			Elements paragraphs = doc.select("p"); 
			Element firstParagraph = paragraphs.first();
			Element lastParagraph = paragraphs.last();
            Element p;
            int i=0;
            p=firstParagraph;
            String para="";
            while (p!=lastParagraph){
                p=paragraphs.get(i);
                para +=" " + p.text();
          //      System.out.println(p.text());
                i++;
                
            } 
            
            
            type = anal.classify2(para);
            admit = true;
        /*    
            String []bagOfWords = null;
            bagOfWords = pre.clean(para);
			bagOfWords = pre.removeStopWords(bagOfWords);
			 type = anal.classify(bagOfWords);
		/	 
		//	para = pre.stem(bagOfWords);
            
/*			
            Elements heading = doc.select("h1");
            Element firstHeading = heading.first();
            Element lastHeading = heading.last();
            Element h;
            i=0;
            h=firstHeading;
            String head = "";
            while (h!=lastHeading){
                h=heading.get(i);
                head += h.text();
            //    System.out.println("HEADING "+i+" : "+h.text());
                i++;
                
            } 
           
            
            bagOfWords = null;
            bagOfWords = pre.clean(head);
			bagOfWords = pre.removeStopWords(bagOfWords);
			head = pre.stem(bagOfWords);
            
			System.out.println(head);
                
			String title = doc.title();
			String content = doc.text();	// full webPage text content
			String urlLink = doc.baseUri();	//	current webPage URL
			String Html = doc.html();		// complete html form of webPage
			String pageTitle = doc.title();	// web Page title
			String alsoContent = doc.body().text(); // full webPage text content
			String alsoLink = doc.location();
			
		//	System.out.println("JSOUP : "+alsoContent);
		 */
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			admit = false;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			admit = false;
		}
        
        return type;
	}
	
	public String getFilePath(String rootPath, String filename) {
		String filePath = null;
		File root = new File(rootPath);
		File[] list = root.listFiles();
		for (File f : list) {
			
		    if (f.isDirectory()) {
		        // store the filePath 
		        filePath = getFilePath(f.getAbsolutePath(), filename);    
		    } else if (f.getName().equalsIgnoreCase(filename)) {
		        filePath = f.getAbsolutePath();
		     //   System.out.println(filePath);
		        break;
		    }
		  if (filePath != null) {
		    break;
		  }
		}
		return filePath;
	}
	
}
