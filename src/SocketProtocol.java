public interface SocketProtocol
{
	//定義協議字串長度
	int PROTOCOL_LEN=2;
	
	//定義協議溝通內容
	String SPLIT_SIGN="|";  //切割字串用
	String C_USER_ROUND="@#"; //使用者第一次登入Server給的訊息
	String S_NAME_REP="-1";  //回應使用者登入失敗
	String S_LOGIN_SUCCESS="1"; //回應使用者登入成功

	String C_PRIVATE_ROUND="#$"; //某使用者想私訊給某使用者
	String S_PRIVATE_ROUND="#$"; //某使用者想私訊給某使用者
	
	String S_USER_LOGIN="%^"; //當使用者登入近來，發送給其他使用者說此使用者登入做紀錄
	String S_USER_LOGOUT="^&"; //當使用者登出時，Server發送給其他使用者說此使用者以登出
	
	String C_HEART_BEAT="&*"; //做心跳溝通用
	String S_HEART_BEAT="&*"; //做心跳溝通用
	
	String C_FIND_ASSISTANT_AND_TRANSMIT="*("; //公開做廣播，傳至每個使用者，有人開始要發射訊息
	
	String S_GIVEN_ROOMID_AND_NO="(*";
	String S_RETURN_NODE_POSITION="%$";
	String S_USER_START_SOUND_TRACK="##"; // 告訴使用者要發出聲音了
	String C_USER_END_SOUND_TRACK="$$"; //使用者告知結束發射訊息了
	
	String C_USER_START_SOUND_RECORD="^^"; //使用者回傳開始錄製聲音
	String S_USER_STOP_SOUND_RECORD="%%"; //告訴使用者停止錄製聲音
	
	String C_FILE_UPLOAD_COMPLETED="@@"; //當檔案傳輸完畢的時候，回傳給Server說結束了
	
	String S_SUCCESS_CALCULATE_COORDINATE="@!";
	
	String SUCCESS_CALCULATE_DISTANCE="!!";//成功計算出使用者與使用者之間的距離，並且將訊息回傳給房間內的使用者
	
	String C_INSERT_TARGET_NODE_DB="@%";
	
	String C_MSG_ROUND="!@"; //訊息回應
	String S_MSG_ROUND="!@"; //訊息回應
}