import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.util.ShapeUtilities;



public class CoordinateSystem extends ApplicationFrame
{
    final XYSeries node1 = new XYSeries( "node1" );   
	public CoordinateSystem( String applicationTitle, String chartTitle )
	{
	      super(applicationTitle);
	      JFreeChart xylineChart = ChartFactory.createXYLineChart(
	         chartTitle ,
	         "Category" ,
	         "Score" ,
	         createDataset() ,
	         PlotOrientation.VERTICAL ,
	         true , true , false);
	         
	      ChartPanel chartPanel = new ChartPanel( xylineChart );
	      chartPanel.setPreferredSize( new java.awt.Dimension( 1000 , 600 ) );
	      final XYPlot plot = xylineChart.getXYPlot( );
	      XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
	      renderer.setSeriesPaint( 0 , Color.GREEN );
	      renderer.setSeriesPaint( 1 , Color.GREEN );
	      renderer.setSeriesPaint( 2 , Color.GREEN );
	      renderer.setSeriesPaint( 3 , Color.GREEN );
	      renderer.setSeriesPaint( 4 , Color.WHITE );
	      renderer.setSeriesPaint( 5 , Color.WHITE );
	      Shape cross = ShapeUtilities.createDiagonalCross(3, 1);
	      renderer.setSeriesStroke( 0 , new BasicStroke( 4.0f ) );
	      renderer.setSeriesStroke( 1 , new BasicStroke( 4.0f ) );
	      renderer.setSeriesStroke( 2 , new BasicStroke( 4.0f ) );
	      renderer.setSeriesStroke( 3 , new BasicStroke( 4.0f ) );

	      plot.setRenderer( renderer ); 
	      setContentPane( chartPanel ); 
	   }
	   
	   private XYDataset createDataset( )
	   {
       
	      node1.add( 0.0 , 0.0 );          

	      final XYSeries node2 = new XYSeries( "node2" );          
	      node2.add( 400.0 , 0.0 );          
       
	      final XYSeries node3 = new XYSeries( "node3" );          
	      node3.add( 0.0 , 400.0 );          
         
	      final XYSeries node4 = new XYSeries( "node4" );          
	      node4.add( 400.0 , 400.0 ); 
	      
	      final XYSeries node5 = new XYSeries( "node5" );          
	      node5.add( -50.0 , -50.0 );                 
	      final XYSeries node6 = new XYSeries( "node6" );          
	      node6.add( 450.0 , 450.0 );          
       
	      
	      
	      final XYSeriesCollection dataset = new XYSeriesCollection( );          
	      dataset.addSeries( node1 );          
	      dataset.addSeries( node2 );          
	      dataset.addSeries( node3 );
	      dataset.addSeries( node4 );
	      dataset.addSeries( node5 );
	      dataset.addSeries( node6 );
	      return dataset;
	   }

	   void start()
	   {
		       

	   }
	   void insertNode()
	   {
		   
	   }
	   
	   void delete()
	   {
		   node1.remove(0);
	   }


}
