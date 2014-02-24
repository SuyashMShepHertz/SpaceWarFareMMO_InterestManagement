package MMO;

import com.shephertz.app42.server.idomain.IUser;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Suyash Mohan
 */
public class Player {
    private VectorF m_position;
    private IUser m_user;
    private VectorF m_aoi;
    private Region m_region;
    private List<Region> m_publishers;
    
    public Player(IUser user)
    {
        m_user = user;
        m_position = new VectorF(0, 0);
        m_aoi = null;
        m_region = null;
        m_publishers = new ArrayList<>();
    }
    
    public Player(IUser user,float x, float y)
    {
        m_user = user;
        m_position = new VectorF(x,y);
        m_aoi = null;
        m_region = null;
        m_publishers = new ArrayList<>();
    }
    
    public Player(IUser user,VectorF pos)
    {
        m_user = user;
        m_position = pos;
        m_aoi = null;
        m_region = null;
        m_publishers = new ArrayList<>();
    }
    
    public void setInterestAreaSize(VectorF rc)
    {
        m_aoi = rc;
    }
    
    public void setInterestAreaSize(float x, float y, float width, float height)
    {
        m_aoi = new VectorF();
        m_aoi.x = x;
        m_aoi.y = y;
    }
    
    public VectorF getInterestAreaSize()
    {
        return m_aoi;    
    }
    
    public IUser getIUser()
    {
        return m_user;
    }
    
    public VectorF getPosition()
    {
        return m_position;
    }
    
    public void setPosition(float x, float y)
    {
        m_position.x = x;
        m_position.y = y;
    }
    
    public Region getRegion()
    {
        return m_region;
    }
    
    public void setRegion(Region rgn)
    {
        m_region = rgn;
    }
    
    public List<Region> getPublishers()
    {
        return m_publishers;
    }
    
    public String getName()
    {
        return m_user.getName();
    }
}
