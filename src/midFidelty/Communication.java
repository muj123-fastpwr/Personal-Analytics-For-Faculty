package midFidelty;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;

/*
 * @author: Mujtaba Ali
 */
public class Communication {
	private Connection cn, c;
	private Statement st;
	private ResultSet rs;
	private PreparedStatement ps;
	private int time;
	private int winId, oldWinId=1;
	private String date;
	
	
	public Communication(){
		;
	}
	public Communication(Connection cn, Connection c){
		this.cn = cn;
		this.c = c;
		GregorianCalendar gcalendar1 = new GregorianCalendar();
		int h1,m1,s1;
		h1 = gcalendar1.get(Calendar.HOUR);
		m1 = gcalendar1.get(Calendar.MINUTE);
		s1 = gcalendar1.get(Calendar.SECOND);
		time = h1 * 3600 + m1 *60 + s1;
		
		Date date2 = new Date();
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
		System.out.println();
		date = ft.format(date2);
		
	}
	
	public boolean ifNoRow(String title){
		boolean flag = false;
		String query = "select * from window where winName = '"+title+"'";
		try {
			st=cn.createStatement();
			rs=st.executeQuery(query);
			if(!rs.first())
				flag=true;
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return flag;
	}
	
	public ResultSet getResultSet(int id, String table){
		String query = "select * from '"+table+"' where winId = "+id+"";
		try {
			st=cn.createStatement();
			rs=st.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	
	public ResultSet getTimeInterval(String type){
		String query = "SELECT startTime, endTime from window natural join date natural join time where date='"+date+"' and type= '"+type+"'";
		try {
			st=cn.createStatement();
			rs=st.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public void newEntry(String title, String type, int startTime) throws SQLException{
		
		if(title == "" || title.equals("") || title == null || title.equals(null)){
			return;
		}
		
		
		String query = "select max(timeId) from time;";
		int tid=0;
		try{
		st=cn.createStatement();
		rs=st.executeQuery(query);
		while(rs.next()){
			tid = rs.getInt(1);
		}

		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Unable to update endTime \n"+e);
		}
		
		
		if(tid!=0){
			query="update time set endTime="+startTime+" where winId="+oldWinId+" and timeId="+tid+";";
			try{
				ps = cn.prepareStatement(query);
				ps.executeUpdate();
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Unable to update endTime \n"+e);
			}
			
		}

		query = "select max(winId) from window;";
		try{
			
			st=cn.createStatement();
			rs=st.executeQuery(query);
			while(rs.next()){
				winId = rs.getInt(1)+1;
			}
			query = "insert into window values("+winId+",'"+title+"','"+type+"');";
			ps = cn.prepareStatement(query);
			ps.executeUpdate();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Unable to insert into Window Table\n"+e);
		}
		
		query = "insert into date values("+winId+",'"+date+"', "+0+")";
		try{
			ps = cn.prepareStatement(query);
			ps.executeUpdate();
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(null, "Unable to insert into date Table\n"+e);
		}
		

		query = "insert into time(startTime, endTime, winId, date) values("+startTime+","+0+","+winId+",'"+date+"');";
		try{
			ps = cn.prepareStatement(query);
			ps.executeUpdate();
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(null, "Unable to insert into time Table\n"+e);
		}
		oldWinId = winId;
		rs.close();
	}
	
	
	public void updateData(int startTime) throws SQLException{
		String query = "select max(timeId) from time;";
		int tid=0;
		try{
		st=cn.createStatement();
		rs=st.executeQuery(query);
		if(rs.first()){
			tid = rs.getInt(1);
		}

		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Unable to update endTime \n"+e);
		}
		
		query="update time set endTime="+startTime+" where winId="+oldWinId+" and timeId="+tid+";";
		try{
			ps = cn.prepareStatement(query);
			ps.executeUpdate();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Unable to update endTime \n"+e);
		}
		
		
		
		query = "select date from date where winId="+winId+" and date='"+date+"'";
		try{
			st=cn.createStatement();
			rs=st.executeQuery(query);
			
			if(!rs.first()){
				
				query = "insert into date values("+winId+",'"+date+"',"+0+")";
				try{
					ps = cn.prepareStatement(query);
					ps.executeUpdate();
					
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Unable to update date for new day\n"+e);
				}
				
				
				query = "insert into time(startTime,endTime,winId,date) values("+startTime+","+0+","+winId+",'"+date+"');";
				try{
					ps = cn.prepareStatement(query);
					ps.executeUpdate();
					
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Unable to insert time for new day\n"+e);
				}
				
			}
			
			else{
			
				query = "insert into time(startTime,endTime,winId,date) values("+startTime+","+0+","+winId+",'"+date+"')";
				try{
					ps = cn.prepareStatement(query);
					ps.executeUpdate();
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Unable to insert into time \n"+e);
				}
			}
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(null, "Unable to retrieve from date\n"+e);
		}			
	
		oldWinId = winId;
		rs.close();
	}
	
	
	
	public boolean ifWindowExists(String title){
		int id=0;
		double match1 = 0.0, match2 = 0.0;
		boolean titleExists = false;
		
		String query="select winId, winName from window;";
		
		try {
			st=cn.createStatement();
			rs=st.executeQuery(query);
			while(rs.next()){
				if((match1 = compareStrings(rs.getString(2),title)) >= 0.80){
					winId = rs.getInt(1);
					
					if(match1 >= match2){
						match2 = match1;
						id = winId;
					}
					else{
						winId = id;
					}
					titleExists = true;
				}
			}
			
	
			
		}
		catch (SQLException e) {
		 	
			JOptionPane.showMessageDialog(null, "Error in ifWindowExists \n"+e);
		}
		
		
		
		
		return titleExists;
	}
	
	
	
	public void autoUpdate(String title,int newTime) throws HeadlessException, SQLException{

		if(title == "" || title.equals("") || title == null || title.equals(null)){
			return;
		}
		
		int oldTime=0,winId=0, id = 0;
		double match1 = 0.0, match2 = 0.0;
		String query="select winId, winName from window;";
		boolean titleExists = false;
		try {
			st=cn.createStatement();
			rs=st.executeQuery(query);
			// rs.first(); boolean: whether row exists in resultset object
			// rs.getInt(int columnIndex); 
			while(rs.next()){
				if((match1 = compareStrings(rs.getString(2),title)) >= 0.70){
					winId = rs.getInt(1);
					
					if(match1 >= match2){
						match2 = match1;
						id = winId;
					}
					else{
						winId = id;
					}
					titleExists = true;
				}
			}
			
		}
		catch (SQLException e) {
		 	
			JOptionPane.showMessageDialog(null, "Unable to retrieve from database\n"+e);
		}
			
		if(!titleExists){
			
			query = "select max(winId) from window;";
			try{
				
				st=cn.createStatement();
				rs=st.executeQuery(query);
				while(rs.next()){
					id = rs.getInt(1)+1;
				}
				query = "insert into window values("+id+",'"+title+"',"+0+");";
				ps = cn.prepareStatement(query);
				ps.executeUpdate();
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Unable to insert into Window Table\n"+e);
			}
			
			query = "insert into date values("+id+",'"+date+"', "+(newTime-time)+")";
			try{
				ps = cn.prepareStatement(query);
				ps.executeUpdate();
			}
			catch(Exception e){
				JOptionPane.showMessageDialog(null, "Unable to insert into date Table\n"+e);
			}
			
			// extract extension
			
			
		}
		
		else if(titleExists){
			query = "select time from date where winId="+winId+" and date='"+date+"'";
			try{
				st=cn.createStatement();
				rs=st.executeQuery(query);
				
				if(!rs.next()){
					
					query = "insert into date values("+winId+",'"+date+"',"+(newTime-time)+")";
					try{
						ps = cn.prepareStatement(query);
						ps.executeUpdate();
						
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to update time for new day\n"+e);
					}
					
				}
				
				else{
				//	while(rs.next()){
						oldTime = rs.getInt(1);
						
				//	}
					query = "update date set time ="+(newTime-time+oldTime)+" where winId="+winId+" and date='"+date+"'";
					try{
						ps = cn.prepareStatement(query);
						ps.executeUpdate();
					}
					catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to update time\n"+e);
					}
				}
			}
			catch(Exception e){
				JOptionPane.showMessageDialog(null, "Unable to retrieve from date\n"+e);
			}
/*			if(oldTime!=0){
				query = "update dateAndTime set time ="+(newTime-time+oldTime)+" where winId="+winId+" and date='"+date+"'";
				try{
					ps = cn.prepareStatement(query);
					ps.executeUpdate();
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Unable to update time\n"+e);
				}
			}
			else if(oldTime==0){
				query = "insert into dateAndTime values("+winId+",'"+date+"',"+(newTime-time)+")";
				try{
					ps = cn.prepareStatement(query);
					ps.executeUpdate();
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Unable to update time for new day\n"+e);
				}
		*/		
			}
			
		
		rs.close();
		time = newTime;
	}
	
	
	public String getUrl(String title){
		String url="";
		double match1 = 0.0, match2 = 0.0;
	
		
		String query = "select url_title, url from tabs_info";
		
		try {
			st=c.createStatement();
			rs=st.executeQuery(query);
			while(rs.next()){
				if((match1 = compareStrings(rs.getString(1),title)) >= 0.70){
					
					if(match1 >= match2){
						match2 = match1;
						url = rs.getString(2);
					}
					
				}
			}
			rs.close();
			
		}
		catch (SQLException e) {
		 	
			JOptionPane.showMessageDialog(null, "Unable to retrieve from webhistory database\n"+e);
		}
		
		return url;
	}
	
	
	public boolean ifExists(String title){
		
		boolean flag = false;
		double match1 =0.0, match2 =0.0;
		String query = "select url_title from tabs_info";
		try {
			st=c.createStatement();
			rs=st.executeQuery(query);
			while(rs.next()){
				if((match1 = compareStrings(rs.getString(1),title)) >= 0.90){
					
					if(match1 >= match2){
						match2 = match1;
						flag = true;
					}
					
				}
			}
			rs.close();
			
		}
		catch (SQLException e) {
		 	
			JOptionPane.showMessageDialog(null, "Unable to retrieve from webhistory database\n"+e);
		}
		return flag;
	}
	
	public double compareStrings(String stringA, String stringB) {
	    JaroWinkler algorithm = new JaroWinkler();
	    return algorithm.getSimilarity(stringA, stringB);
	}
	
}



