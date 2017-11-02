
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import org.jfree.ui.RefineryUtilities;

import com.mathworks.toolbox.javabuilder.MWException;
import java.sql.*;

import MatlabSubFunction.SoundAnalysis;



public class SocketServer {
	
	/*------------Layout Panel-------------*/
	JFrame fame;
	JPanel panel;
	JLabel LBmsg;
	JTextField TFmsg;
	JLabel LBIpAds;
	JTextArea TAcontent;
	JScrollPane SPcontent;
	JButton BTdisconnectAll;
	JButton BTdisconnectUser;
	JButton BTcoordinateSystem;
	JComboBox<String> CBuser;
	
	CoordinateSystem2 coordinateSystem;
	/*------------Use Variable-------------*/
	public static final int LISTEN_PORT = 2525;
	//define store all CrazyitMap of Socket
	public static CrazyitMap<String,DataOutputStream> clients=new CrazyitMap<>();
	
	// In order to know how many people in a group
	public static ArrayList<SocketRoom> socketRoomArray=new ArrayList<SocketRoom>();
	
	//current room sum
	public static int roomSum=0;
	
	// matlab object, calculate and analysis sound file
	public SoundAnalysis soundAnalysis;
	
	//小數點後幾位
	public final static DecimalFormat df=new DecimalFormat("####");

	//parameter
	double temperature=23;
	double soundSpeed=331+0.6*(temperature);
	double weightedDistance=0.06; 
	//JDBC connection object
	public Connection connection=null;

	public boolean coordinateSystemControl=false;
	public static void main (String[] args)
	{
		SocketServer socketServer=new SocketServer();
		socketServer.initLayout();
		socketServer.setIPAdrress();
		socketServer.initListener();
	    socketServer.initObject();
	    socketServer.initJDBC();
	    socketServer.serverReceiver();
	}
	
