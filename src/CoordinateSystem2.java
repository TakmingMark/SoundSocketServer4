import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.Random;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;

import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ShapeUtilities;

public class CoordinateSystem2 extends ApplicationFrame {

	XYSeries seriesNode0;
	XYSeries seriesNode1;
	XYSeries seriesNode2;
	XYSeries seriesNode3;
	XYSeries seriesNode4;
	JLabel JLnode0,JLnode1, JLnode2, JLnode3, JLnode4;
	JTextArea TAcontent;
	JScrollPane SPcontent;
	
	JComboBox<String> CBuser;
	JPanel jpanel;
	String message;
	public CoordinateSystem2(String applicationTitle) {
		super(applicationTitle);
		jpanel = createDemoPanel();
		jpanel.setPreferredSize(new Dimension(1024, 768));
		setLayout(new GridBagLayout());

		add(jpanel, GridBagLayoutPosition(0,0,3,1,0,4,GridBagConstraints.NONE,GridBagConstraints.WEST));

		JLnode0 = new JLabel("Taget node coordinate:");
		JLnode1 = new JLabel("distance-Node1:");
		JLnode2 = new JLabel("distance-Node2:");
		JLnode3 = new JLabel("distance-Node3:");
		JLnode4 = new JLabel("distance-Node4:");
		TAcontent=new JTextArea("");
		SPcontent=new JScrollPane(TAcontent,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		CBuser=new JComboBox<String>();
		
		JLnode0.setFont(new Font("Serif", Font.PLAIN, 20));
		JLnode1.setFont(new Font("Serif", Font.PLAIN, 20));
		JLnode2.setFont(new Font("Serif", Font.PLAIN, 20));
		JLnode3.setFont(new Font("Serif", Font.PLAIN, 20));
		JLnode4.setFont(new Font("Serif", Font.PLAIN, 20));
		TAcontent.setFont(new Font("Serif", Font.PLAIN, 20));
		
		JLnode0.setForeground(Color.red);
		JLnode1.setForeground(Color.blue);
		JLnode2.setForeground(Color.blue);
		JLnode3.setForeground(Color.blue);
		JLnode4.setForeground(Color.blue);
		TAcontent.setForeground(Color.blue);
		
		add(JLnode0, GridBagLayoutPosition(0,1,1,1,1,0,GridBagConstraints.NONE,GridBagConstraints.WEST));
		add(JLnode1, GridBagLayoutPosition(0,2,1,1,0,0,GridBagConstraints.NONE,GridBagConstraints.WEST));
		add(JLnode2, GridBagLayoutPosition(0,3,1,1,0,0,GridBagConstraints.NONE,GridBagConstraints.WEST));
		add(JLnode3, GridBagLayoutPosition(0,4,1,1,0,0,GridBagConstraints.NONE,GridBagConstraints.WEST));
		add(JLnode4, GridBagLayoutPosition(0,5,1,1,0,0,GridBagConstraints.NONE,GridBagConstraints.WEST));
		add(CBuser, GridBagLayoutPosition(1,1,1,1,2,0,GridBagConstraints.BOTH,GridBagConstraints.WEST));
	}

	public JPanel createDemoPanel() {
		JFreeChart jfreechart = ChartFactory.createScatterPlot("Coordinate Plot", "X", "Y", samplexydataset2(),
				PlotOrientation.VERTICAL, true, true, false);
		Shape cross = ShapeUtilities.createDiagonalCross(3, 1);
		XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
		xyPlot.setDomainCrosshairVisible(true);
		xyPlot.setRangeCrosshairVisible(true);
		XYItemRenderer renderer = xyPlot.getRenderer();

		renderer.setSeriesShape(0, cross);
		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesShape(1, cross);
		renderer.setSeriesPaint(1, Color.blue);
		renderer.setSeriesShape(2, cross);
		renderer.setSeriesPaint(2, Color.blue);
		renderer.setSeriesShape(3, cross);
		renderer.setSeriesPaint(3, Color.blue);
		renderer.setSeriesShape(4, cross);
		renderer.setSeriesPaint(4, Color.blue);
		
		NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
		domain.setRange(-10.00, 410.00);
		domain.setTickUnit(new NumberTickUnit(20));

		NumberAxis range = (NumberAxis) xyPlot.getRangeAxis();
		range.setRange(-10.0, 410.0);
		range.setTickUnit(new NumberTickUnit(20));

		ChartPanel chartPanel = new ChartPanel(jfreechart);

		return chartPanel;
	}

	private XYDataset samplexydataset2() {

		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();

		seriesNode0 = new XYSeries("TargetNode");

		seriesNode1 = new XYSeries("Node1");
		seriesNode2 = new XYSeries("Node2");
		seriesNode3 = new XYSeries("Node3");
		seriesNode4 = new XYSeries("Node4");

		xySeriesCollection.addSeries(seriesNode0);
		xySeriesCollection.addSeries(seriesNode1);
		xySeriesCollection.addSeries(seriesNode2);
		xySeriesCollection.addSeries(seriesNode3);
		xySeriesCollection.addSeries(seriesNode4);
		return xySeriesCollection;
	}

	public void addData(int role,int nodeX,int nodeY) 
	{
		switch(role)
		{
			case 0:
				seriesNode0.clear();
				seriesNode0.add(nodeX, nodeY);
				break;
			case 1:
				seriesNode1.clear();
				seriesNode1.add(nodeX, nodeY);
				break;
			case 2:
				seriesNode2.clear();
				seriesNode2.add(nodeX, nodeY);
				break;
			case 3:
				seriesNode3.clear();
				seriesNode3.add(nodeX, nodeY);
				break;
			case 4:
				seriesNode4.clear();
				seriesNode4.add(nodeX, nodeY);
				break;
		}

			

	}

	public void removeData() {
		seriesNode0.remove(0);
	}
	
	public void writeJLabel(String role,String nodeX,String nodeY,int distance)
	{
		switch(role)
		{
			case "0":
				JLnode0.setText("TargetNode:("+nodeX+","+nodeY+")");
				message=message+"TargetNode:"+"("+nodeX+","+nodeY+")";
				break;
			case "1":
				JLnode1.setText("distance-Node1("+nodeX+","+nodeY+"):"+distance);
				message=message+"node1D:"+distance+",";
				break;
			case "2":
				JLnode2.setText("distance-Node2("+nodeX+","+nodeY+"):"+distance);
				message=message+"node2D:"+distance+",";
				break;
			case "3":
				JLnode3.setText("distance-Node3("+nodeX+","+nodeY+"):"+distance);
				message=message+"node3D:"+distance+",";
				break;
			case "4":
				JLnode4.setText("distance-Node4("+nodeX+","+nodeY+"):"+distance);
				message=message+"node4D:"+distance+",";
				break;
		}
	}

	public void writeComboBox(String no)
	{
		message="room:"+no+","+message;
		CBuser.addItem(message);
		message="";
	}
	
	private GridBagConstraints GridBagLayoutPosition(int gridX,int gridY,int gridWidth,int gridHeight,double gridWeightX,double gridWeightY,int fill,int anchor)
	{
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = gridX;
		gridBagConstraints.gridy = gridY;
		gridBagConstraints.gridwidth = gridWidth;
		gridBagConstraints.gridheight = gridHeight;
		gridBagConstraints.weightx = gridWeightX;
	    gridBagConstraints.weighty = gridWeightY;
	    gridBagConstraints.fill = fill;
	    gridBagConstraints.anchor = anchor;
		return gridBagConstraints;
	}

}