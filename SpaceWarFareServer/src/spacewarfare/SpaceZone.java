/**
 *
 * @author Suyash Mohan
 */
package spacewarfare;

import com.shephertz.app42.server.idomain.BaseZoneAdaptor;
import com.shephertz.app42.server.idomain.HandlingResult;
import com.shephertz.app42.server.idomain.IRoom;
import com.shephertz.app42.server.idomain.IUser;
import java.util.HashMap;

public class SpaceZone extends BaseZoneAdaptor {
    private HashMap<IRoom,SpaceRoom> spacerooms;
    
    public SpaceZone()
    {
        spacerooms = new HashMap<>();
    }
    
    @Override
    public void onAdminRoomAdded(IRoom room)
    {
        System.out.println("Room Creatd " + room.getName());
        SpaceRoom spaceroom = new SpaceRoom(room);
        room.setAdaptor(spaceroom);
        spacerooms.put(room, spaceroom);
    } 
    
    @Override
    public void handleAddUserRequest(IUser user, String authString, HandlingResult result)
    {
        System.out.println("UserRequest " + user.getName());
    }   
    
    @Override
    public void onUserRemoved(IUser user) {
        System.out.println("User Removed " + user.getName());
        spacerooms.get(user.getLocation()).onUserLeaveRequest(user);
    }
}
