package MMO;

/**
 *
 * @author Suyash Mohan
 */
public class Rect {
    public VectorF Origin;
    public VectorF Size;
    
    public Rect()
    {
        Origin = new VectorF();
        Size = new VectorF();
    }
    
    public Rect(float x, float y, float width, float height)
    {
        Origin.x = x;
        Origin.y = y;
        Size.x = width;
        Size.y = height;
    }
}
