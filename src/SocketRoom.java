import java.util.ArrayList;


/**
 * ��n�p��Z���ɡA�N�ϥΪ̥[�J�ж���
 * @author admin
 *
 */
public class SocketRoom 
{
	private String roomID;
	private int roomClientSum;
	private ArrayList<RoomClientParameter> roomClientIPArray=new ArrayList<RoomClientParameter>();
	private int completedUserSum=0;
	public int clientTransmitterSum=0;
	public int soundRecordSum=0;
	public void setRoomID(String roomID)
	{
		this.roomID=roomID;
	}
	public String getRoomID()
	{
		return roomID;
	}
	public void setRoomClientSum(int roomClientSum)
	{
		this.roomClientSum=roomClientSum;
	}
	
	public int getRoomClientSum()
	{
		return roomClientSum;
	}
	
	public void setRoomClientArray(String clientIP,String clientFileAudioNo,int clientNo)
	{
		RoomClientParameter roomUserParameter=new RoomClientParameter();
		roomUserParameter.clientIP=clientIP;
		roomUserParameter.clientFileAudioNo=clientFileAudioNo;
		roomUserParameter.clientNo=clientNo;
		roomClientIPArray.add(roomUserParameter);
	}

	public boolean recordCompletedClient()
	{
		completedUserSum+=1;
		if(completedUserSum==roomClientSum)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public ArrayList<RoomClientParameter> getRoomClientIPArray()
	{
		return roomClientIPArray;
	}
}
class RoomClientParameter
{
	public String clientIP;
	public int clientNo;
	public String clientFileAudioNo;
	public boolean whetherTransmit=false;
	public String role;// 0=target node,1,2,3=node
	public String nodeX,nodeY;
	public double distance;
	
}
