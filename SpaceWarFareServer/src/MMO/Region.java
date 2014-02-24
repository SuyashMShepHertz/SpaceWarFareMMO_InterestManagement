package MMO;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Suyash Mohan
 */
public class Region {
    private Rect m_bound;
    private List<Player> m_subscribers;
    private List<Player> m_players;
    
    public Region(float x, float y, float width, float height)
    {
        m_bound = new Rect();
        m_bound.Size.x = width;
        m_bound.Size.y = height;
        m_bound.Origin.x = x;
        m_bound.Origin.y = y;
        
        m_subscribers = new ArrayList<Player>();
        m_players = new ArrayList<Player>();
    }
    
    public void AddSubscriber(Player p)
    {
        if(m_subscribers.contains(p) == false)
            m_subscribers.add(p);
    }
    
    public void RemoveSubscriber(Player p)
    {
        if(m_subscribers.contains(p) == true)
            m_subscribers.remove(p);
    }
    
    public void AddPlayer(Player p)
    {
        if(m_players.contains(p) == false)
            m_players.add(p);
    }
    
    public void RemovePlayer(Player p)
    {
        if(m_players.contains(p) == true)
            m_players.remove(p);
    }
    
    public List<Player> getPlayers()
    {
        return m_players;
    }
    
    public List<Player> getSubscribers()
    {
        return m_subscribers;
    }
    
    public Rect getRect()
    {
        return m_bound;
    }
}
