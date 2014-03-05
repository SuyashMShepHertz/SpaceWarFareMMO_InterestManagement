/**
 *
 * @author Suyash Mohan
 */
package spacewarfare;

import MMO.Player;
import MMO.VectorF;
import MMO.VectorI;
import MMO.World;
import com.shephertz.app42.server.idomain.BaseRoomAdaptor;
import com.shephertz.app42.server.idomain.HandlingResult;
import com.shephertz.app42.server.idomain.IRoom;
import com.shephertz.app42.server.idomain.IUser;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class SpaceRoom extends BaseRoomAdaptor{

    private DragonUser dragon;
    private IRoom m_room;
    private Long ticks;

    private World world;

    public SpaceRoom(IRoom room) {
        m_room = room;
        dragon = new DragonUser();
        dragon.SetPosition(50,50);
        ticks = 0L;

        world = new World(m_room, new VectorF(0,0), new VectorF(800,480),new VectorI(10,10));
    }

    @Override
    public void handleUserJoinRequest(IUser user, HandlingResult result){
        System.out.println("User Joined " + user.getName());
    }
    
    @Override
    public void onUserLeaveRequest(IUser user){
        Player p = world.getPlayer(user);
        world.removePlayer(p);
    }

    @Override
    public void handleChatRequest(IUser sender, String message, HandlingResult result)
    {
        //System.out.println(sender.getName() + " says " + message);

        try
        {
            JSONObject json = new JSONObject(message);
            if(json.getInt("type") == MessageType.MESSAGE_MOVE)
            {

                int x = json.getInt("x");
                int y = json.getInt("y");
                
                List<Player> removedForPlayers = new ArrayList<>();
                List<Player> removedPlayers = new ArrayList<>();
                List<Player> addedPlayers = new ArrayList<>();
                        
                result.sendNotification = false;
                
                Player p = world.getPlayer(sender);
                if(p == null)
                {
                    p = new Player(sender,x,y);
                    p.setInterestAreaSize(new VectorF(80*2,48*2));
                    world.addPlayer(p);
                }
                else
                {
                    p.setPosition((float)x, (float)y);
                    world.updatePlayer(p, removedForPlayers, removedPlayers, addedPlayers);
                }
                
                world.multicastMessage(p, message);
                world.multicastMessage(p, removedForPlayers, buildLeaveRegionMsg(p.getName()));
                for(Player rp:removedPlayers)
                {
                    world.sendMessage(p, buildLeaveRegionMsg(rp.getName()), rp);
                }
                for(Player ap:addedPlayers)
                {
                    world.sendMessage(p, buildMovePlayerMsg(ap), ap);
                }
            }
            else if(json.getInt("type") == MessageType.MESSAGE_HIT && json.getString("p").equals("dragon")){
                if(dragon.ReduceHealth() <= 0){
                    m_room.BroadcastChat("dragon", buildKillDragonMsg());
                }

            }
        }
        catch(JSONException e){

        }
    }

    @Override
    public void onTimerTick(long time) {
        if(time - ticks > 1000){
            ticks = time;

            if(dragon.GetHealth() > 0)
            {
                dragon.MoveRandomStep(25,25,800-50, 480-50);
                //m_room.BroadcastChat("dragon", buildMoveDragonMsg);
            }
            else{
                dragon.Spawn(20);
                dragon.SetPosition(50, 50);
            }
        }
    }

    private String buildLeaveRegionMsg(String userName)
    {
        JSONObject tobeSent = new JSONObject();
        try
        {
            tobeSent.put("name", userName);
            tobeSent.put("type", MessageType.MESSAGE_LEFT_REGION);
        }
        catch(JSONException ex)
        {
        }

        return tobeSent.toString();
    }

    private String buildMoveDragonMsg()
    {
        JSONObject tobeSent = new JSONObject();
        try {
            tobeSent.put("name", "Dragon");
            tobeSent.put("type", MessageType.MESSAGE_MOVE);
            tobeSent.put("x", dragon.GetX());
            tobeSent.put("y", dragon.GetY());
            tobeSent.put("health", dragon.GetHealth());
        } catch (JSONException ex) {
        }
        return tobeSent.toString();
    }
    
    private String buildMovePlayerMsg(Player p)
    {
        JSONObject tobeSent = new JSONObject();
        try {
            tobeSent.put("name", p.getName());
            tobeSent.put("type", MessageType.MESSAGE_MOVE);
            tobeSent.put("x", p.getPosition().x);
            tobeSent.put("y", p.getPosition().y);
        } catch (JSONException ex) {
        }
        return tobeSent.toString();
    }
    
    private String buildKillDragonMsg()
    {
        JSONObject tobeSent = new JSONObject();
        try {
            tobeSent.put("name", "Dragon");
            tobeSent.put("type", MessageType.MESSAGE_DEATH);
        } catch (JSONException ex) {
        }

        return tobeSent.toString();
    }
}