	/**
	 * initial Layout object
	 */
	public void initLayout() 
	{	
		fame=new JFrame("Socket Server");
		panel=new JPanel(null);//setLayout=null
		LBmsg=new JLabel("由此傳送訊息：");
		TFmsg=new JTextField();
		LBIpAds=new JLabel("IP Address:");
		TAcontent=new JTextArea("");
		SPcontent=new JScrollPane(TAcontent,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		BTdisconnectAll=new JButton("斷開所有用戶");
		BTdisconnectUser=new JButton("斷開所選用戶");
		BTcoordinateSystem=new JButton("顯示坐標系");
		CBuser=new JComboBox<String>();
		
	    DefaultCaret caret = (DefaultCaret)TAcontent.getCaret(); //得到TAcontent的caret，能設置往下滾動
	    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    
		fame.setBounds(0,0,600,500);
		fame.setVisible(true);
		fame.setResizable(false);
		LBmsg.setLocation(10,10);
		LBmsg.setSize(100,30);
		TFmsg.setLocation(110,10);
		TFmsg.setSize(450,30);
		LBIpAds.setLocation(10,50);
		LBIpAds.setSize(400,30);
		TAcontent.setLineWrap(true);
		SPcontent.setLocation(10,90); 
		SPcontent.setSize(400,350);
		BTdisconnectAll.setLocation(420,90);
		BTdisconnectAll.setSize(150,50);
		BTdisconnectUser.setLocation(420,150);
		BTdisconnectUser.setSize(150,50);
		BTcoordinateSystem.setLocation(420,260);
		BTcoordinateSystem.setSize(150,50);
		CBuser.setLocation(420,220);
		CBuser.setSize(150, 20);

		panel.add(LBmsg);
		panel.add(TFmsg);
		panel.add(LBIpAds);
		panel.add(SPcontent);
		panel.add(BTdisconnectAll);
		panel.add(BTdisconnectUser);
		panel.add(BTcoordinateSystem);
		panel.add(CBuser);

		fame.add(panel);
		fame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		fame.revalidate() ;	
	}
	
	/**
	 * set Server self IP
	 */
	public void setIPAdrress()
	{
		InetAddress adr = null;
		try 
		{
			adr = InetAddress.getLocalHost(); //IPv6
			adr =Inet4Address.getLocalHost();//IPv4
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LBIpAds.setText("IP Address:"+adr.getHostAddress());
	}
	
	/**`
	 * initial object listener
	 */
	public void initListener()
	{
		TFmsg.addKeyListener(new TFmsgKeyListener());
		BTdisconnectUser.addActionListener(new BTdisconnectUserListener());
		BTdisconnectAll.addActionListener(new BTdisconnectAllListener());
		BTcoordinateSystem.addActionListener(new BTcoordinateSystemListener());
		
	}
	
	/**
	 * Starting listen client connect server 
	 */
	public void serverReceiver()
    {
        ServerSocket serverSocket = null;
        ExecutorService threadExecutor = Executors.newCachedThreadPool(); //建立Thread Pool，讓Client可以連進來不斷線
        try
        {
            serverSocket = new ServerSocket( LISTEN_PORT );
          	TAcontent.append("Server listening requests..."+"\r\n");
            while ( true )// 不斷接收來自網路客戶端的連接請求
            {
                Socket socket = serverSocket.accept();
                threadExecutor.execute( new ServerThread( socket ) ); 
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            TAcontent.append("Server failed to start,whether the "+LISTEN_PORT+" port has been occupied?");
        }
        finally
        {
            if ( threadExecutor != null )
                threadExecutor.shutdown();
            if ( serverSocket != null )
                try
                {
                    serverSocket.close();
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
        }
    }
	
	/**
	 *initial object 
	 */
	private void initObject()
	{
		coordinateSystem = new CoordinateSystem2("CoordinateSystem");
        coordinateSystem.pack();
        RefineryUtilities.centerFrameOnScreen(coordinateSystem);
        
		try {
			soundAnalysis=new SoundAnalysis();	
		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * initial JDBC
	 */
	private void initJDBC()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connection=DriverManager.getConnection("jdbc:mysql://120.127.14.91:3306/soundposition","root","110432008");
		}
		catch(SQLException e)
		{
			System.out.println("initJDBC SQLException:"+e.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("initJDBC ClassNotFoundException:"+e.toString());
		}
	}
	
	/**
     * 監聽TFmsgKey，來處理Server端 User想要發送訊息。
     */
	class TFmsgKeyListener implements KeyListener
	{
		@Override
		public void  keyReleased(KeyEvent event)
		{
			if(event.getKeyCode()==10)
			{
				TAcontent.append("Server:"+TFmsg.getText()+"\t\n");
				new Thread(new ServerSentThread(TFmsg.getText())).start();;
				TFmsg.setText("");
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	/**
     * 監聽BTdisconnectUser，來處理Server端 對某個client斷線。
     */
	class BTdisconnectUserListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String userKey=(String) CBuser.getSelectedItem();			
			DataOutputStream userOutput=clients.getValueByKey(userKey);
			try 
			{
				if(userOutput!=null)
					userOutput.close();
			} catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		
	}
	
	/**
     * 監聽BTdisconnectAll，來處理Server端 對全部client斷線。
     */
	class BTdisconnectAllListener implements ActionListener
	{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			for(DataOutputStream userOutput:clients.valueSet())
			{
				try 
				{
					if(userOutput!=null)
						userOutput.close();
				} catch (IOException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
	}
	
	/**
     * 監聽BTcoordinateSystem
     */
	class BTcoordinateSystemListener implements ActionListener
	{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
			if(coordinateSystemControl==false)
			{
				  coordinateSystem.setVisible(true);
				  coordinateSystem.repaint();
				  coordinateSystem.invalidate();
				  coordinateSystemControl=true;
			}
			else if(coordinateSystemControl==true)
			{
				  coordinateSystem.setVisible(false);
			      coordinateSystemControl=false;
			}

		}
		
	}
    
    /**
     * 處理Client端的連接後不間斷執行緒，每一個client都有一個runnable thread。
     */
    class ServerThread implements Runnable
    {
    	Socket clientSocket;
        DataOutputStream output = null;
        DataInputStream input=null;
        String line="";
        String userAddress;
        int heartBeatTime=30;
        
    	public ServerThread(Socket clientSocket)
        {
    		try 
    		{
    			this.clientSocket=clientSocket;
    			clientSocket.setSoTimeout(60000); //設置，如果client端沒傳送心跳過來，則60秒後自動斷線

    			input=new DataInputStream(this.clientSocket.getInputStream());
				output = new DataOutputStream( this.clientSocket.getOutputStream());
			} catch (IOException e) 
    		{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    	 
		@Override
		public synchronized void run()  
		{
			// TODO Auto-generated method stub
			try
			{
				while((line=readFromClient(input))!=null)
				{
					//使用者開啟application，需要做登入動作
					if(line.startsWith(SocketProtocol.C_USER_ROUND) && line.endsWith(SocketProtocol.C_USER_ROUND))
					{
						//第一次進入SocketServer，新增該使用者
						String userName=getRealMsg(line);
						userAddress=userName;
						if(clients.map.containsKey(userName))
						{
							TAcontent.append("C_USER_ROUND:"+userName+"該Client重複\r\n");
							String message=SocketProtocol.S_NAME_REP;
							transmitterClientByValue(output,message);
						}
						else
						{
							TAcontent.append("C_USER_ROUND:"+clientSocket.getRemoteSocketAddress() +"連線進來!"+"\r\n");
							clients.put(userName,output);
							updateCBuser(userName,"add");
							String message=SocketProtocol.S_LOGIN_SUCCESS;
							transmitterClientByValue(output,message);
							transmitterBroadcastToClient(combineUserIP());
						}
					}
					//client發送私人訊息給其他client
					else if(line.startsWith(SocketProtocol.C_PRIVATE_ROUND) && line.endsWith(SocketProtocol.C_PRIVATE_ROUND))
					{
						//使用者對使用者發送私人訊息
						TAcontent.append("C_PRIVATE_ROUND:"+line+"\r\n");
						String userAndMsg=getRealMsg(line);
						String user=userAndMsg.split("\\"+SocketProtocol.SPLIT_SIGN)[0];
						String msg=userAndMsg.split("\\"+SocketProtocol.SPLIT_SIGN)[1];
						
						//這段有點奇怪，我覺得無法確定output是發射者，需要再修改
						TAcontent.append("S_PRIVATE_ROUND:"+clients.getKeyByValue(output)+">"+user+":"+msg+"\r\n");
						msg=clients.getKeyByValue(output)+msg;
						transmitterClientByKey(user,msg);
					}
					//client發送訊息給全部client(看有沒有人在，這裡可以改成有在wifi點附近的人才回應給server)
					else if(line.startsWith(SocketProtocol.C_FIND_ASSISTANT_AND_TRANSMIT) && line.endsWith(SocketProtocol.C_FIND_ASSISTANT_AND_TRANSMIT))
					{
						TAcontent.append("_______________________next_________________________"+"\r\n\r\n");
						
						//因為還沒做WIFI定位，所以不知道周遭有誰，所以先把IP都新增進去
						SocketRoom socketRoom =new SocketRoom();//新增一個房間
						socketRoom.setRoomID(String.valueOf(roomSum));//放置房間ID，我採取是每次都+1機制
						int clientSortNo=0;// 數全部client有多少人
						
						//幫房間內的使用者建立編號
						for(String clientIP:clients.keySet())
						{
							clientSortNo++;
							String clientFileAudioNo=String.valueOf(roomSum)+String.valueOf(clientSortNo);//每個client端的audio file都是房間名字+排列編號
							socketRoom.setRoomClientArray(clientIP,clientFileAudioNo,clientSortNo);
						}
						
						//放置人數總額
						socketRoom.setRoomClientSum(clientSortNo);
						//放置房間陣列裡
						socketRoomArray.add(socketRoom);
						//房間編號移入下一個
						roomSum+=1;
						//把PUBLIC_BROADCAST 廣播來的訊息 拆開
						String message=getRealMsg(line);
						TAcontent.append("C_FIND_ASSISTANT_AND_TRANSMIT:"+message+"\r\n");
						
						for(int i=0;i<socketRoom.getRoomClientSum();i++)
						{
							String clientIP=socketRoom.getRoomClientIPArray().get(i).clientIP;
							String msg=SocketProtocol.S_GIVEN_ROOMID_AND_NO+
									message+SocketProtocol.SPLIT_SIGN+
									socketRoom.getRoomID()+SocketProtocol.SPLIT_SIGN+
									socketRoom.getRoomClientIPArray().get(i).clientFileAudioNo+
									SocketProtocol.S_GIVEN_ROOMID_AND_NO; //前面用過message，重複用會有問題
		
							transmitterClientByKey(clientIP,msg);
							
							TAcontent.append("S_GIVEN_ROOMID_AND_NO:"+msg+"\r\n");
							//S_GIVEN_ROOMID_AND_NO:(*IP:/10.105.4.31:59904 roomID:0 soundRank:01C_FIND_ASSISTANT_AND_TRANSMIT|1|11(*
						}				
					}
					else if(line.startsWith(SocketProtocol.C_USER_START_SOUND_RECORD) && line.endsWith(SocketProtocol.C_USER_START_SOUND_RECORD))
					{
						String message=getRealMsg(line);
						TAcontent.append("C_USER_START_SOUND_RECORD:"+message+"\r\n");
						//C_USER_START_SOUND_RECORD1|/10.105.4.31:59904|0|0|0
						
						String roomID=message.split("\\"+ SocketProtocol.SPLIT_SIGN)[0];
						String clientIP=message.split("\\"+ SocketProtocol.SPLIT_SIGN)[1];
						String role=message.split("\\"+ SocketProtocol.SPLIT_SIGN)[2];
						String nodeX=message.split("\\"+ SocketProtocol.SPLIT_SIGN)[3];
						String nodeY=message.split("\\"+ SocketProtocol.SPLIT_SIGN)[4];
						
						synchronized(socketRoomArray) 
						{
							for(SocketRoom socketRoom:socketRoomArray)
							{
								if(socketRoom.getRoomID()==roomID || socketRoom.getRoomID().equals(roomID))
								{
									socketRoom.soundRecordSum+=1; //集滿房間人數的回應
									message=SocketProtocol.S_RETURN_NODE_POSITION+
											role+SocketProtocol.SPLIT_SIGN+
											nodeX+SocketProtocol.SPLIT_SIGN+
											nodeY+
											SocketProtocol.S_RETURN_NODE_POSITION;
									for(int i=0;i<socketRoom.getRoomClientIPArray().size();i++) //房間內使用者人數總額
									{
										String NoticeClientIP=socketRoom.getRoomClientIPArray().get(i).clientIP;
										transmitterClientByKey(NoticeClientIP,message);
										if(socketRoom.getRoomClientIPArray().get(i).clientIP==clientIP || socketRoom.getRoomClientIPArray().get(i).clientIP.equals(clientIP))
										{
											socketRoom.getRoomClientIPArray().get(i).role=role;
											socketRoom.getRoomClientIPArray().get(i).nodeX=nodeX;
											socketRoom.getRoomClientIPArray().get(i).nodeY=nodeY;	
											
											if(role!="0" && !role.equals("0"))
											{
												coordinateSystem.addData(Integer.parseInt(role),Integer.parseInt(nodeX),Integer.parseInt(nodeY));
											}

										}
									}
									
									if(socketRoom.soundRecordSum==socketRoom.getRoomClientSum())//累積使用者新增數量已經等於房間內的使用者總額
									{
										long start_long=System.nanoTime();
										Thread.sleep(400);
										long end_long=System.nanoTime();
										System.out.println("wait time:"+String.valueOf((end_long-start_long)/1000000000.0)+"s");
	
										TAcontent.append("0到底近來幾次:userAddress"+userAddress+"\r\n");
										//先找尋編號為1的client，讓他先發射第一個訊號
										for(int i=0;i<socketRoom.getRoomClientIPArray().size();i++)
										{
											if(socketRoom.getRoomClientIPArray().get(i).clientNo==1 &&socketRoom.getRoomClientIPArray().get(i).whetherTransmit==false)
											{
												clientIP=socketRoom.getRoomClientIPArray().get(i).clientIP;
												message=SocketProtocol.S_USER_START_SOUND_TRACK+socketRoom.getRoomClientIPArray().get(i).clientIP+SocketProtocol.S_USER_START_SOUND_TRACK;
												transmitterClientByKey(clientIP,message);
												
												TAcontent.append("S_USER_START_SOUND_TRACK"+message+"\r\n");
												//S_USER_START_SOUND_TRACK##/10.105.4.31:59904##
												socketRoom.getRoomClientIPArray().get(i).whetherTransmit=true;//把發射過的使用者作記號
												socketRoom.clientTransmitterSum=1; //總和+1
												TAcontent.append("1到底近來幾次:"+i+"userAddress"+userAddress+"\r\n");
												break; //找到就不會有下筆，直接跳走
											}
										}
									}
								}
							}
						}
					}
					else if(line.startsWith(SocketProtocol.C_USER_END_SOUND_TRACK)&& line.endsWith(SocketProtocol.C_USER_END_SOUND_TRACK))
					{
						String message=getRealMsg(line);
						TAcontent.append("C_USER_END_SOUND_TRACK:"+message+"\r\n");
						//C_USER_END_SOUND_TRACK:roomID
						String roomID=message;
						synchronized(socketRoomArray) 
						{
							for(SocketRoom socketRoom:socketRoomArray) 
							{
								if(socketRoom.getRoomID()==roomID || socketRoom.getRoomID().equals(roomID))//找房間
								{
									for(int i=0;i<socketRoom.getRoomClientIPArray().size();i++) //找使用者
									{
										
										//先看要不要停止錄製
										if(socketRoom.clientTransmitterSum==socketRoom.getRoomClientIPArray().size())
										{
											System.out.println("clientTransmitterSum:"+socketRoom.clientTransmitterSum);
											//可以告訴使用者們 停止錄製檔案了!!
											message=SocketProtocol.S_USER_STOP_SOUND_RECORD+socketRoom.getRoomID()+
													SocketProtocol.S_USER_STOP_SOUND_RECORD;
											
											long start_long=System.nanoTime();
											Thread.sleep(400);
											long end_long=System.nanoTime();
											System.out.println("wait time:"+String.valueOf((end_long-start_long)/1000000000.0)+"s");
											
											for(RoomClientParameter roomUserParameter:socketRoom.getRoomClientIPArray())
											{
												String clientIP=roomUserParameter.clientIP;
												transmitterClientByKey(clientIP,message);
												TAcontent.append("S_USER_STOP_SOUND_RECORD:"+message+"\r\n");
												//S_USER_STOP_SOUND_RECORD:%%1%%
											}
											break; //已經到達房間人數了，所以跳走找使用者for迴圈
										}
										
									    //如果還不用停止，代表還有使用者還沒發射，找沒發射過的使用者，並找上一位的clientNo+1(有序
										if(socketRoom.getRoomClientIPArray().get(i).whetherTransmit==false && 
												socketRoom.getRoomClientIPArray().get(i).clientNo==(socketRoom.clientTransmitterSum+1))
										{
											String clientIP=socketRoom.getRoomClientIPArray().get(i).clientIP;
											
												long start_long=System.nanoTime();
												Thread.sleep(400);
												long end_long=System.nanoTime();
												System.out.println("wait time:"+String.valueOf((end_long-start_long)/1000000000.0)+"s");
												
												//輪流發射聲音
												message=SocketProtocol.S_USER_START_SOUND_TRACK+
														socketRoom.getRoomClientIPArray().get(i).clientIP+
														SocketProtocol.S_USER_START_SOUND_TRACK;
												transmitterClientByKey(clientIP,message);
												TAcontent.append("S_USER_START_SOUND_TRACK:"+message+"\r\n");
												socketRoom.getRoomClientIPArray().get(i).whetherTransmit=true;
												socketRoom.clientTransmitterSum+=1;
												break; //找到人發射之後，就跳走迴圈，然後等下一個人發射訊號
											
										}
									}
								}
							}
						}
						
					}
					else if(line.startsWith(SocketProtocol.C_FILE_UPLOAD_COMPLETED) && line.endsWith(SocketProtocol.C_FILE_UPLOAD_COMPLETED))
					{
						String message=getRealMsg(line);
						TAcontent.append("FILE_UPLOAD_COMPLETED="+message+"\r\n");
						String roomID=message.split("\\"+ SocketProtocol.SPLIT_SIGN)[0];
						String clientIP=message.split("\\"+ SocketProtocol.SPLIT_SIGN)[1];
						int needDeleteRoomNo=-1;  //新增這個變數，是為了避免刪除Array時，再一次跑進圍圈造成IndexOutOfBoundsException
						//跑目前房間總數
						System.out.println("socketRoomArray.size():"+socketRoomArray.size());
						
						for(int i=0;i<socketRoomArray.size();i++)
						{
							//找尋socketRoomArray ID與接收使用者房間ID做比較
							if(socketRoomArray.get(i).getRoomID()==roomID || socketRoomArray.get(i).getRoomID().equals(roomID))
							{
								//跑特定房間裡的使用者總數
								for(int j=0;j<socketRoomArray.get(i).getRoomClientIPArray().size();j++)
								{
									//特定房間的使用者IP與接收使用者IP做比較
									if(socketRoomArray.get(i).getRoomClientIPArray().get(j).clientIP==clientIP ||
											socketRoomArray.get(i).getRoomClientIPArray().get(j).clientIP.equals(clientIP))
									{
										//當接收使用者數量與房間內使用者數量一樣，就可以計算
										if(socketRoomArray.get(i).recordCompletedClient()==true)
										{
											TAcontent.append("終於可以計算聲音了...\r\n");
											needDeleteRoomNo=i;
											ArrayList<int[]> userPastSimplesArray=new ArrayList<int[]>();
											ArrayList<int[]> userDistanceArray=new ArrayList<int[]>();
											
											//把房間裡的client端 audio file計算出來	
											long startTime=System.nanoTime();
											userPastSimplesArray=findUserPastSimples(socketRoomArray.get(i));
											userDistanceArray=calActualDistance(userPastSimplesArray);
											long endTime=System.nanoTime();
											System.out.println("matlab cal time:"+String.valueOf((endTime-startTime)/1000000000.0)+"s");
											
											//回傳給有參與任務的client，先計算距離再找出target node計算座標
											for(int k=0;k<socketRoomArray.get(i).getRoomClientSum();k++)
											{
												clientIP=socketRoomArray.get(i).getRoomClientIPArray().get(k).clientIP;
												String role=socketRoomArray.get(i).getRoomClientIPArray().get(k).role;
												String nodeX=socketRoomArray.get(i).getRoomClientIPArray().get(k).nodeX;
												int clientNo=socketRoomArray.get(i).getRoomClientIPArray().get(k).clientNo;
												int[] userDistance = null;
												
												for(int[] parameter:userDistanceArray)//把計算結果，分送給使用者
												{
													if(parameter[clientNo-1]==0)
													{
														userDistance=parameter;
													}
												}
						
												String S_userDistance=""; 
												String targetNodeDistance="";
												
												for(int parameter:userDistance)
												{
													S_userDistance+=parameter+SocketProtocol.SPLIT_SIGN;
												}
											
												//找與clientIP配的sencondaryIP，然後新增到資料庫
												for(int sencondaryPosition=0;sencondaryPosition<userDistance.length;sencondaryPosition++)
												{
													String sencondaryClientIP=null;
													if(userDistance[sencondaryPosition]!=0)
													{
														for(RoomClientParameter roomClientParameter:socketRoomArray.get(i).getRoomClientIPArray())
														{
															if(roomClientParameter.clientNo==sencondaryPosition+1)
															{
																sencondaryClientIP=roomClientParameter.clientIP;
																
																//找到client是role==0(target node)的身分，把他與其他node所測距記錄下來
																if((role=="0" || role.equals("0")))
																{
																	
																	targetNodeDistance+=roomClientParameter.nodeX+","+roomClientParameter.nodeY+","+userDistance[sencondaryPosition]+SocketProtocol.SPLIT_SIGN;
																	coordinateSystem.writeJLabel(roomClientParameter.role,roomClientParameter.nodeX,roomClientParameter.nodeY,userDistance[sencondaryPosition]);
																	insertMySqlTable(clientIP,sencondaryClientIP,roomID,userDistance[sencondaryPosition]);
																}
															}
														}
														insertMySqlTable(clientIP,sencondaryClientIP,roomID,userDistance[sencondaryPosition]);
													}
												}
												
//												//跑到這，代表target node distance已經記錄完成，將找出其座標(如果要計算距離就好，可以把這段註解)
												
												if(role=="0" || role.equals("0") && socketRoomArray.get(i).getRoomClientSum()>=4 )
												{
													System.out.println("targetNodeDistance:"+targetNodeDistance);
													
													
													//來看看去除某個第幾大的數值，有沒有差別
													
													
													String targetNodeCoordinateD=findTargetNode(targetNodeDistance,false);
													System.out.println("targetNodeCoordinateD:"+targetNodeCoordinateD);
													
													insertErrorItemTable(roomID,4,targetNodeCoordinateD.split("\\"+SocketProtocol.SPLIT_SIGN)[0],targetNodeCoordinateD.split("\\"+SocketProtocol.SPLIT_SIGN)[1]);

													for(int u=0;u<4;u++)
													{
														String targetNodeD=filterTargetNodeDistance(targetNodeDistance,nodeX,u);
														int item;
														if(targetNodeD.split("\\"+SocketProtocol.SPLIT_SIGN)[0]=="0" ||targetNodeD.split("\\"+SocketProtocol.SPLIT_SIGN)[0].equals("0"))
														{
															item=5;
														}
														else 
														{
															item=u;
														}
														targetNodeD=targetNodeD.substring(2);
														System.out.println("targetNodeDistance:"+targetNodeD);
														targetNodeCoordinateD=findTargetNode(targetNodeD,false);
														System.out.println("targetNodeCoordinateD:"+targetNodeCoordinateD);
														
														insertErrorItemTable(roomID,item,targetNodeCoordinateD.split("\\"+SocketProtocol.SPLIT_SIGN)[0],targetNodeCoordinateD.split("\\"+SocketProtocol.SPLIT_SIGN)[1]);

													}
													
													
													
													
													
													targetNodeDistance=filterTargetNodeDistance2(targetNodeDistance,nodeX,0);
													System.out.println("targetNodeDistance:"+targetNodeDistance);
													
													targetNodeCoordinateD=findTargetNode(targetNodeDistance,true);
													System.out.println("targetNodeCoordinateD:"+targetNodeCoordinateD);
													

													
													message=SocketProtocol.S_SUCCESS_CALCULATE_COORDINATE+targetNodeCoordinateD+SocketProtocol.S_SUCCESS_CALCULATE_COORDINATE;
													transmitterClientByKey(clientIP,message);
													
													//新增一筆
													coordinateSystem.writeComboBox(String.valueOf(roomSum));
												}
												
												//如果要算座標時，要註解這段
												//全部節點發送其S_userDistance
//												message=SocketProtocol.SUCCESS_CALCULATE_DISTANCE+S_userDistance+"\r\n"+SocketProtocol.SUCCESS_CALCULATE_DISTANCE;	
//												transmitterClientByKey(clientIP,message);
//												TAcontent.append("SUCCESS_CALCULATE_DISTANCE:"+S_userDistance+"\r\n");
											}
										}
									}
								}
							}
						}
						if(needDeleteRoomNo!=-1)
							socketRoomArray.remove(needDeleteRoomNo);
					}
					else if(line.startsWith(SocketProtocol.C_INSERT_TARGET_NODE_DB) && line.endsWith(SocketProtocol.C_INSERT_TARGET_NODE_DB))
					{
						String message=getRealMsg(line);
						String parimaryIP=message.split("\\"+SocketProtocol.SPLIT_SIGN)[0];
						String room=message.split("\\"+SocketProtocol.SPLIT_SIGN)[1];
						String actualX=message.split("\\"+SocketProtocol.SPLIT_SIGN)[2];
						String actualY=message.split("\\"+SocketProtocol.SPLIT_SIGN)[3];
						String estimateX=message.split("\\"+SocketProtocol.SPLIT_SIGN)[4];
						String estimateY=message.split("\\"+SocketProtocol.SPLIT_SIGN)[5];
						String deviation=message.split("\\"+SocketProtocol.SPLIT_SIGN)[6];
						insertTargetNodeTable(parimaryIP,room,actualX,actualY,estimateX,estimateY,deviation);
					}
					//避免client or server斷線無回應
					else if(line.startsWith(SocketProtocol.C_HEART_BEAT) && line.endsWith(SocketProtocol.C_HEART_BEAT))
					{
						//使用者發回心跳聲
						String message=getRealMsg(line);
						clientSocket.setSoTimeout(60000);
						TAcontent.append(message+"的心跳"+"\r\n");
						message=SocketProtocol.S_HEART_BEAT+"Server response"+SocketProtocol.S_HEART_BEAT;
						transmitterClientByValue(output,message);
					}
					//其他訊息
					else
					{
						//其他訊息
						String message=getRealMsg(line);
						TAcontent.append("else public:"+clients.getKeyByValue(output)+":"+message+"\r\n");
						//跑遍每個clients的輸出串流
						message=SocketProtocol.S_MSG_ROUND+message+SocketProtocol.S_MSG_ROUND;
						
						transmitterBroadcastToClient(message);
					}
				}
			}
			catch (IOException e) 
    		{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally 
	        {
				closeSocketClient();
	        }
		}
		
		/**
		 * read from client DataInputStream data
		 * @param input= client dataInputStream
		 * @return=data input stream converted data
		 */
		private String readFromClient(DataInputStream input)
        {
        	try
        	{
        		return input.readUTF();
        	}
        	catch(IOException e)
        	{
        		
        	}
			return null;
        }
		
		/**
		 * all users stored server save in a variable
		 * @return
		 */
		private String combineUserIP()
		{
			Set<String> set=clients.keySet();
			String string="";
			string=string+SocketProtocol.S_USER_LOGIN;
			for (String user : set) 
			{
				string=string+user+SocketProtocol.SPLIT_SIGN;
			} 
			string=string+SocketProtocol.S_USER_LOGIN;
			TAcontent.append("user list:"+string+"\r\n");
			return string;
		}
		
		public void transmitterClientByKey(String clientIP,String message)
		{
			try 
			{
				DataOutputStream output=clients.getValueByKey(clientIP);
				output.writeUTF(message);
				output.flush();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void transmitterClientByValue(DataOutputStream output,String message)
		{
			try 
			{
				output.writeUTF(message);
				output.flush();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void transmitterBroadcastToClient(String message)
		{
			try
			{
				for(DataOutputStream output:clients.valueSet())
				{
					output.writeUTF(message);
					output.flush();
				}
			}
			catch(IOException e)
			{
			}
		}

		/**
		 * 當使用者斷線時，把連線關閉後再傳送給其他使用者此使用者斷線的消息
		 */
		public void closeSocketClient()
		{
			String userName=clients.getKeyByValue(output);
			updateCBuser(userName,"remove");
			clients.removeByValue(output);
			System.out.println("clients size:"+clients.map.size());
			
			try
			{
				if(output!=null)
					output.close();
				if(input!=null)
					input.close();
				if(this.clientSocket != null || !this.clientSocket.isClosed())
					clientSocket.close();
			}
			catch(IOException ex)
			{
				TAcontent.append(""+ex);
				System.out.println(ex);
				ex.printStackTrace();
			}
			new Thread(new ServerSentThread(SocketProtocol.S_USER_LOGOUT+userName+SocketProtocol.S_USER_LOGOUT)).start();
		}
		
		/**
		 * 將訊息的標頭與標尾去除
		 * @param line=還沒去除訊息
		 * @return=以去除訊息
		 */
		public String getRealMsg(String line)
		{
		    return line.substring(SocketProtocol.PROTOCOL_LEN,line.length()-SocketProtocol.PROTOCOL_LEN);
		}
		
		/**
		 * 更新目前server端的使用者人數
		 * @param User=需要做動作的使用者
		 * @param action=需要做的動作
		 */
		public void updateCBuser(String User ,String action)
	    {
	    	if(action=="add")
	    	{
	    		CBuser.addItem(User);
	        	CBuser.setSelectedIndex(0);
	        	System.out.println(CBuser.getSelectedIndex());
	    	}
	    	else if(action=="remove")
	    	{
	    		CBuser.removeItem(User);
	    	}
	    	CBuser.revalidate();
	    	CBuser.repaint();
	    	panel.revalidate();
	    	panel.repaint();
	    	fame.revalidate();
	    	fame.repaint();
	    }
		
		
		/**
		 * 找到client端傳來的audio file檔來計算past simples
		 * @param roomUserParameter= 房間內每個使用者的參數(IP and file name)
		 * @return=回傳數值
		 */
		public synchronized ArrayList<int[]> findUserPastSimples(SocketRoom socketRoom)
		{
			ArrayList<int[]> I_simplesArray=new ArrayList<int[]>();
			
			ArrayList<RoomClientParameter> roomClientArray=socketRoom.getRoomClientIPArray();
			int roomClientSum=socketRoom.getRoomClientSum();
			
			for(int i=0; i<roomClientArray.size();i++)
			{
				String userAudioNo=roomClientArray.get(i).clientFileAudioNo;
				int userNo=roomClientArray.get(i).clientNo;
				int[] I_simples;
				String audioPath="C:\\xampp\\htdocs\\VoicePosition\\PHP\\uploads\\new"+userAudioNo+".wav";
				try 
				{
					Object[] objects=soundAnalysis.findPastSimples6(1,roomClientSum, userNo,audioPath);
					String S_pastSimples=objects[0].toString();
					S_pastSimples=S_pastSimples.replaceAll("\\s+", "|"); //去除所有空白
					String[] simplesArray= S_pastSimples.split("\\|");
					
					int i_length=simplesArray.length;
					
					I_simples=new int[i_length];
					
					for(int j=0;j<i_length;j++)
					{
						I_simples[j]=Integer.parseInt(simplesArray[j]);
					}
					I_simplesArray.add(I_simples);
				
				} catch (MWException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			for(int i=0;i<I_simplesArray.size();i++)
			{
				for(int j=0;j<I_simplesArray.get(i).length;j++)
				{
					System.out.print(I_simplesArray.get(i)[j]+",");
				}
				System.out.println();
			}
			return I_simplesArray;
		}
		
		
		/**
		 * 計算between clients distance
		 * @param userDistance
		 * @return
		 */
		public ArrayList<int[]> calActualDistance(ArrayList<int[]> userPastSimplesArray)
		{
			ArrayList<int[]> userActualDistanceArray=new ArrayList<int[]>();
			for(int[] iArray:userPastSimplesArray)
			{
				int length=iArray.length;
				userActualDistanceArray.add(new int[length]);
			}
			
			for(int i=0;i<userPastSimplesArray.size();i++)
			{
				for(int j=0;j<userPastSimplesArray.get(i).length;j++)
				{
					System.out.println("test:"+i+j);
					userActualDistanceArray.get(i)[j]=Integer.parseInt(df.format(Math.abs(userPastSimplesArray.get(i)[j]-userPastSimplesArray.get(j)[i])/44100.0/2.0*soundSpeed*100));
				}
			}
			return userActualDistanceArray;
		}
		
		public void insertMySqlTable(String parimaryIP,String secondaryIP,String room,double distance)
		{
			PreparedStatement preparedStatment=null;
			String insertSqlLine="insert into experience7mtable2(no,parimaryIP,secondaryIP,room,distance)"+"select ifNULL(max(no),0)+1,?,?,?,? From experience7mtable2"; 
			System.out.println("parimaryIP:"+parimaryIP+",secondaryIP:"+secondaryIP+",room"+room+",distance"+distance);
			try
			{
				preparedStatment=connection.prepareStatement(insertSqlLine);
				preparedStatment.setString(1, parimaryIP);
				preparedStatment.setString(2, secondaryIP);
				preparedStatment.setString(3, room);
				preparedStatment.setDouble(4, distance);
				preparedStatment.executeUpdate();
			}catch(SQLException e)
			{
				
			}
			finally
			{
				if(preparedStatment!=null)
				{
					try 
					{
						preparedStatment.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}	
		
		public void insertTargetNodeTable(String parimaryIP,String room,String actualX,String actualY,String estimateX,String estimateY,String deviation)
		{

			PreparedStatement preparedStatment=null;
			String insertSqlLine="insert into experience_position_node(no,parimaryIP,roomID,actualX,actualY,estimateX,estimateY,deviation)"+"select ifNULL(max(no),0)+1,?,?,?,?,?,?,? From experience_position_node"; 
			System.out.println("parimaryIP:"+parimaryIP+",room"+room+",actualX"+actualX+",actualY"+actualY+",estimateX"+estimateX+",estimateY"+estimateY+",deviation"+deviation);
			try
			{
				preparedStatment=connection.prepareStatement(insertSqlLine);
				preparedStatment.setString(1, parimaryIP);
				preparedStatment.setString(2, room);
				preparedStatment.setString(3, actualX);
				preparedStatment.setString(4, actualY);
				preparedStatment.setString(5, estimateX);
				preparedStatment.setString(6, estimateY);
				preparedStatment.setString(7, deviation);
				
				preparedStatment.executeUpdate();
			}catch(SQLException e)
			{
				
			}
			finally
			{
				if(preparedStatment!=null)
				{
					try 
					{
						preparedStatment.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}	
		
		public String findTargetNode(String targetNodeDistance,boolean bool)
		{
			//100,0,0.93|100,100,0.451|0,0,1.255|
			String[] nodeArray=targetNodeDistance.split("\\"+SocketProtocol.SPLIT_SIGN);
			
			int[] nodeXArray=new int[nodeArray.length]; 
			int[] nodeYArray=new int[nodeArray.length]; 
			int[] nodeDArray=new int[nodeArray.length]; 
			for(int i=0;i<nodeArray.length;i++)
			{
				String[] parameterArray=nodeArray[i].split(",");
				nodeXArray[i]=Integer.parseInt(parameterArray[0]);
				nodeYArray[i]=Integer.parseInt(parameterArray[1]);
				nodeDArray[i]=Integer.parseInt(parameterArray[2]);
			}
			
			int[][] nodeCoordinate=new int[2][nodeXArray.length];
			nodeCoordinate[0]=nodeXArray;
			nodeCoordinate[1]=nodeYArray;
			
			
			Object[] objects;
			try 
			{
				objects = soundAnalysis.Dist2DLS(1,nodeCoordinate,nodeDArray);
				String S_targetNodeCoordinate=objects[0].toString();
		
				S_targetNodeCoordinate=S_targetNodeCoordinate.replaceAll("\\s+", "|"); //去除所有空白
				System.out.println("S_targetNodeCoordinate:"+S_targetNodeCoordinate);
				String[] S_targetNodeCoordinateArray= S_targetNodeCoordinate.split("\\|");
				
				if(bool==true)
				{
					coordinateSystem.addData(0,Integer.parseInt(S_targetNodeCoordinateArray[1]),Integer.parseInt(S_targetNodeCoordinateArray[2]));
					coordinateSystem.writeJLabel("0", S_targetNodeCoordinateArray[1], S_targetNodeCoordinateArray[2],0);
				}
								
				
				S_targetNodeCoordinate=S_targetNodeCoordinateArray[1]+SocketProtocol.SPLIT_SIGN+S_targetNodeCoordinateArray[2]; //[0]是一個空白
				System.out.println("S_targetNodeCoordinate"+S_targetNodeCoordinate);
				return S_targetNodeCoordinate;
			} catch (MWException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "0";

		}
		
		private String filterTargetNodeDistance(String targetNodeDistance,String locationRange,int checkErrorItem)
		{
			String[] nodeArray=targetNodeDistance.split("\\"+SocketProtocol.SPLIT_SIGN);
			int[][] nodeInformationArray=new int[nodeArray.length][3];
			for(int i=0;i<nodeArray.length;i++)
			{
				String[] nodeInformation=nodeArray[i].split(",");
				for (int j=0;j<nodeInformation.length;j++)
				{
					nodeInformationArray[i][j]=Integer.valueOf(nodeInformation[j]);
				}
				
				
			}
			
			for(int i=0;i<nodeArray.length;i++)
			{
				for(int j=0;j<=i;j++)
				{
					if(nodeInformationArray[i][2]>nodeInformationArray[j][2])
					{
						int nodeX,nodeY,nodeDistance;
						nodeX=nodeInformationArray[i][0];
						nodeY=nodeInformationArray[i][1];
						nodeDistance=nodeInformationArray[i][2];
						
						nodeInformationArray[i][0]=nodeInformationArray[j][0];
						nodeInformationArray[i][1]=nodeInformationArray[j][1];
						nodeInformationArray[i][2]=nodeInformationArray[j][2];
						
						nodeInformationArray[j][0]=nodeX;
						nodeInformationArray[j][1]=nodeY;
						nodeInformationArray[j][2]=nodeDistance;
					}
				}
			}
			
			targetNodeDistance="";
			
			if(nodeArray.length>=4)
			{
				if(nodeInformationArray[0][2]>Integer.valueOf(locationRange)*1.6)
				{
					targetNodeDistance="0"+SocketProtocol.SPLIT_SIGN;
					for(int i=1;i<nodeArray.length;i++)
					{
						targetNodeDistance=targetNodeDistance+nodeInformationArray[i][0]+","+nodeInformationArray[i][1]+","+nodeInformationArray[i][2]+SocketProtocol.SPLIT_SIGN;
					}
				}
				else
				{
					targetNodeDistance="1"+SocketProtocol.SPLIT_SIGN;
					for(int i=0;i<nodeArray.length;i++)
					{
						if(checkErrorItem!=i)
							targetNodeDistance=targetNodeDistance+nodeInformationArray[i][0]+","+nodeInformationArray[i][1]+","+nodeInformationArray[i][2]+SocketProtocol.SPLIT_SIGN;
					}
					
				}
			}
			else
			{
				targetNodeDistance="6"+SocketProtocol.SPLIT_SIGN;
				for(int i=0;i<nodeArray.length;i++)
				{
					targetNodeDistance=targetNodeDistance+nodeInformationArray[i][0]+","+nodeInformationArray[i][1]+","+nodeInformationArray[i][2]+SocketProtocol.SPLIT_SIGN;
				}	
			}
			
			return targetNodeDistance;
		}
		
		private String filterTargetNodeDistance2(String targetNodeDistance,String locationRange,int checkErrorItem)
		{
			String[] nodeArray=targetNodeDistance.split("\\"+SocketProtocol.SPLIT_SIGN);
			int[][] nodeInformationArray=new int[nodeArray.length][3];
			for(int i=0;i<nodeArray.length;i++)
			{
				String[] nodeInformation=nodeArray[i].split(",");
				for (int j=0;j<nodeInformation.length;j++)
				{
					nodeInformationArray[i][j]=Integer.valueOf(nodeInformation[j]);
				}
				
				
			}
			
			for(int i=0;i<nodeArray.length;i++)
			{
				for(int j=0;j<=i;j++)
				{
					if(nodeInformationArray[i][2]>nodeInformationArray[j][2])
					{
						int nodeX,nodeY,nodeDistance;
						nodeX=nodeInformationArray[i][0];
						nodeY=nodeInformationArray[i][1];
						nodeDistance=nodeInformationArray[i][2];
						
						nodeInformationArray[i][0]=nodeInformationArray[j][0];
						nodeInformationArray[i][1]=nodeInformationArray[j][1];
						nodeInformationArray[i][2]=nodeInformationArray[j][2];
						
						nodeInformationArray[j][0]=nodeX;
						nodeInformationArray[j][1]=nodeY;
						nodeInformationArray[j][2]=nodeDistance;
					}
				}
			}
			
			targetNodeDistance="";
			
			if(nodeArray.length>=4)
			{
				if(nodeInformationArray[0][2]>Integer.valueOf(locationRange)*1.6)
				{
					for(int i=1;i<nodeArray.length;i++)
					{
						targetNodeDistance=targetNodeDistance+nodeInformationArray[i][0]+","+nodeInformationArray[i][1]+","+nodeInformationArray[i][2]+SocketProtocol.SPLIT_SIGN;
					}
				}
				else
				{
					for(int i=0;i<nodeArray.length;i++)
					{
						if(checkErrorItem!=i)
							targetNodeDistance=targetNodeDistance+nodeInformationArray[i][0]+","+nodeInformationArray[i][1]+","+nodeInformationArray[i][2]+SocketProtocol.SPLIT_SIGN;
					}
					
				}
			}
			else
			{
				for(int i=0;i<nodeArray.length;i++)
				{
					targetNodeDistance=targetNodeDistance+nodeInformationArray[i][0]+","+nodeInformationArray[i][1]+","+nodeInformationArray[i][2]+SocketProtocol.SPLIT_SIGN;
				}	
			}
			
			return targetNodeDistance;
		}
		
		
		public void insertErrorItemTable(String roomID,int item,String targetNodeX,String targetNodeY)
		{
			PreparedStatement preparedStatment=null;
			String insertSqlLine="insert into error(no,room,item,x,y)"+"select ifNULL(max(no),0)+1,?,?,?,? From error"; 
			try
			{
				preparedStatment=connection.prepareStatement(insertSqlLine);
				preparedStatment.setString(1, roomID);
				preparedStatment.setInt(2, item);
				preparedStatment.setString(3, targetNodeX);	
				preparedStatment.setString(4, targetNodeY);	
				preparedStatment.executeUpdate();
			}catch(SQLException e)
			{
				
			}
			finally
			{
				if(preparedStatment!=null)
				{
					try 
					{
						preparedStatment.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
    }
    
    /**
     * 處理Server端的Sent請求執行緒。
     */
    class ServerSentThread implements Runnable
    {
    	String msg;
    	
    	public ServerSentThread(String msg)
    	{
    		this.msg=msg;
    	}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try
			{
				for(DataOutputStream output:clients.valueSet())
				{
					output.writeUTF(msg);
					output.flush();
				}
			}
			catch(IOException e)
			{
			}
		}
    }
}
