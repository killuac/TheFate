/**
 * 
 */


import java.util.Random;

import com.electrotank.electroserver5.client.ElectroServer;
import com.electrotank.electroserver5.client.api.EsConnectionResponse;
import com.electrotank.electroserver5.client.api.EsJoinRoomEvent;
import com.electrotank.electroserver5.client.api.EsLoginRequest;
import com.electrotank.electroserver5.client.api.EsLoginResponse;
import com.electrotank.electroserver5.client.api.EsMessageType;
import com.electrotank.electroserver5.client.api.EsQuickJoinGameRequest;
import com.electrotank.electroserver5.client.api.EsUpdateUserVariableRequest;
import com.electrotank.electroserver5.client.api.EsUserVariable;
import com.electrotank.electroserver5.client.extensions.api.value.EsObject;
import com.electrotank.electroserver5.client.user.User;

/**
 * @author Killua
 *
 */
public class Client {

	private static final String xmlPath = "./settings.xml";
	private ElectroServer es = new ElectroServer();
	private String gameType;
    
    public Client(String gameType) {
    	this.gameType = gameType;
    	
    	es.getEngine().addEventListener(EsMessageType.ConnectionResponse, this, "onConnectionResponse", EsConnectionResponse.class);
        es.getEngine().addEventListener(EsMessageType.LoginResponse, this, "onLoginResponse", EsLoginResponse.class);
        es.getEngine().addEventListener(EsMessageType.JoinRoomEvent, this, "onJoinRoomEvent", EsJoinRoomEvent.class);
        
        try {
        	es.loadAndConnect(xmlPath);
        } catch (Exception ex) {
        	System.out.println("loadAndConnect exception: " + ex.getMessage());
        }
    }
    
    public void onConnectionResponse(EsConnectionResponse e) {
        if (e.isSuccessful()) {
            EsLoginRequest lr = new EsLoginRequest();
            lr.setUserName(generateNPCName());
            EsObject esObj = new EsObject();
            esObj.setBoolean("isNPC", true);
            lr.setEsObject(esObj);
            es.getEngine().send(lr);
        }
    }
    
	private String generateNPCName() {
		String charSet = "ABCDEFGHIGKLMNOPQRSTUVWXYZ";
		Random random = new Random(System.currentTimeMillis());
		String alpha = String.valueOf(charSet.charAt(random.nextInt(charSet.length())));
		int numeric = (int) (random.nextDouble() * 1000000000);
		return alpha + numeric;
	}
    
    public void onLoginResponse(EsLoginResponse e) {
        if (e.isSuccessful()) {
        	System.out.println("You are logged in as: " + e.getUserName());
        	this.sendQuickJoinGameRequest();
        } else {
        	System.out.println("Login failed: " + e.getError().toString());
        }
    }
    
    public void sendQuickJoinGameRequest() {
    	EsQuickJoinGameRequest qjgr = new EsQuickJoinGameRequest();
    	qjgr.setGameType(gameType);
    	qjgr.setZoneName(gameType);
    	qjgr.setHidden(false);
    	qjgr.setLocked(false);
    	qjgr.setCreateOnly(false);
    	es.getEngine().send(qjgr);
    }
    
    public void onJoinRoomEvent(EsJoinRoomEvent e) {
    	this.sendUpdateUserStatusVariableRequest();
    }
    
    public void sendUpdateUserStatusVariableRequest() {
    	User user = es.getManagerHelper().getUserManager().getMe();
    	EsUserVariable userVal = user.userVariableByName("userStatus");
    	
    	EsUpdateUserVariableRequest uuvr = new EsUpdateUserVariableRequest();
    	uuvr.setName(userVal.getName());
    	EsObject esObj = userVal.getValue();
    	esObj.setBoolean("isReady", true);
    	uuvr.setValue(esObj);
    	es.getEngine().send(uuvr);
    }
    
	/**
	 * Main method
	 */
	public static void main(String[] args) {
		String gameType = (args.length > 0) ? args[0] : "FateNewbie";
		new Client(gameType);
	}
	
}
