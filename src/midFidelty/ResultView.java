package midFidelty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.sql.Connection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.SimpleTimePeriod;

import com.mysql.jdbc.Statement;


/*
 * @author: Mujtaba Ali
 */
public class ResultView {
	private ResultSet r,t,g;
	private Connection c;
	private Statement st;
	public double teachTime;
	public double researchTime;
	public double grayTime;
	private String date;
	
	
	public ResultView(){
		teachTime = 6;
		researchTime = 2.5;
		grayTime = 3.3;
		
		Date date2 = new Date();
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
		System.out.println();
		date = ft.format(date2);
	//	Communication com = new Communication();
	//	r = com.getTimeInterval("R");
	//	t = com.getTimeInterval("T");
	//	g = com.getTimeInterval("G");
		
	}
	
	public void getResult(){
		Connector con = new Connector();
		c = con.connectDataBase();
		
		String query = "select startTime, endTime from window natural join date natural join time where type='"+"R"+"' and date='"+date+"'";
		try {
			st=(Statement) c.createStatement();
			r=st.executeQuery(query);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		query = "select startTime, endTime from window natural join date natural join time where type='"+"T"+"' and date='"+date+"'";
		try {
			st=(Statement) c.createStatement();
			t=st.executeQuery(query);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		query = "select startTime, endTime from window natural join date natural join time where type='"+"O"+"' and date='"+date+"'";
		try {
			st=(Statement) c.createStatement();
			g=st.executeQuery(query);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getTeachTime() throws SQLException{
		int teach=0;
		while(t.next()){
			if(t.getInt(1) < t.getInt(2)){
				teach += t.getInt(2) - t.getInt(1);
			}
		}
		teachTime = (float)teach/3600.0;
		return teach;
	}
	
	public int getResearchTime() throws SQLException{
		int res=0;
		while(r.next()){
			if(r.getInt(1) < r.getInt(2)){
				res += r.getInt(2) - r.getInt(1);
			}
		}
		
		researchTime = (float)res/3600.0;
		return res;
	}
	
	public int getGrayTime() throws SQLException{
		int gray=0;
		while(g.next()){
			if(g.getInt(1) < g.getInt(2)){
				gray += g.getInt(2) - g.getInt(1);
			}
		}
		grayTime = (float)gray/3600.0;
		return gray;
	}
	
	 private JFreeChart createChart(final IntervalCategoryDataset dataset) {
	        final JFreeChart chart = ChartFactory.createGanttChart(
	            "",  // chart title
	            "Activity",          // domain axis label
	            "Time",              // range axis label
	            dataset,             // data
	            false,                // include legend
	            true,                // tooltips
	            false                // urls
	        );    
//	        chart.getCategoryPlot().getDomainAxis().setMaxCategoryLabelWidthRatio(10.0f);
	        return chart;    
	    }
	 
	 
	 public static IntervalCategoryDataset createDataset() {
		 /*
		         final TaskSeries s1 = new TaskSeries("Scheduled");
		         s1.add(new Task("Research",
		                new SimpleTimePeriod(date(1, Calendar.HOUR),
		                		date(2, Calendar.HOUR))));
		         s1.add(new Task("Teaching",
		                new SimpleTimePeriod(date(2, Calendar.HOUR),
		                		date(3, Calendar.HOUR))));
		         s1.add(new Task("Other",
		                new SimpleTimePeriod(date(3, Calendar.HOUR),
		                		date(4, Calendar.HOUR))));
		         
		     	*/
		         final TaskSeries s2 = new TaskSeries("Actual");
		         
		         
		         s2.add(new Task("Research",
		                new SimpleTimePeriod(date(8, Calendar.HOUR),
		             		   				date(10, Calendar.HOUR))));
		         
		         
		         s2.add(new Task("Teaching",
		                new SimpleTimePeriod(date(10, Calendar.HOUR),
		                 					date(11, Calendar.HOUR))));
		         
		         
		         s2.add(new Task("Other",
		                new SimpleTimePeriod(date(11, Calendar.HOUR),
		                                     date(14, Calendar.HOUR))));
		        
		         
		         
		         
		         final TaskSeriesCollection collection = new TaskSeriesCollection();
		       //  collection.add(s1);
		         collection.add(s2);

		        
		         
		         return collection;
		     }
	 
	 private static Date date(final int hour, final int hourType) {

	        final Calendar calendar = Calendar.getInstance();
	        calendar.set(hourType, hour);
	        final Date result = calendar.getTime();
	        return result;

	    }
	
	
	
	
	
	
	public JFreeChart generateGanttChart(){
		JFreeChart chart=null;
		IntervalCategoryDataset dataset = createDataset();
        chart = createChart(dataset);
		
		return chart;
	}
	
	public JFreeChart generatePieChart() {
		DefaultPieDataset dataSet = new DefaultPieDataset();
		dataSet.setValue("Teaching", teachTime);
		dataSet.setValue("Research", researchTime);
		dataSet.setValue("Other", grayTime);
		

		JFreeChart chart = ChartFactory.createPieChart(
				"Activity Time", dataSet, true, true, false);

		return chart;
	}

	
	public JFreeChart generateBarChart() {
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		dataSet.setValue(teachTime, "", "Teaching");
		dataSet.setValue(researchTime, "", "Reseach");
		dataSet.setValue(grayTime, "", "Other");
		

		JFreeChart chart = ChartFactory.createBarChart(
				"Activity Time", "Activity Nature", "Time in hours",
				dataSet, PlotOrientation.VERTICAL, false, true, true);

		return chart;
	}
}
