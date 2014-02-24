package MMO;

import com.shephertz.app42.server.idomain.IRoom;
import com.shephertz.app42.server.idomain.IUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Suyash Mohan
 */
public class World {
    private IRoom m_room;
    private Region m_regions[][];
    private HashMap<IUser,Player> m_players;
    private VectorF m_tl;
    private VectorF m_br;
    private VectorI m_subdivisions;
    private VectorF m_divisionArea;
    
    public World(IRoom room,VectorF TopLeft, VectorF BottomRight, VectorI SubDivisions)
    {
        m_room = room;
        m_tl = TopLeft;
        m_br = BottomRight;
        m_subdivisions = SubDivisions;
        m_regions = new Region[(int)m_subdivisions.x][(int)m_subdivisions.y];
        m_players = new HashMap<>();
        
        m_divisionArea = new VectorF();
        m_divisionArea.x = Math.abs((m_br.x - m_tl.x) / m_subdivisions.x); 
        m_divisionArea.y = Math.abs((m_tl.y - m_br.y) / m_subdivisions.y);
        
        int i = 0, j;
        for(float x = m_tl.x; x < m_br.x; x+=m_divisionArea.x)
        {
            j = 0;
            for(float y = m_tl.y; y < m_br.y; y+=m_divisionArea.y)
            {
                Region rgn = new Region(x, y, m_divisionArea.x, m_divisionArea.y);
                m_regions[i][j] = rgn;
                j = j + 1;
            }
            i = i + 1;
        }
    }
    
    public Region findRegion(float x, float y)
    {
        int i = (int)Math.floor((x - m_tl.x)/m_divisionArea.x);
        int j = (int)Math.floor((y - m_tl.y)/m_divisionArea.y);
        
        return m_regions[i][j];
    }
    
    public void addPlayer(Player p)
    {
        m_players.put(p.getIUser(), p);
        updatePlayer(p,null,null,null);
    } 
    
    public void removePlayer(Player p)
    {
        try
        {
            Region curRgn = p.getRegion();
            if(curRgn != null)
            {
                curRgn.RemovePlayer(p);
            }
            for(Region rgn:p.getPublishers())
            {
                rgn.RemoveSubscriber(p);
            }
            m_players.remove(p.getIUser());
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }
    
    public Player getPlayer(IUser user)
    {
        if(m_players.containsKey(user) == true)
            return m_players.get(user);
        
        return null;
    }
    
    public void updatePlayer(Player p, List<Player> removedForPlayers, List<Player> removedPlayers, List<Player> addedPlayers)
    {
        try
        {
            List<Region> rgns = new ArrayList<>(p.getPublishers());
            List<Region> rgns_copy = new ArrayList<>(rgns);
            
            p.getPublishers().clear();
            updateInterestRegions(p);
            
            Region newRgn = findRegion(p.getPosition().x, p.getPosition().y);
            Region prevRgn = p.getRegion();
            p.setRegion(newRgn);
            if(prevRgn != null)
                prevRgn.RemovePlayer(p);
            newRgn.AddPlayer(p);
            if(removedForPlayers != null)
            {
                if(newRgn != prevRgn && prevRgn != null)
                {
                    removedForPlayers.addAll(removedFromRegion(prevRgn.getSubscribers(), newRgn));
                }
            }
            
            rgns.retainAll(p.getPublishers());
            rgns_copy.removeAll(rgns);
            for(Region r : rgns_copy)
            {
                if(removedPlayers != null)
                    removedPlayers.addAll(r.getPlayers());
                r.RemoveSubscriber(p);
            }
            
            if(addedPlayers != null)
            {
                List<Region> newRgns_copy = new ArrayList<>();
                newRgns_copy.addAll(p.getPublishers());
                newRgns_copy.removeAll(rgns);
                for(Region nr: newRgns_copy)
                {
                    addedPlayers.addAll(nr.getPlayers());
                }
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    private void updateInterestRegions(Player p)
    {
        VectorF aoi = p.getInterestAreaSize();
        VectorF pos = p.getPosition();
            
        if (aoi != null) 
        {
            int x1 = (int) Math.floor((pos.x - aoi.x / 2 - m_tl.x) / m_divisionArea.x);
            int x2 = (int) Math.floor((pos.x + aoi.x / 2 - m_tl.x) / m_divisionArea.x);
            int y1 = (int) Math.floor((pos.y - aoi.y / 2 - m_tl.y) / m_divisionArea.y);
            int y2 = (int) Math.floor((pos.y + aoi.y / 2 - m_tl.y) / m_divisionArea.y);

            x1 = x1 < 0 ? 0 : x1;
            x2 = x2 >= m_subdivisions.x ? m_subdivisions.x - 1 : x2;
            y1 = y1 < 0 ? 0 : y1;
            y2 = y2 >= m_subdivisions.y ? m_subdivisions.y - 1 : y2;

            for (int i = x1; i <= x2; ++i) {
                for (int j = y1; j <= y2; ++j) {
                    m_regions[i][j].AddSubscriber(p);
                    p.getPublishers().add(m_regions[i][j]);
                }
            }
        }
    }
    
    public void multicastMessage(Player p, String msg)
    {
        IUser user = p.getIUser();
        Region rgn = p.getRegion();
        List<Player> remotePlayers = rgn.getSubscribers();
        
        for(Player rp: remotePlayers)
        {
            rp.getIUser().SendChatNotification(user.getName(), msg, m_room);
        }
    }
    
    public void multicastMessage(Player sender, List<Player> receivers, String msg)
    {
        IUser user = sender.getIUser();
        for(Player rp: receivers)
        {
            rp.getIUser().SendChatNotification(user.getName(), msg, m_room);
        }
    }
    
    public void sendMessage(Player receiver, String message, Player sender)
    {
        if(sender == null)
            sender = receiver;
        
        receiver.getIUser().SendChatNotification(sender.getIUser().getName(), message, m_room);
    }
    
    private List<Player> removedFromRegion(List<Player> players, Region rgn)
    {
        List<Player> removedPlayers = new ArrayList<>();
        for(Player p : players)
        {
            if(p.getPublishers().contains(rgn) == false)
            {
                removedPlayers.add(p);
            }
        }
        
        return removedPlayers;
    }
}
